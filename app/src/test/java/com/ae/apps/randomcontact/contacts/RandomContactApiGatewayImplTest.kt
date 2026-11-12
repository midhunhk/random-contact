package com.ae.apps.randomcontact.contacts

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.os.Handler
import android.os.Looper
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.api.contacts.impl.ContactsApiGatewayImpl
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer
import com.ae.apps.randomcontact.preferences.AppPreferences
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockedConstruction
import org.mockito.MockedStatic
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockConstruction
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * Reproduces the production crash
 *
 *     java.lang.IllegalStateException
 *       at AbstractContactsApiGateway.throwExceptionIfNotInitialized
 *       at AbstractContactsApiGateway.getReadContactsCount
 *       at RandomContactApiGatewayImpl.getReadContactsCount
 *       at RandomContactApiGatewayImpl.onContactsRead
 *       at android.os.Handler.handleCallback
 *
 * which happens when initializeAsync() is called a second time while the contacts are still being
 * read, as two RandomContactFragment instances do on launch.
 *
 * The real library gateway is used. Only the Android pieces underneath it are replaced:
 *  - the ContentResolver query is held open so the test controls how long "reading" takes
 *  - Handler.post() is captured into [mainLooperQueue], which the test drains the way the main
 *    looper would
 */
class RandomContactApiGatewayImplTest {

    private companion object {
        const val TIMEOUT_SECONDS = 5L
        const val CONTACT_COUNT = 3
        val COLUMNS = listOf("_id", "display_name", "has_phone_number")
        val OPTIONS: ContactInfoFilterOptions = ContactInfoFilterOptions.of(true)
    }

    private val mainLooperQueue = LinkedBlockingQueue<Runnable>()
    private val readStarted = CountDownLatch(1)
    private val allowReadToFinish = CountDownLatch(1)

    private lateinit var mockedHandlers: MockedConstruction<Handler>
    private lateinit var mockedLooper: MockedStatic<Looper>
    private lateinit var gateway: ContactsApiGateway

    @Before
    fun setUp() {
        // Callbacks the library posts to the main thread end up in mainLooperQueue
        mockedHandlers = mockConstruction(Handler::class.java) { handler, _ ->
            `when`(handler.post(any())).thenAnswer { invocation ->
                mainLooperQueue.add(invocation.getArgument(0))
                true
            }
        }
        mockedLooper = mockStatic(Looper::class.java)

        val contentResolver = mock(ContentResolver::class.java)
        `when`(contentResolver.query(any(), any(), any(), any(), any())).thenAnswer {
            // Reading contacts is slow on a device with many contacts: hold the query open
            readStarted.countDown()
            check(allowReadToFinish.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) { "Test never released the read" }
            contactsCursor()
        }
        val context = mock(Context::class.java)
        `when`(context.contentResolver).thenReturn(contentResolver)
        `when`(context.resources).thenReturn(mock(Resources::class.java))

        gateway = RandomContactApiGatewayImpl(
            mock(ContactGroupRepository::class.java),
            ContactsApiGatewayImpl.Builder(context).build(),
            mock(AppPreferences::class.java)
        )
    }

    @After
    fun tearDown() {
        allowReadToFinish.countDown()
        mockedHandlers.close()
        mockedLooper.close()
    }

    @Test
    fun secondInitializeAsyncWhileContactsAreStillBeingRead_doesNotCrash() {
        val firstFragment = mock(ContactsDataConsumer::class.java)
        val secondFragment = mock(ContactsDataConsumer::class.java)

        // The first fragment starts loading contacts...
        gateway.initializeAsync(OPTIONS, firstFragment)
        assertTrue("Contacts read never started", readStarted.await(TIMEOUT_SECONDS, TimeUnit.SECONDS))

        // ...and a second fragment asks for the same thing before the read has finished
        gateway.initializeAsync(OPTIONS, secondFragment)

        // Let the main thread run whatever has been posted so far. On affected library versions
        // the second request has already posted its callback although nothing has been read,
        // and running it throws IllegalStateException from RandomContactApiGatewayImpl.onContactsRead.
        runMainLooper(expectedCallbacks = 0, waitMillis = 500)

        // The read completes; both requests should now call back
        allowReadToFinish.countDown()
        runMainLooper(expectedCallbacks = 2, waitMillis = TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS))

        assertEquals(CONTACT_COUNT.toLong(), gateway.readContactsCount)
        assertEquals(CONTACT_COUNT, gateway.allContacts.size)
        verify(secondFragment, atLeastOnce()).onContactsRead()
    }

    /**
     * Runs posted callbacks on this thread, as the main looper would, until [expectedCallbacks]
     * have run or [waitMillis] has passed. Returns the number of callbacks that ran.
     */
    private fun runMainLooper(expectedCallbacks: Int, waitMillis: Long): Int {
        val deadline = System.currentTimeMillis() + waitMillis
        var executed = 0
        while (true) {
            if (expectedCallbacks in 1..executed) break
            val remaining = deadline - System.currentTimeMillis()
            if (remaining <= 0) break
            val callback = mainLooperQueue.poll(remaining, TimeUnit.MILLISECONDS) ?: break
            callback.run()
            executed++
        }
        if (expectedCallbacks > 0) {
            assertEquals("Callbacks delivered to the main thread", expectedCallbacks, executed)
        }
        return executed
    }

    /** A cursor over [CONTACT_COUNT] contacts that all have a phone number. */
    private fun contactsCursor(): Cursor {
        val cursor = mock(Cursor::class.java)
        var row = -1
        `when`(cursor.count).thenReturn(CONTACT_COUNT)
        `when`(cursor.moveToNext()).thenAnswer { ++row < CONTACT_COUNT }
        `when`(cursor.getColumnIndex(anyString())).thenAnswer { COLUMNS.indexOf(it.getArgument<String>(0)) }
        `when`(cursor.getString(anyInt())).thenAnswer {
            when (it.getArgument<Int>(0)) {
                0 -> "${row + 1}"
                1 -> "Contact ${row + 1}"
                else -> "1"
            }
        }
        return cursor
    }
}
