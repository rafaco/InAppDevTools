# Introduction

First off, thank you for considering contributing to InAppDevTools. It's people like you that make InAppDevTools such a great tool. InAppDevTools is an open source project and we love to receive contributions from our community — you! 

There are many ways to contribute, starting from telling your coworkers or in your social networks, sending us your feedback, writing tutorials or blog posts, improving the documentation, submitting bug reports or feature requests, translating or writing code which can be incorporated into InAppDevTools itself.


# Non coding contributions

## Help us spreading
You can start right now by giving this repo a GitHub start or telling your developer friends about us at work or in your social networks. It would be awesome if you write a tutorial or a blog post. Please notify us about publications and we will link it.

## Feedback
In this early stage your feedback is so valuable for us. Share us any thoughts or experiences you have had using our library, what did you like, what annoy you, what would you change... Open a [Feedback issues](https://github.com/rafaco/InAppDevTools/issues/new/choose) or mail us privately.

## Bug report
Open a [Bug Report issues](https://github.com/rafaco/InAppDevTools/issues/new/choose) to let us know about any failure.

## Security issues and private requests:
If you find a security vulnerability, do NOT open a public issue. Email [inappdevtools@gmail.com](mailto:inappdevtools@gmail.com) instead.

## Feature request
Open a [Feature Request issues](https://github.com/rafaco/InAppDevTools/issues/new/choose) to suggest any idea for this project.

## Support
Work in progress: We have to setup a proper support channel. Meanwhile you can open a [Support issues](https://github.com/rafaco/InAppDevTools/issues/new/choose) or mail us with any question you may have.

## Private contact
If you prefer to contact us directly in a private way, you can always send us an email to [inappdevtools@gmail.com](mailto:inappdevtools@gmail.com).


# Coding contributions

Standard user don't need to manually download or build our artifacts as they are available at public repositories preconfigured by Android Studio. Just follow the installation process in our [README](README.md#setup) and rebuild your project. 

Following instructions are for user that want build their own version of InAppDevTools with changes. Remember to contribute your changes to the community using a PR.

### Artifacts

All artifacts are generated from a single project [hosted in this repo](https://github.com/rafaco/InAppDevTools), each one using different combinations of Gradle module and variant.

| Artifact | Module | Variant | Description | Publication |
| --- | --- | --- | --- | --- |
| es.rafaco.inappdevtools | [plugin](/plugin) | - | IADT plugin for Gradle| [Gradle Plugin Portal](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) |
| es.rafaco.inappdevtools:support | [library](/library) | support | IADT library for Support libraries | [Bintray](https://bintray.com/rafaco/InAppDevTools/support) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/support/) |
| es.rafaco.inappdevtools:androidx | [library](/library) | androidx | IADT library for AndroidX libraries | [Bintray](https://bintray.com/rafaco/InAppDevTools/androidx) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/androidx/) |
| es.rafaco.inappdevtools:noop | [noop](/noop) | - | IADT library, no operational | [Bintray](https://bintray.com/rafaco/InAppDevTools/noop) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/noop/) |
| es.rafaco.compat:support | [compat](/compat) | support | Compat library for Support libraries | [Bintray](https://bintray.com/rafaco/Compat/support) / [jCenter](http://jcenter.bintray.com/es/rafaco/compat/support/) |
| es.rafaco.compat:androidx | [compat](/compat) | androidx | Compat library for AndroidX libraries | [Bintray](https://bintray.com/rafaco/Compat/androidx) / [jCenter](http://jcenter.bintray.com/es/rafaco/compat/androidx/) |
| es.rafaco.iadt.demo | [demo](/demo) | androidx/support | Demo app  | ~~[Google Play](https://play.google.com)~~ |

### Build configurations
We have added 23 shared build configurations to the project. Browse their categories on Android Studio for our custom build tasks.
//TODO: document build configurations.

### Switching Androidx/Support build

Work in progress: We use a gradle preprocessor to switch between AndroidX or Support libraries in our modules for each variant. This affect our java source and gradle build files. AndroidX build also require 'android.useAndroidX' properties but we set it dynamically passing following command line args: -Pandroid.useAndroidX=true -Pandroid.enableJetifier=true. 

Our Android Studio project have "Run Configurations" for AndroidX an Support builds per each module and with correct command line args already set.

This params override selections on Android Studio "Build Variant" panel. To manually build AndroidX variants of any submodule, remove command line args and restore properties android.useAndroidX=true and android.enableJetifier=true at gradle.properties.

Sources at our repo should always be ready to build support variant, as it also works on AndroidX project but not the other side around. Test your build switching to Androidx but perform a last build using a support build before committing, to restore support sources.

## Continuous Integration <a name="ci"/>

We use CircleCi to automatise tests for commit and for PR. We currently only test support builds and generate some reports. Unit test are not implemented.

1. Build
    1. Assemble and include PLUGIN
    2. Assemble and local publish COMPAT SupportDebug
    3. Assemble and local publish COMPAT AndroidXDebug
    4. Assemble NOOP Debug
    5. Assemble LIBRARY SupportDebug
    6. Assemble DEMO SupportDebug
    7. Assemble LIBRARY AndroidxDebug
    8. Assemble DEMO AndroidxDebug
2. Report:
    1. Lint report LIBRARY SupportDebug
    2. Lint report DEMO SupportDebug
    3. Sonar report ALL SupportDebug and upload to SonarCloud

Following badges are related to master branch. For other branches check at [https://circleci.com/gh/rafaco/InAppDevTools/](https://circleci.com/gh/rafaco/InAppDevTools/) after committing your branch and before sending your Pull Request.

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

### Pull requests

Use a Pull Request to send us your changes and we will integrate them as soon as possible. 

Working on your first Pull Request? You can learn how from this *free* series, [How to Contribute to an Open Source Project on GitHub](https://egghead.io/series/how-to-contribute-to-an-open-source-project-on-github). Feel free to ask for help; everyone is a beginner at first 😸


## Coding standards
//TODO

_Ideas extracted from the following [template] (https://github.com/nayafia/contributing-template)._
