<!-- # InAppDevTools -->
<p align="center">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/images/social.png" width="75%">
</p>

<p align="center">
  <a href="https://github.com/rafaco/InAppDevTools/releases" alt="Version">
      <img src="https://img.shields.io/maven-metadata/v/https/jcenter.bintray.com/es/rafaco/inappdevtools/support/maven-metadata.xml.svg?colorB=blue&label=Version&style=flat-square"/></a>
  <a href="https://github.com/rafaco/InAppDevTools/commits" alt="Maturity">
      <img src="https://img.shields.io/badge/Maturity-development-orange.svg?style=flat-square"/></a>
  <a href="https://git.io/IADT" alt="ShortLink">
      <img src="https://img.shields.io/badge/ShortLink-git.io%2FIADT-blueviolet.svg?style=flat-square"/></a>
  <!-- <a href="https://android-arsenal.com/details/1/887" alt="Android Arsenal">
      <img src="https://img.shields.io/badge/Android%20Arsenal-InAppDevTools-brightgreen.svg?style=flat"/></a> -->
  <a href="https://www.openhub.net/p/InAppDevTools" alt="OpenHub">
      <img src="https://www.openhub.net/p/InAppDevTools/widgets/project_thin_badge.gif"/></a>
</p>

**A library to enhance the internal compilations of Android apps, adding usefull tools for their development team. It allows to inspect, report and debug your app from the same  screen when it's running, over your app.**

Conceptually this's similar to Chrome DevTools but inside your app instead of in your browser. It allows to inspect, analyze and modify a running app from within it. Our interface get shown over your activities while you use them, helping you to understand what's really happening underneath in order to highlight issues and bug causes. 

- Get exclusive info about your running app (processes, task, threads, services...), your build (user, host, remote repo, local changes...), your app, the device and his OS.
- View crash details immediately with graphic stacktrace, causing source lines, previous logs and screenshot.
- Reports crashes or issues directly to developers, including a zip with logs, screenshots and other info.
- Inspect your standard logs, view layout, source code (original and generated) and storage (db, SharedPrefs and Files).
- Auto log events and create reproduction steps (navigation, network requests, lifecycle events, crashes, ANRs, device events, user interactions...).
- Modify your app behaviour on runtime by editing your view layout or changing values in your db or SharedPrefs.
- Easy installation and configuration. No Application class override needed, all from Gradle.
- Customize our tools to your needs, add buttons to run your methods and use our dev helpers.

*Keywords: Auto-logger, crash handler, source browser, layout inspector, storage editor, logcat viewer, network activity, info panels, flexible reports, class/method tracker, coding helpers and much more.*

<p align="center">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/images/screenshots.gif">
</p>

## Setup <a name="setup"/>

Include our plugin and our libraries in your Gradle files and that's it! We will pop up in your debug compilations.

On your **root build.gradle** file:

```gradle
buidscript {...}

plugins {
    id "es.rafaco.inappdevtools" version "0.0.56" apply false
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
    releaseImplementation 'es.rafaco.inappdevtools:noop:0.0.56'
    
    debugImplementation 'es.rafaco.inappdevtools:support:0.0.56'
    //debugImplementation 'es.rafaco.inappdevtools:androidx:0.0.56'
}

inappdevtools {
    enabled = true
    teamName = 'YourTeam'
    teamEmail = 'youremail@yourdomain.com'
    notes = 'First compilation notes, replace me on following ones.'
}
```

