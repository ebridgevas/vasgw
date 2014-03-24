package com.ebridgecommerce.esme;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
//import com.ebridgecommerce.smsc.processors.PDUProcessorKey;
//import com.ebridgecommerce.smsc.processors.SmppPDUProcessor;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

public class MTSMProcessor implements Runnable {

//	private Map<PDUProcessorKey, SmppPDUProcessor> smppPDUProcessors;

	private Session session;
	private Destination destination;
	private Connection connection;
	private SMPPTransciever transceiver;
	
	public MTSMProcessor(SMPPTransciever transceiver, String queueName) {
		this.transceiver = transceiver;
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
					ActiveMQConnection.DEFAULT_BROKER_URL);
			connection = connectionFactory.createConnection();
			connection.start();
			boolean transacted = false;
			session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
			if( "196.2.77.23".equals(SMPPTransciever.smppIPAdress) ||
					"196.2.77.25".equals(SMPPTransciever.smppIPAdress)){ 
				if( "196.2.77.23".equals(SMPPTransciever.smppIPAdress) ){ 
					destination = session.createQueue("ussd-23-queue");
				} else if( "196.2.77.25".equals(SMPPTransciever.smppIPAdress) ){ 
					destination = session.createQueue("ussd-25-queue");
				}
			} else {
				destination = session.createQueue(queueName.toLowerCase() + "-queue");
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void start() {
		Thread serverThread = new Thread(this);
		serverThread.start();
	}

	@Override
	public void run() {

		try {
			MessageConsumer messageConsumer = session.createConsumer(destination);
			messageConsumer.setMessageListener( new MessageListener() {

				@Override
				public void onMessage(Message message) {
					/* String message */
					/* sourceId|destinationId|text*/
					if (message instanceof ObjectMessage ) {
						try {
							ObjectMessage o = (ObjectMessage) message;
						  HashMap msg = (HashMap) o.getObject();
						  System.out.println("############# MTSMProcessor ");
						  System.out.println((String)msg.get("sourceId") + " - " + (String)msg.get("destinationId") + (String)msg.get("shortMessage"));
						  transceiver.submit((String)msg.get("sourceId"), (String)msg.get("destinationId"), (String)msg.get("shortMessage"));
						} catch(Exception e ) {
							System.out.println("Invalid message specified.");
							e.printStackTrace();
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