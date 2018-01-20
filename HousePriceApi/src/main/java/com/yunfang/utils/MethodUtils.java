package com.yunfang.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import com.yunfang.service.impl.HousePriceApiServiceImpl;

public class MethodUtils {

	public static List<String> getParamterName(Class<?> clazz, String methodName) {
		if(StringUtils.isBlank(methodName) || clazz == null){
			return null;
		}
		LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (methodName.equals(method.getName())) {
				String[] params = u.getParameterNames(method);
				return Arrays.asList(params);
			}
		}
		return null;
	}

	public static List<String> getParamterType(Class<?> clazz, String methodName) {
		if(StringUtils.isBlank(methodName) || clazz == null){
			return null;
		}
		List<String> result = new ArrayList<String>();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if(methodName.equals(method.getName())){
				Class<?>[] parameterTypes = method.getParameterTypes();
				for (Class<?> clas : parameterTypes) {
					String parameterName = clas.getName();
					System.out.println(parameterName);
					result.add(parameterName);
				}
			}
		}
		return result;
	}

	public static List<String> getParamterNames(Class<?> clazz, String methodName) {
		if(StringUtils.isBlank(methodName) || clazz == null){
			return null;
		}
		List<String> result = new ArrayList<String>();
		ClassPool pool = ClassPool.getDefault();  
        try {  
            CtClass ctClass = pool.get(clazz.getName());  
            CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);  
            MethodInfo methodInfo = ctMethod.getMethodInfo();  
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
            if (attr != null) {  
                int len = ctMethod.getParameterTypes().length;  
                // 非静态的成员函数的第一个参数是this  
                int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;  
                System.out.print("test : ");  
                for (int i = 0; i < len; i++) {  
                    System.out.print(attr.variableName(i + pos) + ' ');  
                }  
                System.out.println();  
            }  
        } catch (NotFoundException e) {  
            e.printStackTrace();  
        }  
		return result;
	}
	
	public static Object getTarget(Object proxy) throws Exception {  
        
        if(!AopUtils.isAopProxy(proxy)) {  
            return proxy;//不是代理对象  
        }  
          
        if(AopUtils.isJdkDynamicProxy(proxy)) {  
            return getJdkDynamicProxyTargetObject(proxy);  
        } else { //cglib  
            return getCglibProxyTargetObject(proxy);  
        }  
    }  
	
	private static Object getCglibProxyTargetObject(Object proxy) throws Exception {  
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");  
        h.setAccessible(true);  
        Object dynamicAdvisedInterceptor = h.get(proxy);  
          
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");  
        advised.setAccessible(true);  
          
        Object target = ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();  
          
        return target;  
    }  
  
  
    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {  
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");  
        h.setAccessible(true);  
        AopProxy aopProxy = (AopProxy) h.get(proxy);  
          
        Field advised = aopProxy.getClass().getDeclaredField("advised");  
        advised.setAccessible(true);  
          
        Object target = ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();  
          
        return target;  
    }  
	
	public static void main(String[] args) {
		getParamterName(HousePriceApiServiceImpl.class,"avgPrice");
	}
}
