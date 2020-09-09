<!-- # InAppDevTools -->
<p align="center">
 <img src="https://github.com/rafaco/InAppDevTools/wiki/images/social.png" width="75%">
</p>

<p align="center">
  <a href="https://www.android.com/" alt="Platform">
      <img src="https://img.shields.io/badge/Platform-Android-green.svg?style=flat-square"/></a>
  <a href="https://github.com/rafaco/InAppDevTools/releases" alt="Version">
      <img src="https://img.shields.io/maven-metadata/v/https/jcenter.bintray.com/es/rafaco/inappdevtools/support/maven-metadata.xml.svg?colorB=blue&label=Version&style=flat-square"/></a>
  <a href="https://github.com/rafaco/InAppDevTools/commits" alt="Maturity">
      <img src="https://img.shields.io/badge/Maturity-development-orange.svg?style=flat-square"/></a>
  <a href="https://git.io/IADT" alt="ShortLink">
      <img src="https://img.shields.io/badge/ShortLink-git.io%2FIADT-blueviolet.svg?style=flat-square"/></a>
  <!-- <a href="https://android-arsenal.com/details/1/887" alt="Android Arsenal">
      <img src="https://img.shields.io/badge/Android%20Arsenal-InAppDevTools-brightgreen.svg?style=flat"/></a> 
  <a href="https://www.openhub.net/p/InAppDevTools" alt="OpenHub">
      <img src="https://www.openhub.net/p/InAppDevTools/widgets/project_thin_badge.gif"/></a>-->
</p>

**A library to enhance the internal compilations of Android apps, adding usefull tools for their development team. It allows to inspect, report and debug your app from the same  screen when it's running, over your app.**

For developers this is conceptually similar to Chrome DevTools but packed inside your app. We provide tools to inspect, analyze and modify a running app from within it without needing a computer or a cable. It helps to understand what's really happening underneath in order to highlight issues and bug causes. 

For tester and other internal users, this library provide exact information of what they are testing and allows to easily report any crash or isuess founded.  These reports include a zip with valuable information for developers like apk info, repro steps, logs, screenshots, etc.

Your production users get neither any of our features nor apk size increase. They get a spotless app which has been thorughly polish in an agile development process, enhanced by our tools :)


<p align="center"><b>Tools</b></p>

Auto-logger, repro step generator, logcat viewer, crash handler, reports, source browser, layout inspector, component browser, storage editor, network activity, info panels (apk , build, repo, device and os), coding helpers and much more.


<p align="center"><b>Characteristics</b></p>  

- Usable everywhere without cable, our UI overlap your app.
- Easy to install, just add our dependencies to your Gradle files.
- Careful with your releases, where everything will be auto disabled by default.
- Flexible to configure via Gradle extension.
- Handy integrations available to improve the experience of your testers and to assist your developers.

<p align="center"><b>Features</b></p>  
<table border="0">
<tr><td width="30%" align="center"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_Info.gif" height="15%"></td><td>
 
**Testing environment info**  
Get detailed information about what are you testing and where. The build process (variant, type, date, machine, user, gradle versions, dependencies,...), the sources used (remote repo status, local repo, commits and change diffs...), the resulting app (manifest, version, namespace, signing, installation...), the device where is running (model, hardware, battery, sensors...) and their operative system (version, status, memory, storages, installed apps...).
</td></tr>
<tr><td width="30%" align="center"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_Team.gif" height="15%"></td><td>

**Team resources and reports**  
Provide your own resources for your internal users via Gradle configuration (team name, description, build notes, external links and action buttons. Your users can easily send report directly to the development team. Reports can include a zip with all gathered data (environment info, logs, screenshots, crash details, network request, logic snapshot...).
</td></tr>
<tr><td width="30%" align="center"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_Crash.gif" height="15%"></td><td>
 
 **Inmediate crash visualization and report**  
We intercept any exception and show their details immediately en the same screen where it happen. These include app status, current activity, logs, screenshots and graphic stacktrace with navigation to causing source lines. Crashes can be reported via email and we will include a zip with all gathered details. 
</td></tr>
<tr><td width="30%" align="center"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_Logs.gif" height="15%"></td><td>
 
**Logs, reproduction steps and advance events**  
Browse the standard logcat output from your sessions as you use your app. They are surrounded by our auto generated events to give you more context. Our event cover from basic reproduction steps (user interactions, navigations, network activity...) to advanced entries (lifecycle events, crashes, ANRs, device events...).
</td></tr>
<tr><td width="30%" align="center"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_UI.gif" height="15%"></td><td>
 
**View inspector**  
Navigate throught your current layout components by touching elements or by browsing your hierarchy. Modify xml properties straigh away and see the results in your screen.
Browse you current components and their sources (tasks, activity and fragments), zoom your screen, measure elements and take screenshots.
</td></tr>
<tr><td width="30%" align="center"><img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Animated/Screenshots_Logic.gif" height="15%"></td><td>
 
**Logic and network inspector**  
Get details about your running logic components (processes, threads, services, content providers and broadcast receivers) and inspect the network request/responses between your backend and your app. Browse and edit your storages (databases, shared preferences and files) and edit their values.
</td></tr>
</table>


## Setup <a name="setup"/>

You only need to modify 2 gradle files. On your **root build.gradle** file:

```gradle
buidscript {...}

plugins {
    id "es.rafaco.inappdevtools" version "0.0.56" apply false           // 1.
}

allprojects {
    repositories {
        maven { url "https://jitpack.io"}                               // 2.
    }
}
```
<details><summary align="center">Show details</summary><p>
 
1. Add our plugin in your `plugins` closure, which should be just before `buildscript`.
2. Add jitpack to `allprojects`, `repositories`.

</br></p></details>

On your **app** module **build.gradle** file:

```gradle
apply plugin: 'com.android.application'
apply plugin: 'es.rafaco.inappdevtools'                                 // 1.

android {
    ...
}

dependencies {
    releaseImplementation 'es.rafaco.inappdevtools:noop:0.0.56'         // 2.
    
    debugImplementation 'es.rafaco.inappdevtools:support:0.0.56'        // 3.
    //debugImplementation 'es.rafaco.inappdevtools:androidx:0.0.56'
}

inappdevtools {                                                         // 4.
    enabled = true
    teamName = 'YourTeam'
    teamEmail = 'youremail@yourdomain.com
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

I started this project while I was working on an international flight information app. We had a user located in another country with a really nasty bug that we were completely unable to reproduce. I send him a special apk that will send us back all their logs on crash. We were able to identify the problem straight away.

Days before I added an overlay to see the logs over our running app and the first info panels... and it became my personal tool for my daily duties as developer. I carry on adding more tools and starting to realise that it could be useful for other Android developers as well. Few months later, I quit my job to fully focus on this amazing project for a while.

I am very excited with the results obtained and I'm looking forward to create a friendly community around this project. This is my first open source project so I have a lot to learn. Advices and corrections will be more than welcome.


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

