# In-App DevTools [![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=library&style=plastic)](https://bintray.com/rafaco/InAppDevTools/inappdevtools/_latestVersion) [![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=plugin&colorB=blue?style=plastic)](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) [![Maturity](https://img.shields.io/badge/maturity-experimental-red.svg?style=plastic)](https://github.com/rafaco/InAppDevTools/commits)

**In-App DevTools is a set of developer tools that can be added to your debug compilation as a library. It's like Chrome DevTools but for Android apps and runnning on the same screen (no cable, on the go!). Auto-logger, crash handler, source browser, layout inspector, storage editor, logcat viewer, info panels, flexible reports, classs/method tracker, coding helpers and much more.**

- Inspectors: sources, logs, view layout, edit your storage (db, SharedPrefs and Files) and info panels
- Auto generate a FriendlyLog with basic reproduction steps as well as advanced entries (lifecycle events, network requests, errors, device events,...)
- See a crash detail immediately and navigate to the causing source lines.
- Send flexible reports by email or other apps
- Customize your own tools, easily run your task and use our dev helpers.
- Easy to install and configurable

# Screenshots
//TODO

## Demo App <a name="sample"/>
It will be available to download from GooglePlay, starting from version 1.0.

## Installation <a name="setup"/>

### Limitations <a name="req"/>
- minSdkVersion >= 16 (Jelly Bean). Check it at your app/build.gradle
- If AndroidX is enabled: Jetifier should also be enabled. Check it at your gradle.properties

### Basic set-up <a name="basic"/>
For standard Android projects you only need to modify 2 gradle files. Then build your app and shake it!

1. On your root build.gradle, import our plugin (plugins closure must be just after buidscript). Then add the JitPack repository (TEMP, is a transitive dependency):

```gradle
plugins {
    id "es.rafaco.inappdevtools" version "[PLUGIN_VERSION]"
}

allprojects {
    repositories {
        maven { url "https://jitpack.io"}
    }
}
```

2. On your app build.gradle, include our library in your dependencies: 

```gradle
dependencies {
    implementation 'es.rafaco.inappdevtools:inappdevtools:[LIBRARY_VERSION]'
}
```

Latest versions are (don´t include the "v" character):
![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=[PLUGIN_VERSION]&colorB=blue&style=flat-square) and 
![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=[LIBRARY_VERSION]&style=flat-square) 

### Add network interceptor <a name="network"/>
If your app use Retrofit, we can record all network network communications for you, allowing you to inspect and report them. To enable it, add our OkHttpClient to your api initialization class:

```java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(DevTools.getOkHttpClient())
                .build();
```

## Usage <a name="usage"/>

### Invocation <a name="invocation"/>
### Configuration <a name="configuration"/>
You can configure our library behaviour using our gradle extension on your app module's build.gradle.
```gradle
inappdevtools {
    enabled = true
    email = 'yourmail@yourdomain.com'
}
```

Available properties:

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `enabled` | boolean | true | Disable all if set false |
| `debug` | boolean | false | Additional logs from our library and our plugin  |
| `email` | String | null | Default email to use for reports |
| `overlay_enabled` | boolean | true | Disable our overlay UI  |
| `invocation_by_shake` | boolean | true | Disable opening our UI on device shake  |
| `invocation_by_icon` | boolean | false | Enable a permanent overlay icon to open our UI  |
| `invocation_by_notification` | boolean | true | Disable showing our notification to open the UI  |
| `call_default_crash_handler` | boolean | false | Propagate unhandler exceptions to the default handler (crashalitics)  |

## Features <a name="features"/>

### Overlay system <a name="overlay"/>
We use overlays to show information over your app instead of activities
- Adjust any of our views over your app while you still using it
- Non intrusive with your views their focus or your activity stack
- Few invocation methods available: Notification, floating icon, shake or from your sources (see integrations)

### Friendly Log <a name="friendly"/>
- User interactions:
  - Gestures: Tap, double tap, long press and fling
  - Buttons clicked: back, home, recent, power, volume
  - Device shake
- Application events:
  - App to Foreground and to background
  - Navigation base on activity or fragments
  - Errors: Crash and ANRs
- Lifecycle events:
  - Application: create, start, stop, resume, paused, destroy 
  - Activities: created, started, resumed, paused, stop, save and destroy.
  - Fragments: preAttached, attached, created, activityCreated, viewCreated, started, resumed, paused, stop, save, viewDestroy, destroy and detached.
