# Introduction

First off, thank you for considering contributing to InAppDevTools. It's people like you that make InAppDevTools such a great tool. InAppDevTools is an open source project and we love to receive contributions from our community — you! 

There are many ways to contribute, starting from telling your coworkers or in your social networks, sending us your feedback, writing tutorials or blog posts, improving the documentation, submitting bug reports or feature requests, translating or writing code which can be incorporated into InAppDevTools itself.


# Non coding contributions

## Help us spreading
You can start right now by giving this repo a GitHub start or telling your developer friends about us at work or in your social networks. It would be awesome if you write a tutorial or a blog post. Please notify us about publications and we will link it.

## Feedback
In this early stage your feedback is so valuable for us. Share us any thoughts or experiences you have had using our library, what did you like, what annoy you, what would you change... Open a [Feedback issues](https://github.com/rafaco/InAppDevTools/issues) or mail us privately.

## Bug report
Open a [Bug Report issues](https://github.com/rafaco/InAppDevTools/issues) to let us know about any failure.

## Security issues and private requests:
If you find a security vulnerability, do NOT open a public issue. Email [inappdevtools@gmail.com](mailto:inappdevtools@gmail.com) instead.

## Feature request
Open a [Feature Request issues](https://github.com/rafaco/InAppDevTools/issues) to suggest any idea for this project.

## Support
Work in progress: We have to setup a proper support channel. Meanwhile you can open a [Support issues](https://github.com/rafaco/InAppDevTools/issues) or mail us with any question you may have.

## Private contact
If you prefer to contact us directly in a private way, you can always send us an email to [inappdevtools@gmail.com](mailto:inappdevtools@gmail.com).


# Coding contributions

## Building our project

Standard user don't need to manually download or build our artifacts as they are available at public repositories preconfigured by Android Studio. Just follow the installation process in our [README](https://github.com/rafaco/InAppDevTools/README.md#setup) and rebuild your project.

### Building artifacts

All artifacts are generated from [this GitHub repo](https://github.com/rafaco/InAppDevTools), each one using different combinations of module and variant.[Readme](https://github.com/rafaco/InAppDevTools/Readme.md#setup) 

| Artifact | Module | Variant | Description | Publication |
| --- | --- | --- | --- | --- |
| es.rafaco.inappdevtools | plugin | - | IADT plugin for Gradle| [Gradle Plugin Portal](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) |
| es.rafaco.inappdevtools:support | library | support | IADT library for Support libraries | [Bintray](https://bintray.com/rafaco/InAppDevTools/support) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/support/) |
| es.rafaco.inappdevtools:androidx | library | androidx | IADT library for AndroidX libraries | [Bintray](https://bintray.com/rafaco/InAppDevTools/androidx) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/androidx/) |
| es.rafaco.inappdevtools:noop | noop | - | IADT library, no operational | [Bintray](https://bintray.com/rafaco/InAppDevTools/noop) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/noop/) |
| es.rafaco.compat:support | compat | support | Compat library for Support libraries | [Bintray](https://bintray.com/rafaco/Compat/support) / [jCenter](http://jcenter.bintray.com/es/rafaco/compat/support/) |
| es.rafaco.compat:androidx | compat | androidx | Compat library for AndroidX libraries | [Bintray](https://bintray.com/rafaco/Compat/androidx) / [jCenter](http://jcenter.bintray.com/es/rafaco/compat/androidx/) |
| es.rafaco.iadt.demo | demo | androidx/support | Demo app  | ~~[Google Play](https://play.google.com)~~ |

## Continuous Integration <a name="ci"/>
Work in progress: We currently build our Support flavor and perform lint analisys. Unit test not implemented.

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

### Build configurations
We have added 23 shared build configurations to the project. Browse their categories on Android Studio for a quick usage of build tasks.

### Switching Androidx/Support build

Work in progress: We use a gradle preprocessor to switch between AndroidX or Support libraries in our modules for each variant. This affect our java source and gradle build files but we are not able to dynamically changes lines on gradle.properties. Comment/uncomment of a couple of lines is required at our root folder gradle.properties when changing the variant.

```gradle
# gradle.properties for AndroidX variant:
android.useAndroidX=true
android.enableJetifier=true

# gradle.properties for Support variant:
android.useAndroidX=false
android.enableJetifier=false
```

Sources at our repo should always be ready to build support variant, as it also works on AndroidX project but not the other side around. Test your build switching to Androidx but perform a last build using a support build type to restore support sources before your PR.

### Coding standard
//TODO


### Pull requests

Use a Pull Request to send us your changes and we will integrate them as soon as possible. 

Working on your first Pull Request? You can learn how from this *free* series, [How to Contribute to an Open Source Project on GitHub](https://egghead.io/series/how-to-contribute-to-an-open-source-project-on-github). Feel free to ask for help; everyone is a beginner at first 😸



_Ideas extracted from the following [template] (https://github.com/nayafia/contributing-template)._