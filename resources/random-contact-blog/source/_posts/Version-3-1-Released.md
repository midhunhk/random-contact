---
title: Version 3.1 Released
date: 2018-04-08 00:27:05
tags:
---
<img alt="Random Contact" src="/random-contact/images/feature_graphic_v2.png"/>
Version 3.1 codenamed 'telephone' is complete and available for download.

The changelog for this release are below.

 - Friendlier last contacted date
 - WhatsApp integration from contact method options
 - Landscape orientation for Random Contact screen

### Friendlier last contact date
For showing the last contacted date in a better readable format, we have used the <a href="https://developer.android.com/reference/android/text/format/DateUtils.html#getRelativeDateTimeString(android.content.Context,%20long,%20long,%20long,%20int)">DateUtils.getRelativeDateTimeString()</a> utility method from Android itself.

### WhatsApp integration
This was a tough feature for implementing as many of the attempts didn't work as well as some methods not supported by the WhatsApp app itself. Still some code cleanup and moving some code to `lib-ae-apps` is pending.

### Landscape orientation
Random Contact did support a different layout for Landscape in the past, but was disregarded while doing the redesign. This release provides a better and accessible Landscape layout

Download from the <a href="https://play.google.com/store/apps/details?id=com.ae.apps.randomcontact">Play Store</a> now.