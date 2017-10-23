package com.mxt.price.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {

	private static Logger logger = LoggerFactory.getLogger(DateUtils.class);
	
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
}
