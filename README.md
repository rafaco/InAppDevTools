# InAppDevTools ![Latest version](https://img.shields.io/maven-metadata/v/https/jcenter.bintray.com/es/rafaco/inappdevtools/support/maven-metadata.xml.svg?colorB=blue&label=version&style=flat-square)  [![Maturity](https://img.shields.io/badge/maturity-alpha-orange.svg?style=flat-square)](https://github.com/rafaco/InAppDevTools/commits)  [![Project Stats](https://www.openhub.net/p/InAppDevTools/widgets/project_thin_badge.gif)](https://www.openhub.net/p/InAppDevTools)

<!---[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-InAppDevTools-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/887) -->

<p align="center">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/images/social.png" width="50%">
</p>

<script type='text/javascript' src='https://www.openhub.net/p/InAppDevTools/widgets/project_basic_stats?format=js'></script>

<script type='text/javascript' src='https://www.openhub.net/p/InAppDevTools/widgets/project_languages?format=js'></script>

<script type='text/javascript' src='https://www.openhub.net/p/InAppDevTools/widgets/project_factoids?format=js'></script>

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

## Setup <a name="setup"/>

Include our plugin and our libraries in your Gradle files and that's it! We will pop up in your debug compilations.

On your **root build.gradle** file:

```gradle
buidscript {...}

plugins {
    id "es.rafaco.inappdevtools" version "0.0.55" apply false
}

allprojects {
    repositories {
        maven { url "https://jitpack.io"}
    }
}
```

On your **app** module **build.gradle** file:

```gradle
apply plugin: 'com.android.application'
apply plugin: 'es.rafaco.inappdevtools'

android {
    ...
}
dependencies {
    releaseImplementation 'es.rafaco.inappdevtools:noop:0.0.55'
    
    debugImplementation 'es.rafaco.inappdevtools:support:0.0.55'
    //debugImplementation 'es.rafaco.inappdevtools:androidx:0.0.55'
}
```

Choose only one between `androidx` or `support` artifacts, according to the Android libraries used in your project. `androidx` require Jetifier enabled.

Ready to go! Just run a debug build and our welcome dialog will pop up on your device.

