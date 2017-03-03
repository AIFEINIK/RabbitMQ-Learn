package com.os.china.routing;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author ZhangPengFei
 * @Discription
 * @Data 2017-3-2
 * @Version 1.0.0
 */
public class LogSubscriber {
	private static final String EXCHANGE_NAME = "directExchange";

	public static void main(String[] args)throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		String queueName = channel.queueDeclare().getQueue();
		String[] severity = new String[]{"info","warning","error"};
		for (int i = 0; i < severity.length; i++) {
			channel.queueBind(queueName, EXCHANGE_NAME, severity[i]);
		}
		System.out.println("Waiting message...");
		Consumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println("Received " + envelope.getRoutingKey() + " : " + message);
			}
		};
		channel.basicConsume(queueName, true, consumer);
	}
}
