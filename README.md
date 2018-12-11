# In-App DevTools [![Maturity](https://img.shields.io/badge/maturity-experimental-red.svg?style=flat)](https://github.com/rafaco/InAppDevTools/commits) [![Last release](https://img.shields.io/badge/last%20release-none-red.svg?style=flat)](https://github.com/rafaco/InAppDevTools/releases) [![Contributions](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)

*An Android library with a collection of useful tools for debugging, inspecting and reporting from within your own application* 

It auto-logs everything happening underneath and pop up on crash. You can invoke the overlay view to inspect your app as you use it.

- Auto-logs repro steps: navigation, user interaction, device events
- Advanced Auto-loggers: lifecycle events (application, activities and fragments), Network requests, Errors (crash and anr)...
- Inspector: sources, logcat, layout hierarchy, edit your storage (db, SharedPrefs and Files) and get info
- Customize your reports selecting a content or packing everything
- Define your runnables to easily run your special "piece of codes"

# Table of contents
1. [Introduction](#introduction)
2. [Installation](#installation)
    1. [Configuration](#configuration)
3. [Integration](#integration)
3. [Customization](#customization)
3. [Sample app](#sample)


## Introduction <a name="introduction"></a>
(For testers) Just installing it, your apk will nicely treat any crash. It prompt the error and allow the user to report the bug by email. Additional information could be typed and the report will auto include the exception, the full logcat since the app started, a custom activity log and more info like versions, device, os, hardware, memory, storage...

(For developer) Your app also get an overlay icon with tools to inspect on runtime and on the go. 

It have few preconfigured modes based on how aggressively the library integrate with your apk and their users.

- Develop
    - Overlay: Always accessible via overlay icon
    - OnCrash: see full info (exception, stacktrace, log, inspect, report...)
    - Tools: all enabled and accessible 

- Test: 
   - OnCrash: small error message and a button to report it by email
   - Tools: manual bug report, environment selector, info 

- Production: 
   - A low profile with all disabled and a minimum library get included to avoid code references problems
   - On crash: Restart app and silent report on background?
   
   
**Tools:**

- Crash handler (detect, show on screen, allow to report it and/or restart the app)
- LogCat reader everywhere
- Info summary (App, compilation, os, device, hardware, memory...)
- Shell command executor
- Report bugs by email and attach logs, info, description... 
- Inspector of Storage (filesystem, db, shared preferences)
- Inspector of activities stack, running services, processes and tasks 


**Features:**

- Easy to install
- Accessible everywhere (Overlay icon)
- Non intrusive with your views, stack or focus (system overlay layer)
- Only on release mode 

**Invocation methods:**
- Notification
- Icon
- Shake
- Custom (see integrations)

**Screenshots:**

-




***

## Installation

As simple as include 2 lines of code in your project:
1. Add this library as a gradle dependency (at build.gradle in your app module)
2. Call DevTools.install(this) at the onCreate() method of your extension from Application 

```
dependencies {
    implementation 'es.rafaco.devtools:devtools'
}
```

```java
public class YourApp extends Application {

    public void onCreate() {
        super.onCreate();
        DevTools.install(this);
        //...
    }
}
```

**Configuration:**

You can redefine how this library works by passing a DevToolsConfig object to the initial DevTools.install method. It has been coded following the builder pattern:
```
DevTools.install(this, DevToolsConfig.newBuilder()
    .addEmail("rafaco@gmail.com")
    .build()
);
```

- //TODO: Reference of configuration properties
- //TODO: Predefined configurations (default and userMode)



## Integrations
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

##Customization
- Create a tool
- Replace IconWidget by your own trigger

## Sample App available
A sample app is available to download at [Google Play](https://play.google.com). It allow you to play with Devtools preinstalled on a demo app and it's source code contain examples of installation, configuration and integrations.

## Contributing [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)
