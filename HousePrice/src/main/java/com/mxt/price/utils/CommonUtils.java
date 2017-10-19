package com.mxt.price.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author chenyu23
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
}
