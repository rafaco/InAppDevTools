# InAppDevTools [![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/support/maven-metadata.xml.svg?colorB=blue&label=library&style=plastic)](https://bintray.com/rafaco/InAppDevTools/support/_latestVersion) [![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=plugin&colorB=blue?style=plastic)](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) [![Maturity](https://img.shields.io/badge/maturity-experimental-red.svg?style=plastic)](https://github.com/rafaco/InAppDevTools/commits)

<p align="center">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/images/social.png" width="50%">
</p>


**InAppDevTools is an open source library that enhances the internal compilations of any Android app development teams. It allows your app to report, inspect and debug itself from the same screen when it's running. No cable needed, our UI shows over your running app.**

- Auto log events. From basic reproduction steps to advanced entries (navigation, network requests, lifecycle events, crashes, ANRs, device events, user interactions...).
- View crash details immediately with graphic stacktrace, causing source lines, previous logs and screenshot.
- Inspect your standard logs, view layout, source code (original and generated) and storages (db, SharedPrefs and Files).
- Modify your app behaviour on runtime editing values at your view layout, db or SharedPrefs.
- Get exclusive info about your running app (processes, task, threads, services...), your build (user, host, remote repo, local changes...), your app, the device and his OS.
- Send flexible reports by email or other apps. After a crash or at any time.
- Easy installation and configuration. No Application class needed, all from Gradle.
- Customize our tools to your needs, easily run your methods and use our dev helpers.

**Auto-logger, crash handler, source browser, layout inspector, storage editor, logcat viewer, network activity, info panels, flexible reports, class/method tracker, coding helpers and much more.**

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

For extended feature description, visit our wiki: [Feature description](https://github.com/rafaco/InAppDevTools/wiki/Feature-description)

## Installation <a name="setup"/>

### Limitations <a name="req"/>
- minSdkVersion >= 16 (Jelly Bean). Check it at your app/build.gradle
- Minimun Gradle version: //TODO

### Express setup <a name="basic"/>
You only need to modify 2 gradle files. On your **root build.gradle file**, import our plugin (just after buidscript) and add JitPack repository:

```gradle
buidscript {...}

plugins {
    id "es.rafaco.inappdevtools" version "0.0.13"
}

allprojects {
    repositories {
        maven { url "https://jitpack.io"}
    }
}
```

On your **app build.gradle**, add targetCompatibility with Java8 and include our library in dependencies. Choose between `androidx` or `support` flavors and don't include the "v" character in dependencies.

| Flavor | Version | Description |
|---|---|---|
|androidx | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/androidx/maven-metadata.xml.svg?colorB=blue&label=androidx&style=flat-square) | For modern projects using AndroidX libraries. Jetifier enabled is curretly needed.|
|support | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/support/maven-metadata.xml.svg?colorB=blue&label=support&style=flat-square) | For legacy projects using Android Support libraries.|
|noop | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/noop/maven-metadata.xml.svg?colorB=blue&label=noop&style=flat-square) | No operation flavor recommended for your release versions (androidx and support).| 

```gradle

android {
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation 'es.rafaco.inappdevtools:androidx:0.0.51'
    //implementation 'es.rafaco.inappdevtools:support:0.0.51'
}
```
Build your app and shake your device! 

