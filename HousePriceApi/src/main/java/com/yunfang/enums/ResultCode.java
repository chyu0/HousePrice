package com.yunfang.enums;

/**
 * 公匙校验返回码
 * @author chenyu23
 * @data 2017年11月2日 上午10:59:33
 * @Description TODO
 */
public enum ResultCode {

	SUCCESS(200, "接口调用正常") , ACCESSERROR(400 , "权限校验异常，无相关权限") ,
	ERROR(500 , "接口调用异常") , VERIFYPARAMSERROR(301 , "校验参数错误") , 
	NULLRESULT(302, "接口调用返回结果为空") , REQUESTPARAMSERROR(303, "请求参数为空");
	
	private Integer code;
	private String desc;
	
	private ResultCode(Integer code,String desc){
		this.code = code;
		this.desc = desc;
	}
	
	public Integer getCode() {
		return code;
	}
	
	public String getDesc() {
		return desc;
	}

	public static String getValueByType(Integer code){
		for(ResultCode t : ResultCode.values()){
			if(t.getCode().equals(code)){
				return t.getDesc();
			}
		}
		return null;
	}
}
