package com.mxt.price.handler;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * 消息监听器，消费者监听生产者消息
 * @author chenyu23
 * @data 2017年12月19日 下午5:02:23
 * @Description TODO
 */
public class ConsumerListener implements MessageListener{

	private MessageConverter messageConverter;
	
	public MessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	@Override
	public void onMessage(Message message) {
		try {
			Object obj = messageConverter.fromMessage(message);
			System.out.println(obj.toString());
		} catch (MessageConversionException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
