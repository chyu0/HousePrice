package com.yunfang.modal;

import java.io.Serializable;
import java.util.List;

/**
 * 邮件实体类
 * @author maoxiaotai
 * @data 2017年12月19日 下午8:16:23
 * @Description TODO
 */
public class Email implements Serializable{

	private static final long serialVersionUID = 1497328048584167878L;

	/**
	 * 主题
	 */
	private String subject;
	
	/**
	 * 文本
	 */
	private String content;
	
	/**
	 * 发件人
	 */
	private String form;
	
	/**
	 * 收件人
	 */
	private List<String> to;
	
	/**
	 * 抄送
	 */
	private List<String> cto;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public List<String> getCto() {
		return cto;
	}

	public void setCto(List<String> cto) {
		this.cto = cto;
	}

}
