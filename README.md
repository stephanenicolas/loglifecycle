⟳ loglifecycle [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.stephanenicolas.loglifecycle/loglifecycle-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.stephanenicolas.loglifecycle/loglifecycle-plugin)
============

***Logs all lifecycle methods of annotated activities, fragments, views, etc. on Android.***

###Usage

Inside your `build.gradle` file, add : 

```groovy
apply plugin: 'loglifecycle'
```

And now, annotate every `Activity` you want to see its life cycle logged in logcat : 

```java
@LogLifeCycle
public class MainActivity extends Activity {
...
}
```

You will see something like this in logcat : 

```bash
> adb logcat | grep ⟳

D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onCreate
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onApplyThemeResource
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onWindowAttributesChanged
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onWindowAttributesChanged
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onWindowAttributesChanged
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onContentChanged
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onAttachFragment
D/LogLifeCycle( 4640): MainActivity$MainFragment [1384183528] ⟳ onCreate
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onStart
D/LogLifeCycle( 4640): MainActivity$MainFragment [1384183528] ⟳ onStart
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onTitleChanged
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onPostCreate
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onResume
D/LogLifeCycle( 4640): MainActivity$MainFragment [1384183528] ⟳ onResume
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onPostResume
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onAttachedToWindow
D/LogLifeCycle( 4640): MainActivity [1384162984] ⟳ onWindowFocusChanged
```

Extra features : 
* release builds won't log anything, indeed the app is not modified at all in release builds.
* this plugin will add only a few more bytes to your app : log statements, and the annotation class, nothing more.

### Support library support

If your app uses support library, and you want to annotate a `android.support.v4.app.Fragment`, add the following statements to your `build.gradle` file : 

```groovy
buildscript {
  repositories {
    ...
    maven {
      url "<your android sdk dir>/extras/android/m2repository/"
    }
  }

  dependencies {
    ...
    classpath 'com.android.support:support-v4:19.1.0'
  }
}
```

Notes:

* you *must* use version `19.1.0` or below. More recent versions of the support library are distributed in aar format, which is not usable in the buildscript section of gradle (as far as I know).
* there is currently no generic way to get `ANDROID_HOME` value, and you *must* add it manually, so yes, this breaks
build portability (among developers for instance).

###Example

You will find an example app using LogLifeCycle in the repo.

###How does it work ?

Thanks to 
* [morpheus](https://github.com/stephanenicolas/morpheus), byte code weaver for android.
* [AfterBurner](https://github.com/stephanenicolas/afterburner), byte code weaving swiss army knife for Android.

###TODO
* [ ] add tests
* [ ] add CI + badge