- Device events
  - Connectivity: connected/disconnected with network type and speed, airplane mode switch.
  - Screen rotation

### Crash handler <a name="crash"/>
- Store detailed info with stacktrace, screenshot and recent logcat
- Restart your app and show all details and allow to report
- Restart without details can be configured


### Inspector <a name="inspector"/>
- Inspect Sources:
  - See your sources included in this apk, with your comments.
  - Java sources, generated sources and resource folder
  - Extremely useful to go to the crash line!
- Inspect Logs:
  - View standard Logcat with our Friendly Log 
  - Select verbosity, filter results or search by keywords
  - Powered ups with links to sources, crash details, screenshots....
- Inspect View
  - Navigate through the layout hierarchy and edit his properties
  - Select an element directly on your view to view his properties
  - Show a grid to check alignments
  - Get distances between elements on the screen just by touching them
- Inspect Storage 
  - Databases: all in your app
  - SharedPrefs
  - Files
- Get infos
  - Application status
  - Apk compilation info
  - OS, device, hardware, memory

### Reports <a name="reports"/>
- Report bugs by email or share them with your favourite app
- Attach logs, info, description, crashes, db dumps…
- Take screenshots and attach them
- //TODO

## Customization <a name="customization"/>

## Continuous Integration <a name="ci"/>
[![CircleCI](https://circleci.com/gh/rafaco/InAppDevTools/tree/master.svg?style=svg)](https://circleci.com/gh/rafaco/InAppDevTools/tree/master) 
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=alert_status)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools) 
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=security_rating)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=bugs)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=code_smells)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=coverage)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)

[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=sqale_index)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=ncloc)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)

## Downloads  <a name="download"/>

You don't normally need to manually download our library or our plugin as they are published in repositories preconfigured by Android Studio. Just follow the [installation](#setup) process and build your project.

- Our library is available at our [Bintray](https://bintray.com/rafaco/InAppDevTools/inappdevtools) repository and linked to [jCenter](https://bintray.com/bintray/jcenter?filterByPkgName=inappdevtools) (preconfigured)
- Our plugin is available at [Gradle Plugin Portal](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) (preconfigured)
- Our source repository is available at [GitHub](https://github.com/rafaco/InAppDevTools/) and you can download snapshots of every library version at [releases](https://github.com/rafaco/InAppDevTools/releases)
~~- Our sample app will be available to download from [Google Play](https://play.google.com).~~

## About the author and this project

I'm a Senior Software Engineer and this project started as a playground to learn Android on my spare time. I have a lot of fun with it and as it has turned into an interesting project for every Android developer, I've finally left a permanent work position to focus on this. I hope to publish a first complete version at the end of this summer (2019) before continue with my career.

## Contributing [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)

I'm really looking forward to it! Report bugs, send me a feature request or just give me some feedback about what you like and what would you change.

## Thanks <a name="thanks"/>
- To [whataa](https://github.com/whataa) for [pandora](https://github.com/whataa/pandora): library used for storage and view inspector.
- To [jgilfelt](https://github.com/jgilfelt) for [chuck](https://github.com/jgilfelt/chuck): library used for network inspector.
- To [alorma](https://github.com/alorma) for [timelineview](https://github.com/alorma/timelineview): library used at stacktrace.
- To [tiagohm](https://github.com/tiagohm) for [CodeView](https://github.com/tiagohm/CodeView): inspiration for my codeview.
~~- To [kbiakov](https://github.com/kbiakov) for [CodeView-Android](https://github.com/kbiakov/CodeView-Android): library used to view source codes.~~
- To [Zsolt Kocsi](https://github.com/zsoltk) for [paperwork](https://github.com/zsoltk/paperwork): inspiration for CompileConfig.
- To [valdesekamdem](https://github.com/valdesekamdem) for [MaterialDesign-Toast](https://github.com/valdesekamdem/MaterialDesign-Toast): inspiration for our CustomToast.
- To [Wajahat Karim](https://github.com/wajahatkarim3) for [JCenter-Gradle-Scripts](https://github.com/wajahatkarim3/JCenter-Gradle-Scripts): used to publish into Bintray.
- //TODO: add com.github.anrwatchdog:anrwatchdog?
- //TODO: add replacement for com.opencsv:opencsvallo

## Apps using this library <a name="usages"/>
- Your app linked here! Just ask me for it

## License <a name="license"/>
Apache-2.0
