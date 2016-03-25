[![Release](https://jitpack.io/v/eggheadgames/Siren.svg)](https://jitpack.io/#eggheadgames/Siren)

# Siren for Android

### Notify users when a new version of your Android app is available, and prompt them with the Play Store link. 

This is a port of the iOS library of the same name: https://github.com/ArtSabintsev/Siren

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

## Setup

A minimal usage is to add the following to the `onCreate` of your main activity. 
This will check at most once a day for a new version and give the user the option to choose "Update" or "Next time".
```java

private static final String SIREN_JSON_URL = "https://example.com/com.mycompany.myapp/version.json";

Siren siren = Siren.getInstance(getApplicationContext());
siren.checkVersion(this, SirenVersionCheckType.DAILY, SIREN_JSON_URL);
```

## Installation Instructions
Add the JitPack.io repository to your root `build.gradle`:

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Add a dependency to your application related `build.gradle`

```gradle
dependencies {
    compile 'com.github.eggheadgames:Siren:1.2.1'
}
```

Host a Json document with a public access that will describe your application package name and current application version.

```json
{
    "com.example.app": { "minVersionName": "1.3.2" }
}
```
OR
```json
{
    "com.example.app": { "minVersionCode": 7 }
}
```

## Full Example

Some developers may want to display a less obtrusive custom interface, like a banner or small icon. 
You may also wish to control which level of update to force a user to update vs deferring to later.

You can find a fully functional sample project at https://github.com/eggheadgames/SirenSample.

Here is a code sample that shows the full features available:

```java
    private void checkCurrentAppVersion() {
        Siren siren = Siren.getInstance(getApplicationContext());
        siren.setSirenListener(sirenListener);
        siren.setMajorUpdateAlertType(SirenAlertType.FORCE);
        siren.setMinorUpdateAlertType(SirenAlertType.OPTION);
        siren.setPatchUpdateAlertType(SirenAlertType.SKIP);
        siren.setRevisionUpdateAlertType(SirenAlertType.NONE);
        siren.setVersionCodeUpdateAlertType(SirenAlertType.SKIP);
        siren.checkVersion(this, SirenVersionCheckType.IMMEDIATELY, SIREN_JSON_DOCUMENT_URL);
    }

    ISirenListener sirenListener = new ISirenListener() {
        @Override
        public void onShowUpdateDialog() {
            Log.d(TAG, "onShowUpdateDialog");
        }

        @Override
        public void onLaunchGooglePlay() {
            Log.d(TAG, "onLaunchGooglePlay");
        }

        @Override
        public void onSkipVersion() {
            Log.d(TAG, "onSkipVersion");
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "onCancel");
        }

        @Override
        public void onDetectNewVersionWithoutAlert(String message) {
            Log.d(TAG, "onDetectNewVersionWithoutAlert: " + message);
        }

        @Override
        public void onError(Exception e) {
            Log.d(TAG, "onError");
            e.printStackTrace();
        }
    };
```


The **SirenVersionCheckType** that you choose changes the check frequency as follows:

 * **IMMEDIATELY**     every time the app is launched
 * **DAILY**           once a day
 * **WEEKLY**          once a week

You can also define the dialog appearance and behaviour by setting **SirenAlertType** to react according to your version increment per [Semantic Versioning](http://semver.org/).

