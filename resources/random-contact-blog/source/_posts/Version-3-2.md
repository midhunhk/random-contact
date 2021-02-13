---
title: Version-3.2
date: 2018-07-07 06:55:51
tags:
---
<img alt="Random Contact" src="/random-contact/images/feature_graphic_v2.png"/>
Version 3.2 codenamed 'blackbird' is complete and available for download.

The changelog for this release are below.

 - Target latest Android Version
 - Respect Runtime Permissions
 - Handle No Contacts Case

### Runtime Permissions
The reason for not using the latest Android API version for compiling this app was Runtime Permissions introduced with 
Android M. Finally I found some time to put in the effort to implement this change. Most of the examples for making this 
change are simple use cases or basics, so it took some trial and effort to make it work for this app.

A new reusable Fragment for checking the permission which contains some boilerplate code is created and would be 
soon available with the next version of libAeApps.

### No Contacts View
The results of Alpha testing done by Google Play used to fail as the emulators ddidn't contain any contacts. In practice, 
this edge scenario should only come when the app is installed on a new device. Neverthless, all tests in Google Play 
pre release are passing now.

Download from the <a href="https://play.google.com/store/apps/details?id=com.ae.apps.randomcontact">Play Store</a> now.