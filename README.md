# In-App DevTools [![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=library&style=flat)](https://bintray.com/rafaco/InAppDevTools/inappdevtools/_latestVersion) [![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=plugin&colorB=blue)](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) [![Maturity](https://img.shields.io/badge/maturity-experimental-red.svg?style=flat)](https://github.com/rafaco/InAppDevTools/commits)

**Android library with a set of tools for developers. It allows to inspect and debug apps from within it, on the same screen. Auto-logger, crash handler, source browser, layout inspector, storage editor, logcat viewer, info panels, reports, method tracker, coding helpers and much more.**

- Inspectors: sources, logcat, layout hierarchy, edit your storage (db, SharedPrefs and Files) and info panels
- Auto generate a FriendlyLog with basic reproduction steps as well as advanced entries (lifecycle events, network requests, errors, device events,...)
- See a crash detail immediately and navigate to causing source lines
- Send flexible reports by email or other apps
- Customize your own tools, easily run your task and use our dev helpers.
- Easy to install and configurable


##### Table of Contents

- [Installation](#setup)
  - [Limitations](#req)
  - [Basic setup](#basic)
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



## Installation <a name="setup"/>

### Limitations <a name="req"/>
Current version need a minSdkVersion greater or equal 16 (Jelly Bean). Check it at the build.gradle file of your root module.

If your project use AndroidX you should have Jetifier enabled at your gradle.properties.

### Basic set-up <a name="basic"/>
You only need to modify 2 gradle files and rebuild your app.

- Step 1: On the build.gradle file of your root module folder:
  - Declare our plugin (just after buidscript)
  - Add JitPack repository (TEMP, transitive dependency)
```gradle
buildscript {...}

plugins {
    id "es.rafaco.inappdevtools" version "0.0.07" apply false
}

allprojects {
    repositories {
        maven { url "https://jitpack.io"}
    }
}
```

- Step 2: On the build.gradle file of your app module folder:
  - Apply our gradle plugin
  - Add our library to dependencies
```gradle
apply plugin: 'com.android.application'
apply plugin: 'es.rafaco.inappdevtools'

android {...}

dependencies {
    implementation 'es.rafaco.inappdevtools:inappdevtools:0.0.40'
}
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



## Sample App <a name="sample"/>
A sample app is available in this repo. It allows to play with our library preinstalled on an app and it's source code contain examples of installation, configuration and integrations.

## Downloads  <a name="download"/>
Our sample app will be available to download from [Google Play](https://play.google.com).

You don't normally need to manually download our library or our plugin as they are published in repositories preconfigured by Android Studio. Just follow the [installation](#setup) process and build your project.

- Our library is available at our [Bintray](https://bintray.com/rafaco/InAppDevTools/inappdevtools) repository and linked to [jCenter](https://bintray.com/bintray/jcenter?filterByPkgName=inappdevtools) (preconfigured)
- Our plugin is available at [Gradle Plugin Portal](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) (preconfigured)
- Our source repository is available at [GitHub](https://github.com/rafaco/InAppDevTools/) and you can download snapshots of every library version at [releases](https://github.com/rafaco/InAppDevTools/releases)


## Contributing [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)

## Thanks <a name="thanks"/>
//TODO: add dependencies and used sources

## License <a name="license"/>
Apache-2.0


<br/>
<br/>

# OUT-DATED: Following documentation is outdated... working on it

### Integrations <a name="integrations"/>
This library attempt to extend your app with minimal modification of your source code. Only install is required and you will get...
Although, you can do a direct integration to get:... 

**Programmatically interacting with tools:**
- Custom invocation
- Open Tool
- Take Screenshot
- Send Report


**Environment selector:**

**Inject details into tools:**

Special properties to include in tools and their reports.
- Custom properties (setProperty(Name, value))
- Custom watchers (setWatcher(Name, Runnable))
- Custom report vs "Custom properties" section at info/status 

**Network inspection:**

- Adding our network interceptor (OkHttpClient & http requests)

**Debugger assistance:**
- Show debug messages (showInfo, showError,...)
- Annotations to Debug classes/methods
- Annotations to improve tracking lifecycle (services, fragments...)
