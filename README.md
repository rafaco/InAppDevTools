# In-App DevTools [![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=library&style=plastic)](https://bintray.com/rafaco/InAppDevTools/inappdevtools/_latestVersion) [![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=plugin&colorB=blue?style=plastic)](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) [![Maturity](https://img.shields.io/badge/maturity-experimental-red.svg?style=plastic)](https://github.com/rafaco/InAppDevTools/commits)

**Android library with a set of tools for developers. It allows to inspect and debug apps from within it, on the same screen. Auto-logger, crash handler, source browser, layout inspector, storage editor, logcat viewer, info panels, reports, method tracker, coding helpers and much more.**

- Inspectors: sources, logcat, layout hierarchy, edit your storage (db, SharedPrefs and Files) and info panels
- Auto generate a FriendlyLog with basic reproduction steps as well as advanced entries (lifecycle events, network requests, errors, device events,...)
- See a crash detail immediately and navigate to causing source lines
- Send flexible reports by email or other apps
- Customize your own tools, easily run your task and use our dev helpers.
- Easy to install and configurable


##### Table of Contents

- [Demo](#sample)
- [Installation](#setup)
  - [Limitations](#req)
  - [Basic setup](#basic)
  - [Exclude modules](#modules)
  - [Add network interceptor](#network)
- [Usage](#usage)
  - [Invocation](#invocation)
  - [Configuration](#configuration)
- [Features](#features)
  - [Overlay system](#overlay)  
  - [Friendly logger](#friendly)
  - [Crash handler](#crash)
  - [Inspectors](#inspector)
  - [Reports](#reports)
- [Customization](#customization) 


## Demo App <a name="sample"/>
A demostration app is available to download from GooglePlay (comming soon). It's allow you to play staight away with our library installed on an app.

His source code is in this repository ("sample" folder) and it's contains implementation examples. Clone the project and run the sample app module to install it into a real device or an emulator.

## Installation <a name="setup"/>

### Limitations <a name="req"/>
- minSdkVersion >= 16 (Jelly Bean). Check it at your app/build.gradle
- If AndroidX is enabled: Jetifier should also be enabled. Check it at your gradle.properties

### Basic set-up <a name="basic"/>
For standard projects you only need to modify 2 gradle files and rebuild your app.

1. On the build.gradle file of your root module folder, declare our plugin (just after buidscript closure) and add JitPack to the list of repositories (TEMP, transitive dependency):

```gradle
buildscript {...}

plugins {
    id "es.rafaco.inappdevtools" version "[PLUGIN_VERSION]"
}

allprojects {
    repositories {
        maven { url "https://jitpack.io"}
    }
}
```

2. On the build.gradle file of your app module folder, apply our gradle plugin and add our library to the list of dependencies: 

```gradle
android {...}

dependencies {
    implementation 'es.rafaco.inappdevtools:inappdevtools:[LIBRARY_VERSION]'
}
```

Latest versions are 
![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=[PLUGIN_VERSION]&colorB=blue&style=flat-square) and 
![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=[LIBRARY_VERSION]&style=flat-square) (don´t include the "v" character)

### Exclude modules from plugin <a name="modules"/>
If your project have additional modules, you can decide witch one will run our plugin to collect build info, sources and resources. To do so, you need to disable the default behavior of applying our plugin immediately in the root build.gradle:

```gradle
plugins {
    id "es.rafaco.inappdevtools" version "[PLUGIN_VERSION]" apply false
}
```
And then manually apply our plugin in the build.gradle of every desired module, including the main one (app):

```gradle
apply plugin: 'es.rafaco.inappdevtools'
```

### Add network interceptor <a name="network"/>
If your app use Retrofit, you can inspect and report all network communications make by your app. To enable it, add our OkHttpClient to your api initialization class:

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
Our sample app will be available to download from [Google Play](https://play.google.com).

You don't normally need to manually download our library or our plugin as they are published in repositories preconfigured by Android Studio. Just follow the [installation](#setup) process and build your project.

- Our library is available at our [Bintray](https://bintray.com/rafaco/InAppDevTools/inappdevtools) repository and linked to [jCenter](https://bintray.com/bintray/jcenter?filterByPkgName=inappdevtools) (preconfigured)
- Our plugin is available at [Gradle Plugin Portal](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) (preconfigured)
- Our source repository is available at [GitHub](https://github.com/rafaco/InAppDevTools/) and you can download snapshots of every library version at [releases](https://github.com/rafaco/InAppDevTools/releases)

## Apps using this library <a name="usages"/>
- Your app linked here! Just ask me for it
- Our sample app

## Contributing [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)

## Thanks <a name="thanks"/>
- To [whataa](https://github.com/whataa) for [pandora](https://github.com/whataa/pandora): library used for storage and view inspector.
- To [jgilfelt](https://github.com/jgilfelt) for [chuck](https://github.com/jgilfelt/chuck): library used for network inspector.
- To [alorma](https://github.com/alorma) for [timelineview](https://github.com/alorma/timelineview): library used at stacktrace.
- To [kbiakov](https://github.com/kbiakov) for [CodeView-Android](https://github.com/kbiakov/CodeView-Android): library used to view source codes.
- To [Zsolt Kocsi](https://github.com/zsoltk) for [paperwork](https://github.com/zsoltk/paperwork): inspiration for CompileConfig.
- To [valdesekamdem](https://github.com/valdesekamdem) for [MaterialDesign-Toast](https://github.com/valdesekamdem/MaterialDesign-Toast): inspiration for our CustomToast.
- To [Wajahat Karim](https://github.com/wajahatkarim3) for [JCenter-Gradle-Scripts](https://github.com/wajahatkarim3/JCenter-Gradle-Scripts): used to publish into Bintray.
- //TODO: add com.github.anrwatchdog:anrwatchdog?
- //TODO: add replacement for com.opencsv:opencsvallo

## License <a name="license"/>
Apache-2.0
