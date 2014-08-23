⟳ loglifecycle [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.stephanenicolas.loglifecycle/loglifecycle-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.stephanenicolas.loglifecycle/loglifecycle-plugin)
============

***Logs all lifecycle methods of a annotated activities, fragments, views, etc. on Android.***

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

###Example

You will find an example program in the repo.

###How does it work ?

Thanks to 
* [morpheus](https://github.com/stephanenicolas/morpheus), byte code weaver for android.
* [AfterBurner](https://github.com/stephanenicolas/afterburner), byte code weaving swiss army knife for Android.

###TODO
* [ ] add tests
* [ ] add CI + badge
