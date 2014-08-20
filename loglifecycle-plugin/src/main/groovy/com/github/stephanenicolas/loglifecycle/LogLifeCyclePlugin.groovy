package com.github.stephanenicolas.loglifecycle

import com.github.stephanenicolas.morpheus.AbstractMorpheusPlugin;
import javassist.build.IClassTransformer;
import org.gradle.api.Project;

/**
 * @author SNI
 */
public class LogLifeCyclePlugin extends AbstractMorpheusPlugin {

  @Override
  public IClassTransformer[] getTransformers(Project project) {
    return new LogLifeCycleProcessor();
  }

  @Override
  protected void configure(Project project) {
    project.dependencies {
      provided 'com.github.stephanenicolas.loglifecycle:loglifecycle-annotations:1.0.0'
    }
  }

  @Override
  protected Class getPluginExtension() {
    LogLifeCyclePluginExtension
  }

  @Override
  protected String getExtension() {
    "loglifecycle"
  }
}
