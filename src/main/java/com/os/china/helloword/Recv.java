package com.os.china.helloword;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author ZhangPengFei
 * @Discription 消息接收者
 * @Data 2017-3-1
 * @Version 1.0.0
 */
public class Recv {
	private static final String QUEUE_NAME = "hello";

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		//远程连接需要IP与用户名，密码
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		//这里也需要创建一个hello的队列，以保证该队列是存在的
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println("Waiting for messages...");
		//新建一个DefaultConsumer，当RabbitMQ-server队列数据通过异步方式发送来时会回调handleDelivery方法
		Consumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "utf-8");
				System.out.println("Received '" + message + "'");
			}
		};
		//注册consumer
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}
}
