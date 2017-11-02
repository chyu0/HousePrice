package com.yunfang.modal;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.JSON;

public class Result implements Serializable{

	private static final long serialVersionUID = 3830942032883858449L;
	
	/**
	 * 调用是否成功
	 */
	private boolean success;
	
	/**
	 * Result对象，接口调用返回结果
	 */
	private JSON result;
	
	/**
	 * 接口调用时间
	 */
	private Date date;
	
	
	/**
	 * 返回信息
	 */
	private String message;
	
	/**
	 * 唯一性表示
	 */
	private String guid;
	
	/**
	 * 请求状态码
	 */
	private Integer code;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public JSON getResult() {
		return result;
	}

	public void setResult(JSON result) {
		this.result = result;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
	
}