1. Choose only one artifact between `androidx` or `support`, according to the Android libraries used in your project. `androidx` require Jetifier enabled.
2. `inappdevtools` is our [configuration](https://github.com/rafaco/InAppDevTools/wiki/Configurations) closure. Fill your team name and email for reports by now.
3. Ready to go! Just run a debug build and our welcome dialog will pop up on your device.

For extended setup details visit our wiki:
- [Compatibility](https://github.com/rafaco/InAppDevTools/wiki/Setup#compatibility)
- [Detailed setup](https://github.com/rafaco/InAppDevTools/wiki/Setup#detailed-setup)
- [Configurations](https://github.com/rafaco/InAppDevTools/wiki/Configurations)
- [Web apps and Hybrid apps](https://github.com/rafaco/InAppDevTools/wiki/Setup#hybrid-apps)
- [Including additional modules](https://github.com/rafaco/InAppDevTools/wiki/Setup#including-additional-gradle-modules-optional)

<!--
## Configuration <a name="configuration"/>

You can easily configure our library behaviour at **build time** by using our gradle extension on your app module's build.gradle. This configuration also affect our plugin behaviour and cleaning your app's data will restore to this values.
```gradle
apply plugin: 'es.rafaco.inappdevtools'


```
All available properties with descriptions can be found in our wiki. <a href="https://github.com/rafaco/InAppDevTools/wiki/Configurations">Read More</a>.

You can also override your build configuration at **run time** from our UI (Overlay toolbar > More > Setting) or programmatically calling us from your sources. Runtime values will be lost when cleaning your app data, restoring the build ones from our gradle extension.
```java
Iadt.getConfig().setBoolean(BuildConfigField.ENABLED, false);
Iadt.restartApp();
```
-->

## Important considerations

### Debug vs Release compilation

Our goal is to enhance your internal compilation without interfering in your production compilations. Our default configuration assume that your debug builds are your internal compilations and release ones are for production. So, with default configuration:

 * Your **`Debug` builds** will have this **library ENABLED**.
 * Your **`Release` builds** will have this **library DISABLED** and we have a **release protection mechanism** to auto-disable everything on your release builds even if you enable it by mistake.

You can adjust which builds enable or library and which ones have it disabled. You can also override our protection mechanism. [Read more](https://github.com/rafaco/InAppDevTools/wiki/Configurations/_edit#debug-vs-release-compilation).

### Source code exposition <a name="exposed_sources"/>

When this library is enabled, **your source code get exposed to anyone who get your APK**. It can be navigated and visualized throw our UI and someone could also extract all of them from your APK file, un-compiled.

You can adjust this behaviour to your needs, excluding some sources or disabling related features. [Read more](https://github.com/rafaco/InAppDevTools/wiki/Configurations#3-source-inclusion-and-source-inspection) .


## Usage <a name="usage"/>

<table border="0"><tr><td>

After the [setup](#setup) process, you only need to *Run* a debug build of your app into a real device or emulator. 

Our **welcome dialog** will pop up on first start and every time you deploy a new build over the device. It gives some information, allow to disable our tools, help user in accepting permissions required.

</td><td width="40%"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/overlays/Welcome_Screen.png"></td></tr></table>

<table border="0"><tr><td width="40%"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/overlays/Home_Screen.png"></td><td>

You can **invoke our UI** at any time by tapping the new floating icon that appear over your app or by shaking your device with your app on foreground. 

If your app crash, our UI will **automatically popup** showing full details about the crash and allowing to report it.

</td></tr></table>

<!-- ### Invocation <a name="invocation"/>
On crash our UI will automatically popup but you can also invoke it at any time by using one of the following methods:
- Shake your device with your app on foreground
- Tap our floating icon
- Or programmatically calling `Iadt.show();` -->


## Integrations

There are multiple ways to integrate your app with our library for a better customization or to improve the experience of your internal users. All this methods will be safely ignored in your release compilations (disabled config or noop artifacts).

### Compilation notes
You can provide any text to describe your current build or compilation by using `notes` configuration. This is very useful to describe changes or to provide instructions. This message will be show on welcome dialog, team screen and build screen.
```gradle
inappdevtools {
    notes = "This is a NOTE about this compilation:\n" +
            " - Multiline supported"
```

### Team configuration
You can customize a lot of things in our 'Team Screen' using Gradle configuration.
```gradle
inappdevtools {
    teamName = "DemoTeam"
    teamEmail = 'inappdevtools@gmail.com'
    teamDesc = "Team description or any text you want to show on top of Team screen. Change it with 'teamDesc' configuration."
    teamLinks = [ website   : "http://inappdevtools.org",
                  repo      : "https://github.com/rafaco/InAppDevTools",
                  issues    : "https://github.com/rafaco/InAppDevTools/issues",
                  readme    : "https://github.com/rafaco/InAppDevTools/blob/master/README.md" ]
}
```

### Team actions
You can also add handy buttons to the 'Team screen' to perform any logic or call any of your methods. Define it using a ```ButtonFlexData``` instance, when you can specify some details for your button (message, icon, color...) and the action itself in a ```Runnable```. Add them on startup (i.e. onCreate of your app or main activity) or dynamically at any point (i.e. after user log in).

```java
 Iadt.addTeamAction(new ButtonFlexData("Show message",
                R.drawable.ic_run_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        YourClass.yourMethod("param");
                    }
                }));
```

### Custom events
You can create and fire your own events manually. These events will be shown on our log screen mixed with your logcat logs and our events. It will also appear in reproduction steps if it has a verbosity greater than Info (I, W and E).

```java
 new IadtEvent()
      .setMessage("Custom event sample: User logged in")
      .setExtra(userName)
      .setCategory("YourCategory")
      .setSubcategory("UserLogIn")
      .setSeverity("I")
      .fire();
```


## Contributing and building instructions

First off, thank you for considering contributing to InAppDevTools. It's people like you that make InAppDevTools such a great tool. There are many ways to contribute starting from giving us a :star:, recommending this library to your friends :loudspeaker: or sending us your feedback :love_letter:.

Check out our [CONTRIBUTING.md](CONTRIBUTING.md) document and our [Coding contributions guide](https://github.com/rafaco/InAppDevTools/wiki/Coding-contributions-guide) in our Wiki.


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

