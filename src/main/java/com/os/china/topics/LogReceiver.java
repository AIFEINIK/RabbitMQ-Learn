package com.os.china.topics;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author ZhangPengFei
 * @Discription
 * @Data 2017-3-3
 * @Version 1.0.0
 */
public class LogReceiver {
	private static final String EXCHANGE_NAME = "topic_exchange";

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		String queueName = channel.queueDeclare().getQueue();
		if (args.length < 1) {
			System.out.println("Please input bindingKey.");
			System.exit(1);
		}
		//根据args参数使用 *(match one word)或 #(match zero or more word)模式来绑定queue
		for (String bindingKey : args) {
			channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
		}
		System.out.println("Waiting for message...");
		Consumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println("Received " + envelope.getRoutingKey() + ":" + message);
			}
		};
		channel.basicConsume(queueName, true, consumer);
	}
}
