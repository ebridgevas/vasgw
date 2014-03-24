package com.ebridgecommerce.smppgw;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

public class VASGateway {
	
	private Session session;
	private Destination destination;
	private Connection connection;
	
	public VASGateway(){
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
					ActiveMQConnection.DEFAULT_BROKER_URL);
			connection = connectionFactory.createConnection();
			connection.start();
			boolean transacted = false;
			session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue("vasgw-queue");
			run();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void run() {
		try {
			MessageConsumer messageConsumer = session.createConsumer(destination);
			messageConsumer.setMessageListener( new MessageListener() {

				@Override
				public void onMessage(Message message) {
					/* String message */
					/* sourceId|destinationId|text*/
					if (message instanceof TextMessage ) {
						try {
							String[] tokens = ((TextMessage)message).getText().split("|");
							Map<String,String> msg = new HashMap<String,String>();
							msg.put("sourceId", tokens[0]);
							msg.put("destinationId", tokens[1]);
							msg.put("text", tokens[2]);
//							process(msg);
						} catch(Exception e ) {
							System.out.println("Invalid message specified.");
						}
					}
				}
				
			});
			connection.setExceptionListener(new ExceptionListener() {

				@Override
				public void onException(JMSException arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
		} catch (JMSException e) {
			System.out.println("JMSException - " + e.getMessage());
		}		
	}
}