For extended setup details visit our wiki:
- [Compatibility](https://github.com/rafaco/InAppDevTools/wiki/Setup#compatibility)
- [Detailed setup](https://github.com/rafaco/InAppDevTools/wiki/Setup#detailed-setup)
- [Web apps and Hybrid apps](https://github.com/rafaco/InAppDevTools/wiki/Setup#hybrid-apps)
- [Including additional modules](https://github.com/rafaco/InAppDevTools/wiki/Setup#including-additional-gradle-modules-optional)


## Configuration <a name="configuration"/>

You can easily configure our library behaviour at **build time** by using our gradle extension on your app module's build.gradle. This configuration also affect our plugin behaviour and cleaning your app's data will restore to this values.
```gradle
apply plugin: 'es.rafaco.inappdevtools'

inappdevtools {
    enabled = true
    email = 'yourmail@yourdomain.com'
    notes = 'This compilation fix the following issues:..'
}
```
All available properties with descriptions can be found in our wiki. <a href="https://github.com/rafaco/InAppDevTools/wiki/Configurations">Read More</a>.

You can also override your build configuration at **run time** from our UI (Overlay toolbar > More > Setting) or programmatically calling us from your sources. Runtime values will be lost when cleaning your app data, restoring the build ones from our gradle extension.
```java
Iadt.getConfig().setBoolean(BuildConfigField.ENABLED, false);
Iadt.restartApp();
```

## Important considerations

### Debug vs Release compilation

Our goal is to enhance your internal compilation without interfering in your production compilations. Our default configuration assume that your debug compilations are for internal use and your release ones are for production, but you can adjust it.

You can disable our library and plugin in your **debug builds** by setting `enabled = false` in configuration or using our `noop` library. [Read more](https://github.com/rafaco/InAppDevTools/wiki/Configurations#1-enabled).

We have a **release protection mechanism** to auto-disable everything on your release builds even if you forget to use our `noop` artifact or if your configuration have `enabled = true`. On release builds, our plugin will not perform any of their tasks and our `androidx` and `support` libraries will behave like the `noop` one.

To enable our library and plugin in your **release builds** you have to explicitly override our protection mechanism by setting `enabledOnRelease = true` in configuration. [Read more](https://github.com/rafaco/InAppDevTools/wiki/Configurations#2-enabled-on-release).

### Source code exposition <a name="exposed_sources"/>

When this library is enabled, **your source code get exposed to anyone who get your APK**. It can be navigated and visualized throw our UI and someone could also extract all of them from your APK file, un-compiled.

You can adjust this behaviour to your needs, excluding some sources or disabling related features. [Read more](https://github.com/rafaco/InAppDevTools/wiki/Configurations#3-source-inclusion-and-source-inspection) .


## Usage <a name="usage"/>
After the [setup](#setup) you only need to *Run* a debug build of your app into a real device or emulator. Our welcome dialog will pop up.

### Invocation <a name="invocation"/>
On crash our UI will automatically popup but you can also invoke it at any time by using one of the following methods:
- Shake your device with your app on foreground
- Tap our floating icon
- Tap our notification (disabled by default, enable it with configuration)
- Or programmatically calling `Iadt.show();`


## Integrations

### Include compilation notes
You can provide any text to describe your compilation using `notes` configuration. This is very useful to describe changes or to provide instructions and it will be show on first dialog and on BuildInfo panel. 
```gradle
inappdevtools {
    notes = "This is a NOTE about this compilation:\n" +
            " - Multiline supported"
```

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


## Contributing and building instructions [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)

First off, thank you for considering contributing to InAppDevTools. It's people like you that make InAppDevTools such a great tool. There are many ways to contribute starting from giving us a :star:, recommending this library to your friends :loudspeaker: or sending us your feedback :love_letter:. Check out our [CONTRIBUTING.md](CONTRIBUTING.md) guide.


## About the author

I started this project to get the log from a real user located in another country. He has a nasty bug that we were unable to reproduce and we detect the issue by sending him a special apk. Then I added an overlay to see the logs over our app and the first info panels... and it became my personal tool for my daily duties as developer.

After tons of overnight coding fun, I've left my work position to fully focus on this amazing project for a while. I hope to publish a first complete version around Spring 2020.

As a Senior Software Engineer, I have always worked on proprietary software and this is my first open source project. I am really excited to give back what I received during all this years and I'm looking forward to create a friendly community around this project. Please feel free to correct me, give me any advise or pointing me in the right direction.


## Links <a name="links"/>
- External references:
    - [inappdevtools.org](https:/inappdevtools.org). Our new website (under construction)
    - \[Your link here\] Write a public entry and notify us!
- Apps using this library:
    - ~~[InAppDevTools Demo](https://play.google.com)~~ Coming soon at Google Play
    - \[Your app here\] Let us know and get a link to your app.


## Thanks <a name="thanks"/>
- To [@whataa](https://github.com/whataa) for [Pandora](https://github.com/whataa/pandora), key for layout, network and storage inspection :trophy:
- To [@Zsolt Kocsi](https://github.com/zsoltk) for [Paperwork](https://github.com/zsoltk/paperwork), inspiration for CompileConfig
- To [@tiagohm](https://github.com/tiagohm) for [CodeView](https://github.com/tiagohm/CodeView)
- To [@alorma](https://github.com/alorma) for [TimelineView](https://github.com/alorma/timelineview)
- To [@valdesekamdem](https://github.com/valdesekamdem) for [MaterialDesign-Toast](https://github.com/valdesekamdem/MaterialDesign-Toast)
- To [@rnevet](https://github.com/rnevet) for [WCViewPager](https://github.com/rnevet/WCViewPager)
- To [@SalomonBrys](https://github.com/SalomonBrys) for [ANR-WatchDog](https://github.com/SalomonBrys/ANR-WatchDog)
- To [@nisrulz](https://github.com/nisrulz) for [EasyDeviceInfo](https://github.com/nisrulz/easydeviceinfo)
- To [Android Open Source Project](https://source.android.com/) for [Arch](https://developer.android.com/topic/libraries/architecture)

## License <a name="license"/>
```
Copyright 2018-2020 Rafael Acosta Alvarez

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
