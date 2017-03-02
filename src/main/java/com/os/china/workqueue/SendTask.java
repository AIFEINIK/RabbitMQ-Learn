package com.os.china.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * @author ZhangPengFei
 * @Discription 模拟生产者Publish多个数据到queue
 * @Data 2017-3-2
 * @Version 1.0.0
 */
public class SendTask {
	private static final String TASK_QUEUE_NAME = "task_queue";

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		String[] mes = new String[]{"A","B","C","D","E"};
		for (int i = 0; i < mes.length; i++) {
			String message = mes[i];
			channel.basicPublish("", TASK_QUEUE_NAME,
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					message.getBytes("UTF-8"));
			System.out.println("Sent " + message);
		}

		channel.close();
		connection.close();
	}
}
