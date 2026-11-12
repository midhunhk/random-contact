package com.ae.apps.randomcontact

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Guards manifest entries whose absence does not fail the build but breaks a feature at runtime.
 */
class AppManifestTest {

    private companion object {
        const val ANDROID_NS = "http://schemas.android.com/apk/res/android"
        // Unit tests run with the module directory as the working directory
        val MANIFEST = File("src/main/AndroidManifest.xml")
    }

    private val manifest: Element by lazy {
        val factory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
        factory.newDocumentBuilder().parse(MANIFEST).documentElement
    }

    /**
     * From Android 11 another app is invisible unless it is declared in <queries>. Without this,
     * the WhatsApp button is never shown and the WhatsApp chat intent never resolves.
     */
    @Test
    fun whatsAppIsDeclaredInQueries() {
        val queriedPackages = manifest.childElements("queries")
            .flatMap { it.childElements("package") }
            .map { it.getAttributeNS(ANDROID_NS, "name") }

        assertTrue(
            "AndroidManifest.xml must declare <queries><package android:name=\"com.whatsapp\"/></queries>",
            "com.whatsapp" in queriedPackages
        )
    }

    @Test
    fun intentFiltersOnlyUseValidActionNames() {
        val actions = manifest.getElementsByTagName("action").let { nodes ->
            (0 until nodes.length).map { (nodes.item(it) as Element).getAttributeNS(ANDROID_NS, "name") }
        }

        actions.forEach { action ->
            assertFalse("Invalid action name: $action", action.contains('-'))
        }
    }

    private fun Element.childElements(tagName: String): List<Element> {
        val nodes = childNodes
        return (0 until nodes.length)
            .map { nodes.item(it) }
            .filterIsInstance<Element>()
            .filter { it.tagName == tagName }
    }
}
