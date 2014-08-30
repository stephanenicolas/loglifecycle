package com.github.stephanenicolas.loglifecycle;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

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
@Config(shadows = {ShadowLog.class})
public class LogLifeCycleProcessorTest {

  @Before
  public void setup() {
    ShadowLog.reset();
  }

  @Test
  public void shouldLogs_activity() {
    Robolectric.buildActivity(TestActivity.class).create().start().stop().destroy().get();
    List<ShadowLog.LogItem> logItems = ShadowLog.getLogsForTag("LogLifeCycle");

    assertNotNull(logItems);
    assertLogContainsMessage(logItems, "onCreate");
    assertLogContainsMessage(logItems, "onStart");
    assertLogContainsMessage(logItems, "onStop");
    assertLogContainsMessage(logItems, "onDestroy");
  }

  @Test
  public void shouldLogs_fragment() {
    Robolectric.buildActivity(TestActivity.class).create().start().stop().destroy().get();
    List<ShadowLog.LogItem> logItems = ShadowLog.getLogsForTag("LogLifeCycle");

    assertNotNull(logItems);
    assertLogContainsMessage(logItems, "onStart");
    assertLogContainsMessage(logItems, "onStop");
  }

  @LogLifeCycle
  public static class TestActivity extends Activity {
  }

  //do not log activity now, only fragment
  public static class TestActivityWitFragment extends Activity {

    public void onCreate(Bundle bundle) {
      TestFragment testFragment = new TestFragment();
      getFragmentManager().beginTransaction().add(1, testFragment, "TAG").commit();
    }
  }

  @LogLifeCycle
  public static class TestFragment extends Fragment {
  }

  private void assertLogContainsMessage(List<ShadowLog.LogItem> logItems, String logMessage) {
    for(ShadowLog.LogItem logItem : logItems ) {
      if (logItem.msg.contains(logMessage)) {
        return;
      }
    }
    fail("No message " + logMessage + " in log");
  }

}