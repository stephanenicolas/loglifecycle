package com.github.stephanenicolas.loglifecycle;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.content.Context;
import android.content.Intent;

import org.robolectric.shadows.ShadowLog;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author SNI
 */
@RunWith(LogLifeCycleTestRunner.class)
@Config(shadows = {ShadowLog.class}, manifest = Config.NONE)
public class LogLifeCycleProcessorTest {

  @Before
  public void setup() {
    ShadowLog.reset();
  }

  @Test
  public void shouldLog_activity() {
    Robolectric.buildActivity(TestActivity.class).create().start().stop().destroy().get();
    List<ShadowLog.LogItem> logItems = ShadowLog.getLogsForTag("LogLifeCycle");

    assertNotNull(logItems);
    assertLogContainsMessage(logItems, "onCreate");
    assertLogContainsMessage(logItems, "onStart");
    assertLogContainsMessage(logItems, "onStop");
    assertLogContainsMessage(logItems, "onDestroy");
  }

  @Test
  public void shouldLog_fragment() {
    Robolectric.buildActivity(TestActivityWitFragment.class).create().start().stop().destroy().get();
    List<ShadowLog.LogItem> logItems = ShadowLog.getLogsForTag("LogLifeCycle");

    assertNotNull(logItems);
    assertLogContainsMessage(logItems, "onStart");
    assertLogContainsMessage(logItems, "onStop");
  }

  @Test
  public void shouldLog_View() {
    Robolectric.buildActivity(TestActivityWitView.class).create().start().get();
    List<ShadowLog.LogItem> logItems = ShadowLog.getLogsForTag("LogLifeCycle");

    assertNotNull(logItems);
    assertLogContainsMessage(logItems, "onFinishInflate");
  }

  @Test
  public void shouldLog_Service() {
    Robolectric.buildService(TestService.class).attach().create().withIntent(null).startCommand(0,1).destroy().get();
    List<ShadowLog.LogItem> logItems = ShadowLog.getLogsForTag("LogLifeCycle");

    assertNotNull(logItems);
    assertLogContainsMessage(logItems, "onCreate");
    assertLogContainsMessage(logItems, "onStart");
    assertLogContainsMessage(logItems, "onDestroy");
  }

  @LogLifeCycle
  public static class TestActivity extends Activity {
  }

  //do not log activity now, only fragment
  public static class TestActivityWitFragment extends Activity {

    public void onCreate(Bundle bundle) {
      TestFragment testFragment = new TestFragment();
      getFragmentManager().beginTransaction().add(testFragment, "TAG").commit();
    }
  }

  @LogLifeCycle
  public static class TestFragment extends Fragment {
  }

  //do not log activity now, only fragment
  public static class TestActivityWitView extends Activity {

    public void onCreate(Bundle bundle) {
      setContentView(new TestView(this));
    }
  }

  @LogLifeCycle
  public static class TestView extends View {

    public TestView(Context context) {
      super(context);
      onFinishInflate();
    }
  }

  @LogLifeCycle
  public static class TestService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }  }

  private void assertLogContainsMessage(List<ShadowLog.LogItem> logItems, String logMessage) {
    for(ShadowLog.LogItem logItem : logItems ) {
      if (logItem.msg.contains(logMessage)) {
        return;
      }
    }
    fail("No message " + logMessage + " in log");
  }

}