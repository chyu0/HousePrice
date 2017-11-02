package com.yunfang.enums;

/**
 * 公匙校验返回码
 * @author chenyu23
 * @data 2017年11月2日 上午10:59:33
 * @Description TODO
 */
public enum PublicKeyVerifyCode {

	ACCESSSUCCESS(0, "恭喜您，校验成功") , ACCESSERROR(1 , "公匙验证失败，请检查公匙是否正确") ,
	NOAUTH(2 , "抱歉，该公匙没有查询该模块的相关权限") , TIMESTAMPOUT(3 , "校验失败，时间撮超时，请使用最新的时间撮");
	
	private Integer code;
	private String desc;
	
	private PublicKeyVerifyCode(Integer code,String desc){
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
		for(PublicKeyVerifyCode t : PublicKeyVerifyCode.values()){
			if(t.getCode().equals(code)){
				return t.getDesc();
			}
		}
		return null;
	}
}
