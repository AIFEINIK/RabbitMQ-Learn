package com.os.china.helloword;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author ZhangPengFei
 * @Discription 消息发送者
 * @Data 2017-3-1
 * @Version 1.0.0
 */
public class Send {
	private static final String QUEUE_NAME = "hello";

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		//远程连接需要IP与用户名，密码
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		//创建虚拟连接channel
		Channel channel = connection.createChannel();
		//创建一个名称为hello的队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		String message = "Hello, world";
		//发布消息
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
		System.out.println("Send '" + message + "'");

		channel.close();
		connection.close();
	}
}
