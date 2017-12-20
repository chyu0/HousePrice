package com.yunfang.mq;

/**
 * 消息生产者
 * @author chenyu23
 * @data 2017年12月19日 下午4:13:28
 * @Description TODO
 */
public interface Productor<T> {

	public void sendMessage(T message);
}
