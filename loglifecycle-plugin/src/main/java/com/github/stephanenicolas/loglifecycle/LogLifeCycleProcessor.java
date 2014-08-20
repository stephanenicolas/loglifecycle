package com.github.stephanenicolas.loglifecycle;

import android.app.Activity;

import com.github.stephanenicolas.afterburner.AfterBurner;

import com.github.stephanenicolas.afterburner.exception.AfterBurnerImpossibleException;
import java.util.HashSet;
import java.util.Set;
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
      Set<CtMethod> methodSet = getAllMethods(pool, Activity.class);
      debugLifeCycleMethods(classToTransform, methodSet.toArray(new CtMethod[methodSet.size()]));
    } catch (Exception e) {
      throw new JavassistBuildException(e);
    }
  }

  private Set<CtMethod> getAllMethods(ClassPool pool, Class<?> clazz) throws NotFoundException {
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
