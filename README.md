[![Release](https://jitpack.io/v/eggheadgames/Siren.svg)](https://jitpack.io/#eggheadgames/Siren)

# Siren for Android

Notify users when a new version of your Android app is available, and prompt them with the Play Store link. This is a port of the iOS library of the same name: https://github.com/ArtSabintsev/Siren

## About

Siren checks a user's currently installed version of your Android app against the latest version information stored in a JSON file on a server URL you provide. (_Unfortunately, the Google public API for version checking requires a token, and due to logistics and rate limiting, it's not feasible to use the API from an Android app_).

If a new version is available, an alert can be presented to the user informing them of the newer version, and giving them the option to update the application. Alternatively, Siren can notify your app programmatically, enabling you to inform the user through alternative means, such as a custom interface.

* Siren is built to work with the Semantic Versioning system.
 * Semantic Versioning is a three number versioning system (e.g., 1.0.0)
 * Siren also supports two-number versioning (e.g., 1.0)
 * Siren also supports four-number versioning (e.g., 1.0.0.0)
* Siren is a Java language port of a [Siren](https://github.com/ArtSabintsev/Siren), an iOS Swift library that achieves the same functionality.
* Siren is actively maintained by [Egghead Games](http://eggheadgames.com)

## Features
- [x] Gradle support (using [JitPack](https://jitpack.io/))
- [x] Localized for 20+ languages (See **Localization** Section)
- [x] Three types of alerts (see **Screenshots & Alert Types**)
- [x] Optional override methods (see **Optional Override** section)
- [x] Accompanying sample Android app

## Etc...

This is a work in progress...  It began March 9 2016 and is in active development.

## How to use
Add JitPack.io repository to your root build.gradle:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Add dependepcy to your application related build.gradle

```gradle
dependencies {
    compile 'com.github.eggheadgames:Siren:1.1.0'
}
```

Host a Json document with a public access that will describe your application package name and current application version.

```{
    "com.example.app": {
        "minVersionName": "1.0.0.0"
    }
}
```
OR
```{
    "com.example.app": {
        "minVersionCode": 7,
    }
}
```

Get instance of Siren class and perform version verification by calling
```
public void checkVersion(Activity activity, SirenVersionCheckType versionCheckType, String appDescriptionUrl)
```
Depending on **SirenVersionCheckType** that you choose, verification will be performed in the following way:

**IMMEDIATELY**     Version check performed every time the app is launched
**DAILY**           Version check performed once a day
**WEEKLY**          Version check performed once a week

You can also define dialog appearance and behaviour by setting **SirenAlertType** as a reaction to every digit in Semantic Versioning System 