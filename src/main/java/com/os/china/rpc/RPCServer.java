package com.os.china.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ZhangPengFei
 * @Discription
 * @Data 2017-3-3
 * @Version 1.0.0
 */
public class RPCServer {
	private static final String RPC_QUEUE_NAME = "rpc_queue";

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.211.135");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		try {
			final Channel channel = connection.createChannel();
			channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
			channel.basicQos(1);

			System.out.println("Awaiting rpc request...");
			Consumer consumer = new DefaultConsumer(channel){
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope,
										   AMQP.BasicProperties properties, byte[] body) throws IOException {
					AMQP.BasicProperties replyProp = new AMQP.BasicProperties()
							.builder()
							.correlationId(properties.getCorrelationId())
							.build();
					String response = "";
					try {
						String message = new String(body, "UTF-8");
						System.out.println("Rpc request message:[" + message + "]");
						response = "[" + message +
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]";
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} finally {
						channel.basicPublish("", properties.getReplyTo(),
								replyProp, response.getBytes("UTF-8"));
						channel.basicAck(envelope.getDeliveryTag(), false);
					}

				}
			};
			channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
}
