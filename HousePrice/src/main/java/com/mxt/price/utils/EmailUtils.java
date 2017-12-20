package com.mxt.price.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mxt.price.modal.common.Email;
import com.sun.mail.util.MailSSLSocketFactory;

@Component
public class EmailUtils {

	private static final boolean debug = false;
	private static final boolean auth = true;
	private static final String host = "smtp.qq.com";
	private static final String protocol = "smtp";
	private static final boolean enable = true;
	private static final String from = "1939861002@qq.com";//发送邮箱
	private static final String password = "svdnymozfljsdfcc";//授权码
	
	private static boolean swith;
	
	@Value("${email_swith}")
	public void setEmailSwithVal(boolean email_swith) {
		setSwith(email_swith);
	}
	
	public static void setSwith(boolean email_swith) {
		swith = email_swith;
	}
	
	private static Logger logger = LoggerFactory.getLogger(EmailUtils.class);
	
	public static void sendEmail(Email email){
		if(!swith){
			logger.info("邮件发送已停用！");
			return ;
		}
		try{
			Properties props = new Properties();

	        // 开启debug调试
	        props.setProperty("mail.debug", String.valueOf(debug));
	        // 发送服务器需要身份验证
	        props.setProperty("mail.smtp.auth", String.valueOf(auth));
	        // 设置邮件服务器主机名
	        props.setProperty("mail.host", host);
	        // 发送邮件协议名称
	        props.setProperty("mail.transport.protocol", protocol);

	        MailSSLSocketFactory sf = new MailSSLSocketFactory();
	        sf.setTrustAllHosts(true);
	        props.put("mail.smtp.ssl.enable", String.valueOf(enable));
	        props.put("mail.smtp.ssl.socketFactory", sf);

	        Session session = Session.getInstance(props);

	        Message msg = new MimeMessage(session);
	        msg.setSubject(email.getSubject());
	        msg.setText(email.getContent());
	        msg.setFrom(new InternetAddress(from));

	        Transport transport = session.getTransport();
	        transport.connect(host, from, password);

	        List<String> to = email.getTo();
	        if(to != null && to.size() > 0){
	        	Address[] address = new Address[to.size()];
		        for(int i=0; i<to.size(); i++){
		        	address[i] = new InternetAddress(to.get(i));
		        }
		        transport.sendMessage(msg, address);
	        }else{
	        	logger.info("收件人为空！");
	        }
	        transport.close();
		}catch(Exception e){
			logger.error("邮件发送异常：" + CommonUtils.exceptionToStr(e));
		}
	}
	
	public static void main(String[] args) {
		Email email = new Email();
		email.setContent("系统异常");
		email.setSubject("HousePrice异常");
		email.setTo(Arrays.asList(new String[]{"1939861002@qq.com"}));
		EmailUtils.sendEmail(email);
	}
}
