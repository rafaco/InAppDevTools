# InAppDevTools [![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/support/maven-metadata.xml.svg?colorB=blue&label=library&style=plastic)](https://bintray.com/rafaco/InAppDevTools/support/_latestVersion) [![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=plugin&colorB=blue&style=plastic)](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) [![Maturity](https://img.shields.io/badge/maturity-experimental-red.svg?style=plastic)](https://github.com/rafaco/InAppDevTools/commits)

<p align="center">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/images/social.png" width="50%">
</p>


**InAppDevTools is an open source library that enhances the internal compilations of any Android app development teams. It allows to report, inspect and debug your app from the same screen when it's running. No cable needed, our UI shows over your app.**

- Auto log events. From basic reproduction steps to advanced entries (navigation, network requests, lifecycle events, crashes, ANRs, device events, user interactions...).
- View crash details immediately with graphic stacktrace, causing source lines, previous logs and screenshot.
- Inspect your standard logs, view layout, source code (original and generated) and storage (db, SharedPrefs and Files).
- Modify your app behaviour on runtime editing values at your view layout, db or SharedPrefs.
- Get exclusive info about your running app (processes, task, threads, services...), your build (user, host, remote repo, local changes...), your app, the device and his OS.
- Reports crashes or issues directly to developers, including a zip with logs, screenshots and other info.
- Easy installation and configuration. No Application class override needed, all from Gradle.
- Customize our tools to your needs, add buttons to run your methods and use our dev helpers.

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
You only need to modify 2 gradle files. 

On your **root** build.gradle file, import our plugin and add the JitPack repository. Plugin closure should be just after `buildscript` and latest version is ![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=plugin&colorB=blue&style=flat-square)

```gradle
buidscript {...}

plugins {
    id "es.rafaco.inappdevtools" version "0.0.14" apply false
}

allprojects {
    repositories {
        maven { url "https://jitpack.io"}
    }
}
```

Then, on your **app** build.gradle, add targetCompatibility with Java8, apply our plugin and include our library in dependencies. Latest version is [![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/support/maven-metadata.xml.svg?colorB=blue&label=library&style=flat-square)](https://bintray.com/rafaco/InAppDevTools/support/_latestVersion) 

```gradle
apply plugin: 'com.android.application'
apply plugin: 'es.rafaco.inappdevtools'

android {
    ...
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation 'es.rafaco.inappdevtools:support:0.0.52'
}
```
This setup enable InAppDevTools only for your debug builds but it have some side effects solved in following sections. You can already give a try to our library, just build a debug version of your app and shake it!

### Source code exposition disclaimer <a name="sources_disclaimer"/>
Using default configuration, on your debug builds **all your source code can be view in our UI, can be shared and can be extracted from your APK**. This could also include hardcoded API keys or passwords.

You can limit this behaviour by using our [configuration](#exposed_sources).

### Optimize your builds <a name="noop"/>
If your app use AndroidX libraries, we provide an optimized artifact for you. It currently need Jettifier enabled due to a transitive dependency (WIP). 

We also provide a `noop` artifact recommended for your release compilations. Operational artifacts (`androidx` or `support`) are automatically disabled for your release builds but they needlessly increase your release apk size.

To add conditional Gradle dependencies, prepend build type or your flavor name when including our dependencies: 
```gradle
dependencies {
    debugImplementation 'es.rafaco.inappdevtools:androidx:0.0.52'
    //debugImplementation 'es.rafaco.inappdevtools:support:0.0.52'
    
    releaseImplementation 'es.rafaco.inappdevtools:noop:0.0.52'
}
```

| Artifact | Version | Description |
|---|---|---|
|support | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/support/maven-metadata.xml.svg?colorB=blue&label=support&style=flat-square) | For legacy projects using Android Support libraries.|
|androidx | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/androidx/maven-metadata.xml.svg?colorB=blue&label=androidx&style=flat-square) | For modern projects using AndroidX libraries. Jetifier enabled is curretly needed.|
|noop | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/noop/maven-metadata.xml.svg?colorB=blue&label=noop&style=flat-square) | No operation. Recommended for your release versions with AndroidX or Support libraries.| 

Note: Don't include the "v" character in your dependencies.

### Enable network inspection (optional) <a name="network"/>
If your app use Retrofit, we can record all network communications for you, allowing to inspect and report them. To enable it, add our OkHttpClient to your Retrofit initialization class:

```java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(Iadt.getOkHttpClient())
                .build();
```

### Including additional gradle modules <a name="modules"/>
You can easily include your sources from other submodules from your project. We only currently support Android modules: Application, Library or Feature. 

To do so just apply our plugin to their build.gradle file:
```gradle
apply plugin: 'es.rafaco.inappdevtools'
```


## Usage <a name="usage"/>
After the [installation](#setup) you only need to generate a debug build of your app and run it on a real device or emulator. 

### Invocation <a name="invocation"/>
On crash our UI will automatically popup but you can also invoke it at any time by using one of the following methods:
- Shake your device
- Tap our floating icon
- Touch our notification
- Or programmatically calling `Iadt.show();`
```java

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

When this library is enabled, **your source code can be view in our ui and they can also be extracted from your apk files**. Using default configuration, our library will be enabled on debug builds and automatically disabled for release builds, even if you don't use `noop` artifact for your release.

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
* Stop using our `noop` artifact in your release dependencies. i.e. replace `releaseImplementation 'es.rafaco.inappdevtools:noop:...'` by `releaseImplementation 'es.rafaco.inappdevtools:androidx:...'`

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
