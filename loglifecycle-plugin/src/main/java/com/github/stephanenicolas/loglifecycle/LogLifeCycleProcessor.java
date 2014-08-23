package com.github.stephanenicolas.loglifecycle;

import android.app.Activity;

import android.app.Application;
import android.app.Fragment;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.view.View;
import com.github.stephanenicolas.afterburner.AfterBurner;

import com.github.stephanenicolas.afterburner.exception.AfterBurnerImpossibleException;
import java.util.HashSet;
import java.util.Set;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;
import lombok.extern.slf4j.Slf4j;

/**
 * A class transformer to inject logging byte code for all life cycle methods.
 * @author SNI
 */
@Slf4j
public class LogLifeCycleProcessor implements IClassTransformer {

  public final Class[] SUPPORTED_CLASSES = new Class[] {
      Activity.class,
      Fragment.class,
      android.support.v4.app.Fragment.class,
      View.class,
      Service.class,
      BroadcastReceiver.class,
      ContentResolver.class,
      Application.class
  };

	private AfterBurner afterBurner = new AfterBurner();
  private boolean debug;

  public LogLifeCycleProcessor(boolean debug) {
    this.debug = debug;
  }

  @Override
  public boolean shouldTransform(CtClass candidateClass) throws JavassistBuildException {
		try {
      ClassPool pool = candidateClass.getClassPool();
      boolean isSupported = isSupported(candidateClass, pool);
			return isSupported &&  candidateClass.hasAnnotation(LogLifeCycle.class);
		} catch (Exception e) {
      logMoreIfDebug("Should transform filter failed for class " + candidateClass.getName(), e);
      throw new JavassistBuildException(e);
		}
	}

  private boolean isSupported(CtClass candidateClass, ClassPool pool) {
    for (Class<?> supportedClass : SUPPORTED_CLASSES) {
      try {
        if (candidateClass.subtypeOf(pool.get(supportedClass.getName()))) {
          return true;
        }
      } catch (NotFoundException e) {
        //nothing to do
      }
    }
    return false;
  }

  @Override
	public void applyTransformations(CtClass classToTransform)  throws JavassistBuildException {
    String classToTransformName = classToTransform.getName();
    try {
      log.info("Transforming " + classToTransformName);
      ClassPool pool = classToTransform.getClassPool();
      Set<CtMethod> methodSet = getAllLifeCycleMethods(pool, Activity.class);
      debugLifeCycleMethods(classToTransform, methodSet.toArray(new CtMethod[methodSet.size()]));
    } catch (Exception e) {
      logMoreIfDebug("Transformation failed for class " + classToTransformName, e);
      throw new JavassistBuildException(e);
    }
    log.info("Transformation successful for " + classToTransformName);
  }

  private Set<CtMethod> getAllLifeCycleMethods(ClassPool pool, Class<?> clazz) throws NotFoundException {
    Set<CtMethod> methodSet = new HashSet<CtMethod>();
    CtMethod[] inheritedMethods = pool.get(clazz.getName()).getMethods();
    CtMethod[] declaredMethods = pool.get(clazz.getName()).getDeclaredMethods();
    for (CtMethod method : inheritedMethods) {
      methodSet.add(method);
    }
    for (CtMethod method : declaredMethods) {
      methodSet.add(method);
    }
    return methodSet;
  }

  private void debugLifeCycleMethods(CtClass classToTransform, CtMethod[] methods)
      throws CannotCompileException, AfterBurnerImpossibleException, NotFoundException {
    for (CtMethod lifeCycleHook : methods) {
      String methodName = lifeCycleHook.getName();
      String className = classToTransform.getName();
      if (methodName.startsWith("on")) {
        log.info("Overriding " + methodName);
        try {

          String body = "android.util.Log.d(\"LogLifeCycle\", \""
              + className
              +" [\" + System.identityHashCode(this) + \"] \u27F3 "
              + methodName
              + "\");";
          afterBurner.afterOverrideMethod(classToTransform, methodName, body);
          log.info("Override successful " + methodName);
        } catch (Exception e) {
          logMoreIfDebug("Override didn't work ", e);
        }
      }
    }
  }

  private void logMoreIfDebug(String message, Exception e) {
    if (debug) {
      log.debug(message, e);
    } else {
      log.info(message);
    }
  }
}
