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

### Setup <a name="basic"/>
You only need to modify 2 gradle files. On your **root build.gradle** file, import our plugin and add the JitPack repository. `plugins` closure should be right after `buildscript` and latest version is ![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=plugin&colorB=blue&style=flat-square)

```gradle
buidscript {...}

plugins {
    id "es.rafaco.inappdevtools" version "0.0.16" apply false
}

allprojects {
    repositories {
        maven { url "https://jitpack.io"}
    }
}
```

Then, on your **app module build.gradle**, apply our plugin, add targetCompatibility with Java8, and include our library in dependencies. You have to choose between `androidx` or `support` artifact according to your Android libraries and `noop` artifact is recommended for your release compilations. 

| Artifact | Version | Description |
|---|---|---|
|support | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/support/maven-metadata.xml.svg?colorB=blue&label=support&style=flat-square) | For legacy projects using Android Support libraries.|
|androidx | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/androidx/maven-metadata.xml.svg?colorB=blue&label=androidx&style=flat-square) | For modern projects using AndroidX libraries. Jetifier required.|
|noop | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/noop/maven-metadata.xml.svg?colorB=blue&label=noop&style=flat-square) | No operational, for your release compilations. Compatible with AndroidX and Support libraries.| 

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
    debugImplementation 'es.rafaco.inappdevtools:androidx:0.0.53'
    //debugImplementation 'es.rafaco.inappdevtools:support:0.0.53'
    
    releaseImplementation 'es.rafaco.inappdevtools:noop:0.0.53'
}
```

For AndroidX ensure Jetifier is enabled in your project (our sources are 100% migrated but not all our dependencies). On your **root gradle.properties** include:

```gradle
android.useAndroidX=true
android.enableJetifier=true
```

> **IMPORTANT: Your source code will be exposed on your debug compilations.** throw our Source Inspection feature, but you can [disable or limit your source code exposition](#exposed_sources).

Ready to go. Just run a debug build and shake your app!

### Flutter <a name="flutter"/>
For a Flutter project you can use our library in your android compilations. Perform the same previous steps but within the android folder of your project. 

Then modify your `pubspec.yaml` file to include the assets generated by our plugin:

```yaml
flutter:
  uses-material-design: true
  
  assets:
    - build/app/generated/assets/
```
Additional Flutter integrations are in mind but with low priority. Join us and take this task :).

### Enable network inspection (optional) <a name="network"/>
If your app use Retrofit, we can record all network communications for you, allowing to inspect and report them. To enable it, add our OkHttpClient to your Retrofit initialization class:

```java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(Iadt.getOkHttpClient())
                .build();
```

### Including additional gradle modules (optional)  <a name="modules"/>
You can easily include your sources from other submodules from your project. We only currently support Android modules: Application, Library or Feature. 

To do so just apply our plugin to their build.gradle file:
```gradle
apply plugin: 'es.rafaco.inappdevtools'
```


## Usage <a name="usage"/>
After the [installation](#setup) you only need to generate a debug build of your app and run it on a real device or emulator. 

### Invocation <a name="invocation"/>
On crash our UI will automatically popup but you can also invoke it at any time by using one of the following methods:
- Shake your device with your app on foreground
- Tap our floating icon
- Tap our notification (disabled by default, enable it with configuration)
- Or programmatically calling `Iadt.show();`


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
| `invocationByShake` | boolean | true | Disable opening our UI on device shake |
| `invocationByIcon` | boolean | true | Enable a permanent overlay icon to open our UI |
| `invocationByNotification` | boolean | false | Show a permanent notification to open the UI. Warning: it currently use a foreground service so your app will not be killed on background |
| `callDefaultCrashHandler` | boolean | false | Propagate unhandled exceptions to the default handler (for Crashlytics and similar) |
<!-- ## Customization <a name="customization"/> -->

### Limiting sources exposition <a name="exposed_sources"/>

When this library is enabled, **your source code can be accessed at our UI and they can also be extracted from your APK**. Using default configuration, our library will be enabled on debug builds and automatically disabled for release builds, even if you don't use `noop` artifact for your release.

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


## Apps using this library <a name="usages"/>
- Your app can be listed here, just ask me for it! 


## Thanks <a name="thanks"/>
- To [@whataa](https://github.com/whataa) for [Pandora](https://github.com/whataa/pandora), key for layout inspection and storage browser features :trophy:
- To [@Zsolt Kocsi](https://github.com/zsoltk) for [Paperwork](https://github.com/zsoltk/paperwork), inspiration for CompileConfig
- To [@jgilfelt](https://github.com/jgilfelt) for [Chuck](https://github.com/jgilfelt/chuck), used for network interception
- To [@tiagohm](https://github.com/tiagohm) for [CodeView](https://github.com/tiagohm/CodeView)
- To [@alorma](https://github.com/alorma) for [TimelineView](https://github.com/alorma/timelineview)
- To [@valdesekamdem](https://github.com/valdesekamdem) for [MaterialDesign-Toast](https://github.com/valdesekamdem/MaterialDesign-Toast)
- To [@rnevet](https://github.com/rnevet) for [WCViewPager](https://github.com/rnevet/WCViewPager)
- To [@SalomonBrys](https://github.com/SalomonBrys) for [ANR-WatchDog](https://github.com/SalomonBrys/ANR-WatchDog)
- To [@nisrulz](https://github.com/nisrulz) for [EasyDeviceInfo](https://github.com/nisrulz/easydeviceinfo)
- To [Android Open Source Project](https://source.android.com/) for [Arch](https://developer.android.com/topic/libraries/architecture)


## Contributing and building instructions [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)

First off, thank you for considering contributing to InAppDevTools. It's people like you that make InAppDevTools such a great tool. There are many ways to contribute starting from giving us a :star:, recommending this library to your friends :loudspeaker: or sending us your feedback :love_letter:. Check out our [CONTRIBUTING.md](CONTRIBUTING.md) guide.


## About the author

I'm a Senior Software Engineer and I started this project as a personal tool to support my daily duties. I have always worked on proprietary software around Spain and UK, this is my first open source endeavour and I'm looking forward to create a friendly community around this project. Please feel free to correct me, give me any advise or pointing me in the right direction.
 
After 2 years of overnight coding fun, I've left my work position to fully focus on this. I hope to publish a fully functional version before Christmas 2019.


## License <a name="license"/>
```
Copyright 2018-2019 Rafael Acosta Alvarez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
This project modify, include and use products with separate copyright
notices and license terms. For details, see [LICENSE](LICENSE)
