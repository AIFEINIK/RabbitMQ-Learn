package com.os.china.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author ZhangPengFei
 * @Discription
 * @Data 2017-3-3
 * @Version 1.0.0
 */
public class RPCClient {
	private Connection connection;
	private Channel channel;
	private String requestQueueName = "rpc_queue";
	private String replyQueueName;

	public RPCClient() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		connection = factory.newConnection();
		channel = connection.createChannel();
		replyQueueName = channel.queueDeclare().getQueue();
	}

	public String call(String message) throws Exception {
		final String corrId = UUID.randomUUID().toString();
		AMQP.BasicProperties properties = new AMQP.BasicProperties
				.Builder()
				.correlationId(corrId) //唯一的Callback queue id
				.replyTo(replyQueueName) //Callback queue name
				.build();
		channel.basicPublish("", requestQueueName, properties, message.getBytes("UTF-8"));

		System.out.println("Request message is :" + message);

		final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);
		channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   AMQP.BasicProperties properties, byte[] body) throws IOException {
				/**
				 * 如果server端返回的CorrelationId是client端发送message时所携带的CorrelationId
				 * 则进行处理
				 */
				if (properties.getCorrelationId().equals(corrId)) {
					response.offer(new String(body, "UTF-8"));
				}
			}
		});
		return response.take();
	}

	public void close() throws Exception {
		channel.close();
	}

	public static void main(String[] args){
		RPCClient client = null;
		try {
			client = new RPCClient();
			System.out.println("Requesting...");
			String resp = client.call("Now time is :");
			System.out.println("RPC server response :" + resp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
