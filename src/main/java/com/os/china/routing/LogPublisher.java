package com.os.china.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author ZhangPengFei
 * @Discription
 * @Data 2017-3-2
 * @Version 1.0.0
 */
public class LogPublisher {
	private static final String EXCHANGE_NAME = "directExchange";

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		String severity = "error";
		String message = "Hello, I am error log message";
		channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes("UTF-8"));
		System.out.println("Send message : " + message);

		channel.close();
		connection.close();
	}
}
