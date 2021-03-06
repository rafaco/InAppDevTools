<!-- # InAppDevTools -->
<p align="center">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/images/social.png" width="50%">
</p>

<p align="center">
  <a href="https://www.android.com/" alt="Platform">
      <img src="https://img.shields.io/badge/Platform-Android-green.svg?style=flat"/></a>
  <a href="https://github.com/rafaco/InAppDevTools/wiki/Coding-contributions-guide#project-structure" alt="Artifacts">
      <img src="https://img.shields.io/badge/Artifacts-Plugin_and_libraries-purple.svg?style=flat"/></a>
  <a href="https://github.com/rafaco/InAppDevTools/commits" alt="Maturity">
      <img src="https://img.shields.io/badge/Maturity-development-orange.svg?style=flat"/></a>
  </br>
  <a href="https://github.com/rafaco/InAppDevTools/releases" alt="Version">
      <img src="https://img.shields.io/maven-metadata/v/https/jcenter.bintray.com/es/rafaco/inappdevtools/support/maven-metadata.xml.svg?colorB=blue&label=Version&style=flat"/></a>
  <a href="https://circleci.com/gh/rafaco/InAppDevTools/tree/master" alt="CircleCi Status">
      <img src="https://circleci.com/gh/rafaco/InAppDevTools/tree/master.svg?style=shield"/></a>
  <a href="https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools" alt="Sonar Status">
      <img src="https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=alert_status"/></a>
  <a href="https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools" alt="Sonar lines count">
      <img src="https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=ncloc"/></a>
  </br>
  <a href="https://git.io/IADT" alt="ShortLink">
      <img src="https://img.shields.io/badge/ShortLink-git.io%2FIADT-blueviolet.svg?style=flat&logo=github"/></a>
  <a href="https://www.openhub.net/p/InAppDevTools" alt="OpenHub">
      <img src="https://www.openhub.net/p/InAppDevTools/widgets/project_thin_badge.gif"/></a>
  <!-- <a href="https://android-arsenal.com/details/1/887" alt="Android Arsenal">
      <img src="https://img.shields.io/badge/Android%20Arsenal-InAppDevTools-brightgreen.svg?style=flat"/></a> 
  <a href="https://www.openhub.net/p/InAppDevTools" alt="OpenHub">
      <img src="https://www.openhub.net/p/InAppDevTools/widgets/project_thin_badge.gif"/></a>-->
</p>

**A library to enhance the internal compilations of any Android app, adding useful tools for their early stage users. They can get info on what they are testing, send comprehensive crash reports and deeply inspect their running app on the go. It also bring a customizable team panel and other helpful tools, all within your app.**

<p align="center">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/Lunatic/NonDevBanners/ScreenBanner1.gif" width="24%">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/Lunatic/NonDevBanners/ScreenBanner2.gif" width="24%">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/Lunatic/NonDevBanners/ScreenBanner3.gif" width="24%">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/Lunatic/NonDevBanners/ScreenBanner4.gif" width="24%">
</p>
<p align="center">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/Lunatic/Devs/Screen1.gif" width="24%">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/Lunatic/Devs/Screen2.gif" width="24%">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/Lunatic/Devs/Screen3.gif" width="24%">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/Lunatic/Devs/Screen4.gif" width="24%">
</p>

**All your internal users** (QA, managers, client, beta testers...) can get precise information on what they are testing and use an exclusive panel customized by the dev team with actions and resources. They also can send contextualized reports, which automagically include highly valuable information like repro steps, screenshots, crash details, full logs and environment info (app, build, repo status, device and OS).</p>

**For developers** this is conceptually similar to Chrome DevTools but packed inside your app, a revolutionary concept that enhance their daily compilations. We provide a complete set of tools to inspect, analyze and modify a running app from within it, without cable and on the go. It helps them to understand what's really happening underneath in order to highlight issues and bug causes. They also receive comprehensive reports, can customize our tools for others and can make use of some coding helpers.

**Production users** get neither any of our features nor apk size increase. They get a spotless app thoroughly polished in an agile development process, enhanced by our tools :)


#### Tools
Auto-logger, repro step generator, logcat viewer, crash handler, reports, source browser, layout inspector, component browser, storage editor, network activity, info panels (apk , build, repo, device and os), coding helpers and much more.


#### Characteristics
- Usable everywhere without cable, our UI overlap your app.
- Easy to install, just add our dependencies to your Gradle files.
- Careful with your releases, where everything will be auto disabled by default.
- Flexible to configure via Gradle extension.
- Handy integrations available to improve the experience of your testers and to assist your developers.

#### Features

<table border="0">
<tr><td width="2%" align="center" valign="top"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_Info.gif"></td><td>
 
**App and device info**  
Get detailed information about what are you testing and where. The build process (variant, type, date, machine, user, gradle versions, dependencies,...), the sources used (remote repo status, local repo, commits and change diffs...), the resulting app (manifest, version, namespace, signing, installation...), the device where is running (model, hardware, battery, sensors...) and their operative system (version, status, memory, storage, installed apps...).
</td></tr>
<tr><td width="20%" align="center" valign="top"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_Team.gif"></td><td>

