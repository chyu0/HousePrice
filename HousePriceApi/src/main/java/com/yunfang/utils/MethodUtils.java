package com.yunfang.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

public class MethodUtils {

	public static List<String> getParamterName(Class<?> clazz, String methodName){
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
}
