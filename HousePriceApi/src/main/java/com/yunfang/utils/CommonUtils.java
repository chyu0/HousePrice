package com.yunfang.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author maoxiaotai
 * @data 2017年7月20日 下午4:43:18
 * @Description 帮助类
 */
public class CommonUtils {

	/**
	 * @param e
	 * @return 日志详细信息，堆栈信息
	 */
	public static String exceptionToStr(Throwable e){
		StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw =  new PrintWriter(sw);
            //将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            return sw.toString();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
	}
	
	/**   
     * 通过反射,获得定义Class时声明的父类的范型参数的类型.   
     * 如public BookManager extends GenricManager<Book>   
     *   
     * @param clazz The class to introspect   
     * @return the first generic declaration, or <code>Object.class</code> if cannot be determined   
     */  
    public static Class<?> getSuperClassGenricType(Class<?> clazz) {  
        return getSuperClassGenricType(clazz, 0);  
    }  
  
    /**   
     * 通过反射,获得定义Class时声明的父类的范型参数的类型.   
     * 如public BookManager extends GenricManager<Book>   
     *   
     * @param clazz clazz The class to introspect   
     * @param index the Index of the generic ddeclaration,start from 0.   
     */  
    public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) throws IndexOutOfBoundsException {  
  
        Type genType = clazz.getGenericSuperclass();  
  
        if (!(genType instanceof ParameterizedType)) {  
            return Object.class;  
        }  
  
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();  
  
        if (index >= params.length || index < 0) {  
            return Object.class;  
        }  
        if (!(params[index] instanceof Class)) {  
            return Object.class;  
        }  
        return (Class<?>) params[index];  
    }  
}
