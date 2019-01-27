# In-App DevTools [![Maturity](https://img.shields.io/badge/maturity-experimental-blue.svg?style=flat)](https://github.com/rafaco/InAppDevTools/commits) [![Download from Bintray](https://api.bintray.com/packages/rafaco/InAppDevTools/inappdevtools/images/download.svg) ](https://bintray.com/rafaco/InAppDevTools/inappdevtools/_latestVersion) [![Contributions](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)


**Android library with a set of tools for developers. It allow to inspect and debug apps from within it, on the same screen. Auto-logger, crash handler, source browser, layout inspector, storage editor, logcat viewer, info panels, reports, method tracker, coding helpers and much more.**

- Inspectors: sources, logcat, layout hierarchy, edit your storage (db, SharedPrefs and Files) and info panels
- Auto generate a FriendlyLog with reproduction steps and advanced entries(lifecycle events, network requests, errors, device events,...
- See a crash detail inmediately and navigate to causing source lines
- Send flexible reports by email or other apps
- Customize your own tools, easily run your task and use our dev helpers.
- Easy to install and configurable


##### Table of Contents

- [Usage](#usage)  
- [Features](#features)  
  - [Overlay system](#overlay)  
  - [Friendly logger](#friendly)
  - [Crash handler](#crash)
  - [Inspectors](#inspector)
  - [Report](#report)
- [Configuration](#configuration) 
- [Customization](#customization) 


## Usage <a name="usage"/>

Basic setup only requiere you to modify gradle files for your project.

- Step 1: Add my bintray repository to allprojects repositories at your project's build.gradle. Temporary, it will be not needed when migrated to jCenter
```gradle
allprojects {
    repositories {
        //...
        maven { url "https://dl.bintray.com/rafaco/InAppDevTools"}
    }
}
```

- Step 2: Add our library as dependency and apply our plugin at your app module's build.gradle)
```gradle
apply plugin: 'com.android.application'

dependencies {
    //...
    implementation 'es.rafaco.inappdevtools:library:0.0.1'
}

apply from: 'https://raw.githubusercontent.com/rafaco/InAppDevTools/master/plugin/devtools-plugin.gradle'
devtools {
    enabled = true
    email = 'rafaco@gmail.com'
}
```
//TODO Config

## Features <a name="features"/>

### Overlay system <a name="overlay"/>
We use overlays to show informations over your app instead of activities
- Adjust any of our views over your app while you still using it
- Non intrusive with your views their focus or your activity stack
- Few invocation methods availables: Notification, floating icon, shake or from your sources (see integrations)

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
  - Navigate thru the layout hierarchy and edit his properties
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

### Reports <a name="Reports"/>
- Report bugs by email or share them with your favourite app
- Attach logs, info, description, crashes, db dumps…
- Take screenshots and attach them
- //TODO


## Configuration <a name="Configuration"/>

## Customization <a name="Customization"/>


## Sample App available
A sample app is available in this repo. It allow you to play with Devtools preinstalled on a demo app and it's source code contain examples of installation, configuration and integrations. It will be available to download at [Google Play](https://play.google.com). 

## Contributing [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)

## Downloads
<a href='https://bintray.com/rafaco/InAppDevTools/library?source=watch' alt='Get automatic notifications about new "library" versions'><img src='https://www.bintray.com/docs/images/bintray_badge_color.png'></a>



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