This express setup enable InAppDevTools only for your debug builds but it have some side effects solved in following sections:
* Your release apk size will be unnecessarly increased. [Using noop flavor](#noop)
* Your debug builds will expose your source code [Limiting sources exposition](#exposed_sources)
* Start logging all communications with your servers. [Enable network inspection](#network)
* Customize your experience [configuring our library](#configuration) and using our [coding helpers/integrations](#coding_helpers).




### Using noop flavor <a name="noop"/>
One side effects is that our library resources will increase your release apk size with never used code. We provide a tiny noop version and recommended it for release compilations. This also allow you to keep references to our library in your release soures.

To add conditional Gradle dependencies, prepend build type or your flavors to our dependency implementations. 
```gradle
dependencies {
    debugImplementation 'es.rafaco.inappdevtools:androidx:0.0.51'
    //debugImplementation 'es.rafaco.inappdevtools:support:0.0.51'
    
    releaseImplementation 'es.rafaco.inappdevtools:noop:0.0.51'
}
```

### Enable network inspection (optional) <a name="network"/>
If your app use Retrofit, we can record all network network communications for you, allowing you to inspect and report them. To enable it, add our OkHttpClient to your Retrofit initialization class:

```java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(DevTools.getOkHttpClient())
                .build();
```
For extended installation instructions, visit our wiki: [Extended installation](https://github.com/rafaco/InAppDevTools/wiki/Extended-installation)


## Usage <a name="usage"/>

After the [installation](#setup) you only need to generate a debug build of your app and run it on a real device or emulator. 

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
You can configure our library behaviour at build time by using our gradle extension on your app module's build.gradle. This configuration also affect our plugin behaviour and cleaning your app's data will restore to this values. 
```gradle
apply plugin: 'es.rafaco.inappdevtools'

inappdevtools {
    enabled = true
    email = 'yourmail@yourdomain.com'
}
```

You can also override build configurations at run time by using our Config Screen (Overlay toolbar > More > Setting) or programmatically from your sources. This values will be lost when cleaning your app data, restoring the build configuration. 
```java
Iadt.getConfig().setBoolean(Config.ENABLED, false);
Iadt.restartApp();
```

Available properties:

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `email` | String | null | Default email to use for reports |
| `enabled` | boolean | true | Disable all and simulate the no-op library and plugin |
| `enabledOnRelease` | boolean | false | Force enabling our library for release builds of your app. Warning, read [Exposed sources disclaimer](#exposed_sources) |
| `debug` | boolean | false | Enable debug mode for the library. It print extra logs and include our sources to your compilation  |
| `sourceInclusion` | boolean | true | Disable including this module sources in your apk. Read [Exposed sources disclaimer](#exposed_sources) |
| `sourceInspection` | boolean | true | Disable source features and source inclusion. Read [Exposed sources disclaimer](#exposed_sources) |
| `overlayEnabled` | boolean | true | Disable our overlay interface  |
| `invocation_by_shake` | boolean | true | Disable opening our UI on device shake |
| `invocationByIcon` | boolean | false | Enable a permanent overlay icon to open our UI |
| `invocationByNotification` | boolean | true | Disable showing our notification to open the UI |
| `callDefaultCrashHandler` | boolean | false | Propagate unhandled exceptions to the default handler (for Crashlytics and similar) |
<!-- ## Customization <a name="customization"/> -->

### Limiting sources exposition <a name="exposed_sources"/>

When this library is enabled, **your source code can be view in our ui and they can also be extracted from your apk files**. Using default configuration, our library will be enabled on debug builds and automatically disabled for release builds, even if you don't use noop flavor for your release.

If you don't want to show all or some of your proprietary sources in your debug builds, you have few options:

* ~~(TODO) Exclude concrete source files by configuration. Useful for specific files with sensible information like passwords, api keys,...~~
* Disable source inclusion by configuration (`sourceInclusion = false`). Your apk will not include your sources but assets inspection will be available in our overlay.
* Disable source inspection by configuration (`sourceInspection = false`). Your apk will not include your sources and our interface will not show your assets.
* ~~(TODO) Enable tester mode~~
* Disable all our library by configuration (`enabled = false`). Your apk will not include your sources and all our features will be disabled but in your apk.
* Disable all our library using the noop dependency. Same as before but with a minimal apk size increase.

When source inclusion or source inspection get disabled you also lost the following features: browse your sources, view a source, share a source and navigation from stacktrace to source line.

You can also enable our library and the source inclusion/inspection in your release builds. This is not recommended but can be useful for beta versions: 
* Include `enabledOnRelease = true` in your configuration
* Remove `sourceInclusion` and `sourceInspection` from your configuration or ensure both of them are `true`.
* Stop using our noop flavor in your release dependencies. i.e. replace `releaseImplementation 'es.rafaco.inappdevtools:noop:...'` by `releaseImplementation 'es.rafaco.inappdevtools:androidx:...'`

<!--This library work out of the box for developer compilations which include Source inspection, allowing users to view and share your source code. In order to provide this features, your apk contains your source code as well as you compiled code (a zip file asset). 
We can directly read your app's assets but we also include a a zip file in your apk with other source files:
* Your Java source sets. Content of src/main/java plus dynamic inclusions.
* Your resources: Content of src/main/res but excluding the raw folder.
* Build time generated sources. Content of build/generated/ excluding assets and png. -->

### Add run button
Add your own buttons to our Run screen. You have to provide a title and Runnable object, when you can perform any logic or call any of your app methods. 
```gradle
Iadt.addRunButton(new RunButton("Your text",
        new Runnable() {
            @Override
            public void run() {
                YourClass.yourMethod();
            }
        }));
```
Add them on startup (i.e. onCreate of your app or main activity) or dynamically at any point (i.e. after user log in). You can also specify an icon, a background color or a callback.


## Contributing [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)

I'm really looking forward to create a small community around this library! You can use the [issues tab](https://github.com/rafaco/InAppDevTools/issues) to report bugs, request a feature or just to give me your feedback. Pull request are more than welcome.

### Building artifacts

Standard user don't need to manually download or build our artifacts as they are available at public repositories preconfigured by Android Studio. Just follow the [installation](#setup) process and rebuild your project.

All artifacts are generated from [this GitHub repo](https://github.com/rafaco/InAppDevTools), each one using different combinations of module and variant.

| Artifact | Module | Variant | Description | Publication |
| --- | --- | --- | --- | --- |
| es.rafaco.inappdevtools | plugin | - | IADT plugin for Gradle| [Gradle Plugin Portal](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) |
| es.rafaco.inappdevtools:support | library | support | IADT library for Support libraries | [Bintray](https://bintray.com/rafaco/InAppDevTools/support) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/support/) |
| es.rafaco.inappdevtools:androidx | library | androidx | IADT library for AndroidX libraries | [Bintray](https://bintray.com/rafaco/InAppDevTools/androidx) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/androidx/) |
| es.rafaco.inappdevtools:noop | noop | - | IADT library, no operational | [Bintray](https://bintray.com/rafaco/InAppDevTools/noop) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/noop/) |
| es.rafaco.compat:support | compat | support | Compat library for Support libraries | [Bintray](https://bintray.com/rafaco/Compat/support) / [jCenter](http://jcenter.bintray.com/es/rafaco/compat/support/) |
| es.rafaco.compat:androidx | compat | androidx | Compat library for AndroidX libraries | [Bintray](https://bintray.com/rafaco/Compat/androidx) / [jCenter](http://jcenter.bintray.com/es/rafaco/compat/androidx/) |
| es.rafaco.iadt.demo | demo | androidx/support | Demo app  | ~~[Google Play](https://play.google.com)~~ |


### Continuous Integration <a name="ci"/>
(Work in progress)
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

### About the author

I'm a Senior Software Engineer who has always been working on proprietary software around Spain and UK. I started this project as a personal tool to support my daily duties but I realise it could be interesting for other Android developers. After 2 years of overnight coding fun, I've left my work position to make it flexible and polished for everyone joy. I hope to publish a fully functional version before Christmas 2019.

This is my first open source project and I'm looking forward to create a small community around. Please feel free to correct me, give me any advise or pointing me in the right direction. 


## Thanks <a name="thanks"/>
- To [@whataa](https://github.com/whataa) for [pandora](https://github.com/whataa/pandora): library used for storage and view inspector.
- To [@jgilfelt](https://github.com/jgilfelt) for [chuck](https://github.com/jgilfelt/chuck): library used for network inspector.
- To [@alorma](https://github.com/alorma) for [timelineview](https://github.com/alorma/timelineview): library used at stacktrace.
- To [@tiagohm](https://github.com/tiagohm) for [CodeView](https://github.com/tiagohm/CodeView): inspiration for my codeview.
- To [@Zsolt Kocsi](https://github.com/zsoltk) for [paperwork](https://github.com/zsoltk/paperwork): inspiration for CompileConfig.
- To [@valdesekamdem](https://github.com/valdesekamdem) for [MaterialDesign-Toast](https://github.com/valdesekamdem/MaterialDesign-Toast): inspiration for our CustomToast.
- //TODO: add com.github.anrwatchdog:anrwatchdog?
- //TODO: add replacement for com.opencsv:opencsvallo

## Apps using this library <a name="usages"/>
- Your app linked here! Just ask me for it

## License <a name="license"/>
Apache-2.0
