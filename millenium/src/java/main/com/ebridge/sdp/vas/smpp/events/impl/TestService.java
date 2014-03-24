package com.ebridge.sdp.vas.smpp.events.impl;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class TestService {

    private String queueName;
    private Connection connection;
    private Session session;
    private Destination destination;

    public TestService(String queueName) throws JMSException {

        this.queueName = queueName;

        ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory(
                        ActiveMQConnection.DEFAULT_USER,
                        ActiveMQConnection.DEFAULT_PASSWORD,
                        ActiveMQConnection.DEFAULT_BROKER_URL);
        connection = connectionFactory.createConnection();
        connection.start();
        boolean transacted = false;
        session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
        destination = session.createQueue(queueName);
    }

    public void process() throws JMSException {

        MessageConsumer messageConsumer = session.createConsumer(destination);
        messageConsumer.setMessageListener( new MessageListener() {

            @Override
            public void onMessage(Message message) {

                if (message instanceof ObjectMessage ) {
                    try {
                        ObjectMessage o = (ObjectMessage) message;
                        HashMap msg = (HashMap) o.getObject();

                        System.out.println( "Processing ... \n" +
                                "\t{ uuid : " + (String)msg.get("uuid") + ",\n" +
                                "\tsourceId : " + (String)msg.get("sourceId") + ",\n" +
                                "\tdestinationId : " + (String)msg.get("destinationId") + ",\n" +
                                "\tshortMessage : " + (String)msg.get("shortMessage") + ",\n" +
                                "\tdebugString : " + (String)msg.get("debugString") + ",\n" +
                                "\tpduDate : " + (String)msg.get("pduDate") + ",\n" +
                                "\tpduType : " + (String)msg.get("pduType") + "}\n");

                    } catch(JMSException e ) {
                        System.out.println("Invalid message specified.");
                        e.printStackTrace();
                    }
                }
            }

        });
        connection.setExceptionListener(new ExceptionListener() {

            @Override
            public void onException(JMSException e) {
                e.printStackTrace();

            }
        });
    }

    public static void  main(String[] args) {
        try {
            new TestService(args[0]).process();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
