package com.os.china.publish_subscribe;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author ZhangPengFei
 * @Discription 消息订阅者
 * @Data 2017-3-2
 * @Version 1.0.0
 */
public class MsgSubscriber {
	private static final String EXCHANGE_NAME = "fanoutExchange";

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		//获得queue的名称，该queue的名称由RabbitMQ随机命名
		String queueName = channel.queueDeclare().getQueue();
		//将queue与Exchange绑定
		channel.queueBind(queueName, EXCHANGE_NAME, "");
		System.out.println("Waiting for messages...");
		Consumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println("Received message : " + message);
			}
		};
		//注册Consume并指定autoAck为true
		channel.basicConsume(queueName, true, consumer);
	}
}
