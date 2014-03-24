//package com.ebridgecommerce.sdp.disruptor;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
//* Created with IntelliJ IDEA.
//* User: David
//* Date: 6/20/12
//* Time: 9:08 PM
//* To change this template use File | Settings | File Templates.
//*/
//
//import org.apache.activemq.ActiveMQConnection;
//import org.apache.activemq.ActiveMQConnectionFactory;
//import zw.co.ebridge.jms.JMSWriter;
//import zw.co.telecel.akm.simreg.external.SubscriberRaw;
//
//import javax.jms.*;
//
//public class WebServiceQueueReader implements Runnable {
//
//    private WebServiceOutputDisruptor simRegistrar;
//    private JMSWriter jmsWriter;
//
//    private Session session;
//    private Destination destination;
//    private Connection connection;
//
//    public WebServiceQueueReader(String outputQueue, String mtsmQueue, String endpoint ) {
//
//        simRegistrar = new WebServiceOutputDisruptor( endpoint );
//
//        try {
//
//            ActiveMQConnectionFactory connectionFactory =
//                    new ActiveMQConnectionFactory(  ActiveMQConnection.DEFAULT_USER,
//                                                    ActiveMQConnection.DEFAULT_PASSWORD,
//                                                    ActiveMQConnection.DEFAULT_BROKER_URL);
//            connection = connectionFactory.createConnection();
//            connection.start();
//            boolean transacted = false;
//            session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
//            destination = session.createQueue( outputQueue );
//
//            jmsWriter = new JMSWriter( mtsmQueue );
//
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void start() {
//        Thread serverThread = new Thread(this);
//        serverThread.start();
//    }
//
//    @Override
//    public void run() {
//
//        try {
//            MessageConsumer messageConsumer = session.createConsumer(destination);
//            messageConsumer.setMessageListener( new MessageListener() {
//
//                @Override
//                public void onMessage(Message message) {
//
//                    if (message instanceof ObjectMessage ) {
//                        try {
//                            ObjectMessage o = (ObjectMessage) message;
//                            process((HashMap) o.getObject());
//                        } catch(Exception e ) {
//                            System.out.println("########## WARNING - Invalid message specified.");
//                        }
//                    }
//                }
//
//            });
//            connection.setExceptionListener(new ExceptionListener() {
//
//                @Override
//                public void onException(JMSException arg0) {
//                    // TODO Auto-generated method stub
//                }
//
//            });
//        } catch (JMSException e) {
//            System.out.println("JMSException - " + e.getMessage());
//        }
//    }
//
//    private void process(HashMap msg) {
//        if( "sim-registration".equalsIgnoreCase((String) msg.get("msg-type")) ) {
//            SubscriberRaw subscriber = simRegistrar.getSubscriber((String)msg.get("sourceId"));
//            if (subscriber == null) {
//                simRegistrar.createSubscriber(msg);
//            } else {
//                msg.put("destinationId", msg.get("sourceId"));
//                msg.put("sourceId", msg.get("shortCode"));
//                msg.put("shortMessage", "Already registered by " + subscriber.getFirstName() + " " + subscriber.getLastName());
//                jmsWriter.write(msg);
//            }
//        }
//    }
//
//    public static void main( String[] args ) {
//        String outputQueue = args[0];  // "simregistration-queue"
//        String mtsmQueue = args[1];    // "smpp11-queue"
//        String endpoint = args[2];     // "http://10.10.4.28:8080/RegisterSubscriber/RegisterSubscriber"
//        new WebServiceQueueReader(outputQueue, mtsmQueue, endpoint).start();
//    }
//}