**Team resources and reports**  
Provide your own resources for your internal users via Gradle configuration (team name, description, build notes, external links and action buttons. Your users can easily send report directly to the development team. Reports can include a zip with all gathered data (environment info, logs, screenshots, crash details, network request, logic snapshot...).
</td></tr>
<tr><td width="20%" align="center" valign="top"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_Crash.gif"></td><td>
 
 **Crash visualization and report**  
We intercept any exception and show details immediately, on the same screen where it happen. These details include app status, current activity, logs, screenshots and graphic stacktrace with navigation to causing source lines. Crashes can be reported via email and we will include a zip with all gathered details.
</td></tr>
<tr><td width="20%" align="center" valign="top"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_Logs.gif"></td><td>
 
**Logs, reproduction steps and advance events**  
Browse the standard logcat output from your sessions as you use your app. They are surrounded by our auto generated events to give you more context. Our event cover from basic reproduction steps (user interaction, navigation, network activity...) to advanced entries (lifecycle events, crashes, ANRs, device events...).
</td></tr>
<tr><td width="20%" align="center" valign="top"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_UI.gif"></td><td>
 
**View inspector**  
Navigate through your current layout components by touching elements or by browsing your hierarchy. Modify xml properties straight away and see the results in your screen.
Browse you current components and their sources (tasks, activity and fragments), zoom your screen, measure elements and take screenshots.
</td></tr>
<tr><td width="20%" align="center" valign="top"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_Logic.gif"></td><td>
 
**Logic and network inspector**  
Get details about your running logic components (processes, threads, services, content providers and broadcast receivers) and inspect the network request/responses between your backend and your app. Browse and edit your storages (databases, shared preferences and files) and edit their values.
</td></tr>
</table>


## Setup <a name="setup"/>

You only need to modify 2 gradle files. On your **root build.gradle** file:

```gradle
buidscript {...}

plugins {
    id "es.rafaco.inappdevtools" version "0.0.58" apply false           // 1.
}

allprojects {
    repositories {
        maven { url "https://jitpack.io"}                               // 2.
    }
}
```
<details><summary align="center">Show details</summary><p>
 
1. Add our plugin in your `plugins` closure, which should be just before `buildscript`.
2. Add JitPack to `allprojects`, `repositories`.

</br></p></details>

On your **app** module **build.gradle** file:

```gradle
apply plugin: 'com.android.application'
apply plugin: 'es.rafaco.inappdevtools'                                 // 1.

android {
    ...
}

dependencies {
    releaseImplementation 'es.rafaco.inappdevtools:noop:0.0.58'         // 2.
    
    debugImplementation 'es.rafaco.inappdevtools:support:0.0.58'        // 3.
    //debugImplementation 'es.rafaco.inappdevtools:androidx:0.0.58'
}

inappdevtools {                                                         // 4.
    enabled = true
    teamName = 'YourTeam'
    teamEmail = 'youremail@yourdomain.com'
    notes = 'First build note, replace me on the next ones.'
}
```
<details><summary align="center">Show details</summary><p>

1. Apply our plugin
2. Add our `noop` for your release builds
3. Choose between `androidx` or `support` for your debug builds, according to the Android libraries in your project. `androidx` require Jetifier enabled.
4. Add our configuration closure `inappdevtools` and fill your email at least.

</br></p></details>

From now on, when building your project artifacts:

* **Iadt will be enabled on your Debug builds**: all features will be available and **your source code will be exposed** throw our UI and in your APK files.
* **Iadt will be disabled on your Release builds**: no feature will be available, your sources aren't exposed and your APK size will be minimally increased.

Ready to go! Just run a Debug build and our welcome dialog will pop up on your device.

For additional setup details visit our wiki:

 - [Compatibility](https://github.com/rafaco/InAppDevTools/wiki/Setup#compatibility)
 - [Detailed setup](https://github.com/rafaco/InAppDevTools/wiki/Setup#detailed-setup)
 - [Configurations](https://github.com/rafaco/InAppDevTools/wiki/Configurations)
 - [Redefine which are your internal compilations](https://github.com/rafaco/InAppDevTools/wiki/Configurations#debug-vs-release-compilation)
 - [Limit source code exposition](https://github.com/rafaco/InAppDevTools/wiki/Configurations#3-source-inclusion-and-source-inspection)
 - [Web apps and Hybrid apps](https://github.com/rafaco/InAppDevTools/wiki/Setup#hybrid-apps)
 - [Including additional modules](https://github.com/rafaco/InAppDevTools/wiki/Setup#including-additional-gradle-modules-optional)


## Usage <a name="usage"/>

<table border="0"><tr><td>

After the [setup](#setup) process, you only need to *Run* a debug build of your app into a real device or emulator. 

On first start, our **welcome dialog** will pop up. It gives basic information about the running apk, allows to disable our tools and helps in accepting the permission to show over your app.

</td><td width="30%"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/overlays/Welcome_Screen.png"></td></tr></table>


<table border="0"><tr><td width="30%"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/overlays/Home_Screen.png"></td><td>

You can **invoke our UI** at any time by tapping the new floating icon that appear over your app or by shaking your device with your app on foreground. It gives you access to all our tools while you keep using your app.

Our UI will **auto popup on crash**, showing full details about the crash and allowing to report it.

</td></tr></table>

<!-- ### Invocation <a name="invocation"/>
On crash our UI will automatically popup but you can also invoke it at any time by using one of the following methods:
- Shake your device with your app on foreground
- Tap our floating icon
- Or programmatically calling `Iadt.show();` -->


## Integrations

There are multiple ways to integrate your app with our library for a better customization or to improve the experience of your internal users. All this methods will be safely ignored when our library is disabled (release builds, disabled configuration or using noop artifacts).

### Customize your team info
You can customize a lot of things in the 'Team Screen' of your compilations by using our Gradle configuration. For field details, visit [configurations](https://github.com/rafaco/InAppDevTools/wiki/Configurations).
```gradle
inappdevtools {
    teamName = "DemoTeam"
    teamEmail = 'inappdevtools@gmail.com'
    teamDesc = "Team description or any text you want to show on top of Team screen. Change it with 'teamDesc' configuration."
    teamLinks = [ website   : "http://inappdevtools.org",
                  repo      : "https://github.com/rafaco/InAppDevTools"]
}
```

### Add team actions
You can easily add buttons into your 'Team screen' to perform any logic or to call any of your methods. Pass a ```ButtonFlexData``` instance to ```Iadt.addTeamAction()```, with your action in a ```Runnable``` and details for the button (message, icon, color...). Add them on startup (i.e. onCreate of your app or main activity) or dynamically at any point (i.e. after user log in).

```java
Iadt.addTeamAction(new ButtonFlexData("Call yourMethod",
        R.drawable.ic_run_white_24dp,
        new Runnable() {
            @Override
            public void run() {
                YourClass.yourMethod("someParam");
            }
        }));
```

### Add build notes
You can provide a text to describe your compilation, their changes or to provide instructions. It will be shown at welcome dialog, team screen and build screen.

Use our `notes` configuration in Gradle or modify `BuildConfigField.NOTES` at runtime:
```gradle
inappdevtools {
    notes = "This is a SAMPLE NOTE provided at buildtime by our Gradle extension"
}
```
```java
Iadt.getConfig()
    .setString(BuildConfigField.NOTES, 
            "This is a SAMPLE NOTE provided at runtime by our Java interface");
```

### Show internal messages
You can show special toast messages only for your internal users. This messages will be shown when this library is enabled and will be ignored on your release builds.

Your internal users can easily distinguish them from the standard toast as they are shown in a top position and they are colored base on the severity. This messages will auto generate an event.

```java
Iadt.buildMessage("This is a DEV message").fire();                 //Light blue (default)
Iadt.buildMessage("This is a INFO message").isInfo().fire();       //Green
Iadt.buildMessage("This is a WARNING message").isWarning().fire(); //Yellow
Iadt.buildMessage("This is a ERROR message").isError().fire();     //Red
```

### Fire your own events
You can create and fire your own events manually. These events will be shown on our log screen like any other auto-generated events. It will also appear in reproduction steps if it has a verbosity greater than Info (I, W and E).

```java
Iadt.buildEvent("Quick event sample").fire();

Iadt.buildEvent("User logged in: " + userData.getName())
    .setExtra(userData.toString())
    .setCategory("User")
    .setSubcategory("LogIn")
    .isInfo()
    .fire();
```

## Contributing and building instructions

There are many ways to help us starting from giving this project a GitHub :star:, recommending this library to your friends :loudspeaker: or sending us your feedback :love_letter:.

For more, please check out our [CONTRIBUTING.md](CONTRIBUTING.md) document and our [Coding contributions guide](https://github.com/rafaco/InAppDevTools/wiki/Coding-contributions-guide) in our Wiki.

   **Join our community and help us making your job easy! :)**


## About this project

I started this project while I was working on an international flight information app. We had a user in another country with a nasty bug that we were unable to reproduce. I send him a special apk to record their logs and to send them to us on crash. We identify the problem straight away just looking at the log.

Days after I added an overlay to see the logs over our activities and the first info panel... and it became my personal tool for my daily duties. I carried on adding more tools and I started to realise that it could be useful for other Android developers as well. Few months later, I quit my job to fully focus on this project and to make it flexible for other apps.

Year and a half later, I am very excited with the results obtained and I'm looking forward to create a friendly community to push this project to the moon, [join us!](CONTRIBUTING.md). Meanwhile, I'm currently searching for job at Madrid, ideally within an Android team open to use this tools. Check out my [LinkedIn profile](https://linkedin.com/in/rafaco).

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

