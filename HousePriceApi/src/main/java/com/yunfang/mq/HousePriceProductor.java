package com.yunfang.mq;

import javax.annotation.Resource;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.yunfang.modal.HousePriceMongo;

/**
 * 消息生产者service，用于生产HousePriceMongo对象消息
 * @author chenyu23
 * @data 2017年12月19日 下午4:13:54
 * @Description TODO
 */
@Component
public class HousePriceProductor implements Productor<HousePriceMongo>{

	@Resource 
    private JmsTemplate jmsTemplate;  
	
	@Override
	public void sendMessage(HousePriceMongo message) {
		jmsTemplate.convertAndSend(JSONObject.toJSONString(message));
	}
}
