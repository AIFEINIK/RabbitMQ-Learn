package com.os.china.publish_subscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author ZhangPengFei
 * @Discription 消息发布者
 * @Data 2017-3-2
 * @Version 1.0.0
 */
public class MsgPublisher {
	private static final String EXCHANGE_NAME = "fanoutExchange";

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		//创建类型为fanout，名称为myExchange的Exchange
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		String message = "message from exchange of queue";
		//将消息发送至Exchange,由Exchange决定要将消息转发至哪个queue
		channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
		System.out.println("Send message is : " + message);

		channel.close();
		connection.close();
	}
}
