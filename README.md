⟳ loglifecycle
============

Logs all lifecycle methods of a given activity on Android. 

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
> adb logcat | grep LogLifecycle

D/LogLifeCycle( 3172): MainActivity ⟳ onCreate
D/LogLifeCycle( 3172): MainActivity ⟳ onApplyThemeResource
D/LogLifeCycle( 3172): MainActivity ⟳ onWindowAttributesChanged
D/LogLifeCycle( 3172): MainActivity ⟳ onWindowAttributesChanged
D/LogLifeCycle( 3172): MainActivity ⟳ onWindowAttributesChanged
D/LogLifeCycle( 3172): MainActivity ⟳ onContentChanged
D/LogLifeCycle( 3172): MainActivity ⟳ onStart
D/LogLifeCycle( 3172): MainActivity ⟳ onTitleChanged
D/LogLifeCycle( 3172): MainActivity ⟳ onPostCreate
D/LogLifeCycle( 3172): MainActivity ⟳ onResume
D/LogLifeCycle( 3172): MainActivity ⟳ onPostResume
D/LogLifeCycle( 3172): MainActivity ⟳ onAttachedToWindow
D/LogLifeCycle( 3172): MainActivity ⟳ onWindowFocusChanged
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
* [ ] release on central
* [ ] add maven badge
