# DevToolsLib

~~*A bunch of tools to speed up your development from within your own app. Build as a companion for each stage*~~

*A library that provide your apk with a bunch of useful tools carefully preconfigured for each step of your app live cycle (development, test and production).*



(For testers) Just installing it, your apk will nicely treat any crash. It prompt the error and allow the user to report the bug by email. Additional information could be typed and the report will auto include the exception, the full logcat since the app started, a custom activity log and more info like versions, device, os, hardware, memory, storage...

(For developer) Your app also get an overlay icon with tools to inspect on runtime and on the go. 

It have few preconfigured modes based on how aggressively the library integrate with your apk and their users.

- Develop
    - Overlay: Always accessible via overlay icon
    - OnCrash: see full info (exception, tracestack, log, inspect, report...)
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


**Features**

- Easy to install
- Accessible everywhere (Overlay icon)
- Non intrusive with your views, stack or focus (system overlay layer)
- Only on release mode 

**Screenshoots**

-




***

## Instalation

- Add library as a gradle dependency
- Call DevTools.Init() inside Application.onCreate()
- TODO: 

## Configuration

- Predefined configurations
- Using the ConfigBuilder

## Customization

- Create a tool
- Replace IconWidget by your own trigger