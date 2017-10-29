package com.mxt.price.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期格式化工具类
 * @author maoxiaotai
 * @data 2017年10月27日 下午3:56:16
 * @Description TODO
 */
public class DateUtils {

	private static Logger logger = LoggerFactory.getLogger(DateUtils.class);
	
	/** 年-月-日 时:分:秒 显示格式 */
    public static final String DATE_TO_STRING_DETAIAL_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** 年-月-日 显示格式 */
    public static final String DATE_TO_STRING_YEAR_PATTERN = "yyyy-MM-dd";
    
    /** 年-月 显示格式 */
    public static final String DATE_TO_STRING_MONTH_PATTERN = "yyyy-MM";

    private static SimpleDateFormat simpleDateFormat;
	
    /**
     * 获取开始时间到结束时间的列表
     * @param minDate yyyy-MM格式
     * @param maxDate yyyy-MM格式
     * @return
     */
	public static List<String> getMonthBetween(String minDate, String maxDate){
	    ArrayList<String> result = new ArrayList<String>();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

	    Calendar min = Calendar.getInstance();
	    Calendar max = Calendar.getInstance();

	    try{
	    	min.setTime(sdf.parse(minDate));
		    min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

		    max.setTime(sdf.parse(maxDate));
		    max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
	    }catch(Exception e){
	    	logger.error("日期格式化错误：【getMonthBetween】" + CommonUtils.exceptionToStr(e));
	    }
	    
	    Calendar curr = min;
	    while (curr.before(max)) {
	     result.add(sdf.format(curr.getTime()));
	     curr.add(Calendar.MONTH, 1);
	    }
	    
	    return result;
	}
	
	/**
     * Date类型转为指定格式的String类型
     * 
     * @param source
     * @param pattern
     * @return
     */
    public static String format(Date source, String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(source);
    }

    /**
     * 
     * unix时间戳转为指定格式的String类型
     * 
     * 
     * System.currentTimeMillis()获得的是是从1970年1月1日开始所经过的毫秒数
     * unix时间戳:是从1970年1月1日（UTC/GMT的午夜）开始所经过的秒数,不考虑闰秒
     * 
     * @param source
     * @param pattern
     * @return
     */
    public static String format(long source, String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = new Date(source * 1000);
        return simpleDateFormat.format(date);
    }

    /**
     * 将日期转换为时间戳(unix时间戳,单位秒)
     * 
     * @param date
     * @return
     */
    public static long dateToTimeStamp(Date date) {
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp.getTime() / 1000;

    }

    /**
     * 
     * 字符串转换为对应日期(可能会报错异常)
     * 
     * @param source
     * @param pattern
     * @return
     */
    public static Date parse(String source, String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = simpleDateFormat.parse(source);
        } catch (ParseException e) {
            logger.error("字符串转换日期异常", e);
        }
        return date;
    }

    /**
     * 获得当前时间对应的指定格式
     * 
     * @param pattern
     * @return
     */
    public static String format(String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date());

    }

    /**
     * 获得当前unix时间戳(单位秒)
     * @return 当前unix时间戳
     */
    public static long currentTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

}
