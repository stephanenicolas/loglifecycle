package com.github.stephanenicolas.loglifecycle;

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
import javassist.bytecode.AccessFlag;
import lombok.extern.slf4j.Slf4j;

/**
 * A class transformer to inject logging byte code for all life cycle methods.
 * @author SNI
 */
@Slf4j
public class LogLifeCycleProcessor implements IClassTransformer {

  public final String[] SUPPORTED_CLASSES = new String[] {
      "android.app.Activity",
      "android.app.Fragment",
      "android.support.v4.app.Fragment",
      "android.view.View",
      "android.app.Service",
      "android.content.BroadcastReceiver",
      "android.content.ContentResolver",
      "android.app.Application"
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
    for (String supportedClass : SUPPORTED_CLASSES) {
      try {
        if (candidateClass.subtypeOf(pool.get(supportedClass))) {
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
      Set<CtMethod> methodSet = getAllLifeCycleMethods(pool, classToTransform.getName());
      debugLifeCycleMethods(classToTransform, methodSet.toArray(new CtMethod[methodSet.size()]));
    } catch (Exception e) {
      logMoreIfDebug("Transformation failed for class " + classToTransformName, e);
      throw new JavassistBuildException(e);
    }
    log.info("Transformation successful for " + classToTransformName);
  }

  private Set<CtMethod> getAllLifeCycleMethods(ClassPool pool, String className)
      throws NotFoundException {
    Set<CtMethod> methodSet = new HashSet<CtMethod>();
    CtMethod[] inheritedMethods = pool.get(className).getMethods();
    CtMethod[] declaredMethods = pool.get(className).getDeclaredMethods();
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

      int accessFlags = lifeCycleHook.getMethodInfo().getAccessFlags();
      boolean isFinal = (accessFlags & AccessFlag.FINAL) == AccessFlag.FINAL;
      boolean canOverride = !isFinal && (AccessFlag.isPublic(accessFlags) || AccessFlag.isProtected(accessFlags) || AccessFlag.isPackage(accessFlags));

      if (canOverride && methodName.startsWith("on")) {
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
      } else {
        log.info("Skipping " + methodName + ". Either it is final, private or doesn't start by 'on...'");
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
