package com.github.stephanenicolas.loglifecycle;

import android.app.Activity;

import com.github.stephanenicolas.afterburner.AfterBurner;

import com.github.stephanenicolas.afterburner.exception.AfterBurnerImpossibleException;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;
import lombok.extern.slf4j.Slf4j;

/**
 * A simple annotation processor.
 * @author SNI
 */
@Slf4j
public class LogLifeCycleProcessor implements IClassTransformer {

	private AfterBurner afterBurner = new AfterBurner();

	@Override
  public boolean shouldTransform(CtClass candidateClass) throws JavassistBuildException {
		try {
      ClassPool pool = candidateClass.getClassPool();
			return candidateClass.subclassOf(pool.get(Activity.class.getName())) &&  candidateClass.hasAnnotation(LogLifeCycle.class);
		} catch (Exception e) {
			throw new JavassistBuildException(e);
		}
	}

	@Override
	public void applyTransformations(CtClass classToTransform)  throws JavassistBuildException {
    try {
      log.info("Transforming " + classToTransform.getName());
      ClassPool pool = classToTransform.getClassPool();
      debugLifeCycleMethods(classToTransform, pool.get(Activity.class.getName()).getMethods());
      debugLifeCycleMethods(classToTransform, pool.get(Activity.class.getName()).getDeclaredMethods());
    } catch (Exception e) {
      throw new JavassistBuildException(e);
    }
  }

  private void debugLifeCycleMethods(CtClass classToTransform, CtMethod[] methods)
      throws CannotCompileException, AfterBurnerImpossibleException, NotFoundException {
    for (CtMethod lifeCycleHook : methods) {
      String methodName = lifeCycleHook.getName();
      if (methodName.startsWith("on")) {
        log.info("Overriding " + methodName);
        try {
          String body = String.format("android.util.Log.d(\"LogLifeCycle\",\"%s ⟳ %s\");",
              classToTransform.getSimpleName(), methodName);
          afterBurner.afterOverrideMethod(classToTransform, methodName, body);
        } catch (Exception e) {
          log.info("Override didn't work ", e);
        }
      }
    }
  }
}
