# In-App DevTools [![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=library&style=plastic)](https://bintray.com/rafaco/InAppDevTools/inappdevtools/_latestVersion) [![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=plugin&colorB=blue?style=plastic)](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) [![Maturity](https://img.shields.io/badge/maturity-experimental-red.svg?style=plastic)](https://github.com/rafaco/InAppDevTools/commits)


## Description

**In-App DevTools is a set of developer tools that can be added to your debug compilation as a library. It's like Chrome DevTools but for Android apps and runnning on the same screen! (without cable and on the go). Auto-logger, crash handler, source browser, layout inspector, storage editor, logcat viewer, networkactivity, info panels, flexible reports, classs/method tracker, coding helpers and much more.**

- Inspectors: sources, logs, view layout, edit your storage (db, SharedPrefs and Files) and info panels
- Auto generate a FriendlyLog with basic reproduction steps as well as advanced entries (lifecycle events, network requests, errors, device events,...)
- See a crash detail immediately and navigate to the causing source lines.
- Send flexible reports by email or other apps
- Customize your own tools, easily run your task and use our dev helpers.
- Easy to install and configurable

For extended feature description, visit our wiki: [Feature description](https://github.com/rafaco/InAppDevTools/wiki/Feature-description)

<p>
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Home.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Info.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Crash.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Source.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Logs.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_View.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_View2.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Storage.jpg" width="200">
<!---![Home](https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Home.jpg)-->
</p>

## Installation <a name="setup"/>

### Limitations <a name="req"/>
- minSdkVersion >= 16 (Jelly Bean). Check it at your app/build.gradle
- If AndroidX is enabled, Jetifier should also be enabled (An AndroidX specific version will be release).

### Basic set-up <a name="basic"/>
For standard Android projects you only need to modify 2 gradle files and rebuild your app.

1. On your root build.gradle, import our plugin (plugins closure must be just after buidscript). Then add the JitPack repository (TEMP, it's a transitive dependency):

```gradle
plugins {
    id "es.rafaco.inappdevtools" version "0.0.12"
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
    implementation 'es.rafaco.inappdevtools:inappdevtools:0.0.46'
}
```

Latest versions are 
![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=[PLUGIN_VERSION]&colorB=blue&style=flat-square) and 
![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=[LIBRARY_VERSION]&style=flat-square).  Don't include the "v" character. 

### Add network interceptor (optional) <a name="network"/>
If your app use Retrofit, we can record all network network communications for you, allowing you to inspect and report them. To enable it, add our OkHttpClient to your Retrofit initialization class:

```java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(DevTools.getOkHttpClient())
                .build();
```
For extended installation instructions, visit our wiki: [Extended installation](https://github.com/rafaco/InAppDevTools/wiki/Extended-installation)


## Usage <a name="usage"/>

After the [installation](#setup) you only need to rebuild your app and run it on a real device or emulator. 

On crash our UI will automatically popup but you can also invoke it at any time by using one of the following methods:
- Shake your device
- Touch our notification
- Tap our floating icon over your app (disabled by default)
- Or programmatically using our public interface (Iadt):
```java
Iadt.show();
Iadt.hide();
```


### Configuration <a name="configuration"/>
You can configure our library **and our plugin** behaviour at build time by using our gradle extension on your app module's build.gradle. Note that the plugin is only afected by this way of configuration and their values will remain after cleaning app's data. 
```gradle
inappdevtools {
    enabled = true
    email = 'yourmail@yourdomain.com'
}
```

You can also override previous build time configurations at run time by using the ConfigScreen (Home, Config) or programmatically from your sources. This values will be lost after cleaning app's data. 
```java
Iadt.getConfig().setBoolean(Config.ENABLED, false);
Iadt.restartApp();
```

Available properties:

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `enabled` | boolean | true | Disable all and simulate a no-op library and plugin |
| `debug` | boolean | false | Refering to our library. Print extra logs and include our sources to your compilation  |
| `email` | String | null | Default email to use for reports |
| `overlay_enabled` | boolean | true | Disable our overlay UI  |
| `invocation_by_shake` | boolean | true | Disable opening our UI on device shake  |
| `invocation_by_icon` | boolean | false | Enable a permanent overlay icon to open our UI  |
| `invocation_by_notification` | boolean | true | Disable showing our notification to open the UI  |
| `call_default_crash_handler` | boolean | false | Propagate unhandler exceptions to the default handler (Crashalitics)  |

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
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=sqale_index)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=coverage)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=ncloc)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)

//TODO: Pass SonarCloud's quality gate


## Downloads  <a name="download"/>

You don't normally need to manually download our library or our plugin as they are published in repositories preconfigured by Android Studio. Just follow the [installation](#setup) process and build your project.

- Our library is available at our [Bintray](https://bintray.com/rafaco/InAppDevTools/inappdevtools) repository and linked to [jCenter](https://bintray.com/bintray/jcenter?filterByPkgName=inappdevtools) (preconfigured)
- Our plugin is available at [Gradle Plugin Portal](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) (preconfigured)
- Our source repository is available at [GitHub](https://github.com/rafaco/InAppDevTools/) and you can download snapshots of every library version at [releases](https://github.com/rafaco/InAppDevTools/releases)
- ~~Our sample app is be available to download from [Google Play](https://play.google.com).~~

## Contributing [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)

I'm really looking forward to create a small comunity around this library! You can use the [issues](https://github.com/rafaco/InAppDevTools/issues) tab to report bugs, request a feature or just to give me some feedback. Pull request are more than welcome.


## Apps using this library <a name="usages"/>
- Your app linked here! Just ask me for it

## Thanks <a name="thanks"/>
- To [@whataa](https://github.com/whataa) for [pandora](https://github.com/whataa/pandora): library used for storage and view inspector.
- To [@jgilfelt](https://github.com/jgilfelt) for [chuck](https://github.com/jgilfelt/chuck): library used for network inspector.
- To [@alorma](https://github.com/alorma) for [timelineview](https://github.com/alorma/timelineview): library used at stacktrace.
- To [@tiagohm](https://github.com/tiagohm) for [CodeView](https://github.com/tiagohm/CodeView): inspiration for my codeview.
- To [@Zsolt Kocsi](https://github.com/zsoltk) for [paperwork](https://github.com/zsoltk/paperwork): inspiration for CompileConfig.
- To [@valdesekamdem](https://github.com/valdesekamdem) for [MaterialDesign-Toast](https://github.com/valdesekamdem/MaterialDesign-Toast): inspiration for our CustomToast.
- To [@Wajahat Karim](https://github.com/wajahatkarim3) for [JCenter-Gradle-Scripts](https://github.com/wajahatkarim3/JCenter-Gradle-Scripts): used to publish into Bintray.
- //TODO: add com.github.anrwatchdog:anrwatchdog?
- //TODO: add replacement for com.opencsv:opencsvallo

## About the author
I'm a Senior Software Engineer and this project started as a playground to learn Android on my spare time. I have a lot of fun with it and as it has turned out to be an interesting project for every Android developer, I've finally left a permanent work position to focus on this. I hope to publish a first complete version at the end of this summer (2019) before continue with my career.

## License <a name="license"/>
Apache-2.0
