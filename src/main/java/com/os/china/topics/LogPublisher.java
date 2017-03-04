package com.os.china.topics;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author ZhangPengFei
 * @Discription
 * @Data 2017-3-3
 * @Version 1.0.0
 */
public class LogPublisher {
	private static final String EXCHANGE_NAME = "topic_exchange";

	public static void main(String[] args) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = null;
		Channel channel = null;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			//使用type为topic的Exchange
			channel.exchangeDeclare(EXCHANGE_NAME, "topic");
			String routingKey = getRoutingKey(args);
			String message = getMessage(args);
			channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (channel != null) {
					channel.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String getMessage(String[] args) {
		if (args.length < 2) {
			return "Hello word";
		}
		return args[1];
	}

	private static String getRoutingKey(String[] args) {
		if (args.length < 1) {
			return "anonymous.info";
		}
		return args[0];
	}
}
