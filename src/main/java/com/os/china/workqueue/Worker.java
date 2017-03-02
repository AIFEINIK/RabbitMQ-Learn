package com.os.china.workqueue;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author ZhangPengFei
 * @Discription模拟消费者接收queue中的数据
 * @Data 2017-3-2
 * @Version 1.0.0
 */
public class Worker {
	private static final String TASK_QUEUE_NAME = "task_queue";

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		final Connection connection = factory.newConnection();
		final Channel channel = connection.createChannel();

		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		System.out.println("Waiting for messages...");
		/**
		 * 设置RabbitMQ在未收到Consumer发送的Ack包之前，每次最多只能处理一个Message
		 * 这样可以起到Message的合理分发
		 */
		channel.basicQos(1);

		final Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");

				System.out.println("Received from server: " + message);
				try {
					//接收到Message后，sleep 2s
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					System.out.println("Ack Done");
					//2s后发送Ack包, 如果不做Ack确认操作则Message消息将一直存在与queue中，久而久之会导致RabbitMQ内存泄漏
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		//注意，在设置了basicAck后，basicConsume方法的第二个参数autoAck需为false
		channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
	}
}
