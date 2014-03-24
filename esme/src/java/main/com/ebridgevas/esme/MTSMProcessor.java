//package com.ebridgevas.esme;
//
//import com.ebridge.sdp.smpp.pdu.SubmitSM;
//
//import com.ebridgevas.util.SubmitSmFactory;
//import org.apache.activemq.ActiveMQConnection;
//import org.apache.activemq.ActiveMQConnectionFactory;
//
//import javax.jms.*;
//import java.util.HashMap;
//
//public class MTSMProcessor implements Runnable {
//
//    private com.ebridge.sdp.smpp.Session smppSession;
//    private Session session;
//    private Destination destination;
//    private Connection connection;
//
//    public MTSMProcessor(com.ebridge.sdp.smpp.Session smppSession) {
//        System.out.println("\n\n\n### MTSMProcessor version 13.04\n\n\n");
//        this.smppSession = smppSession;
//        try {
//            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
//                    ActiveMQConnection.DEFAULT_BROKER_URL);
//            connection = connectionFactory.createConnection();
//            connection.start();
//            boolean transacted = false;
//            session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
//            destination = session.createQueue("smpp11-queue");
//        } catch (JMSException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
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
//                    /* sourceId|destinationId|text*/
//                    if (message instanceof ObjectMessage ) {
//                        try {
//                            ObjectMessage o = (ObjectMessage) message;
//                            HashMap msg = (HashMap) o.getObject();
//                            System.out.println("### MTSMProcessor ");
//                            System.out.println("### MTSMProcessor : " +
//                                                (String)msg.get("sourceId") + " - " +
//                                                (String)msg.get("destinationId") +
//                                                (String)msg.get("shortMessage"));
//                            SubmitSM submitSM = new SubmitSmFactory().create(
//                                    (String)msg.get("sourceId"),
//                                    (String)msg.get("destinationId"),
//                                    (String)msg.get("shortMessage"),
//                                    // (short)9280,
//                                    null,
//                                    (byte) 3,
//                                    (byte) 1 );
//                            System.out.println("###### submitSM : " + submitSM.debugString());
//                            smppSession.submit(submitSM);
//                        } catch(Exception e ) {
//                            System.out.println("Invalid message specified.");
//                            e.printStackTrace();
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
//
//                }
//
//            });
//        } catch (JMSException e) {
//            System.out.println("JMSException - " + e.getMessage());
//        }
//    }
//}