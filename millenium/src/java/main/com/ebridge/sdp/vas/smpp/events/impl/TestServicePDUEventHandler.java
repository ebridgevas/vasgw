package com.ebridge.sdp.vas.smpp.events.impl;


import com.ebridge.sdp.vas.services.MilleniumService;
import com.ebridge.sdp.vas.smpp.events.ServerPDUEventHandler;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import org.apache.log4j.Logger;
import zw.co.ebridge.jms.JMSWriter;
import zw.co.telecel.akm.millenium.dto.PduDto;

public class TestServicePDUEventHandler extends AbstractPDUEventHandler implements ServerPDUEventHandler {

    private Logger log = Logger.getLogger(TestServicePDUEventHandler.class.getName());
    private JMSWriter jmsWriter;
    //private CalculatorService service;
    private MilleniumService service;

    public TestServicePDUEventHandler(String queueName) throws JMSException {
        jmsWriter = new JMSWriter( queueName );
        service = new MilleniumService();
    }

    @Override
    public String handleEvent( PduDto pdu ) {

        Map<String, String> msg = new HashMap<String, String>();

        msg.put("uuid", pdu.getUuid());
        msg.put("sourceId", pdu.getSourceId());
        msg.put("destinationId", pdu.getDestinationId());
        msg.put("shortMessage", pdu.getShortMessage());
        msg.put("debugString", pdu.getDebugString());
        msg.put("pduDate", new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(pdu.getPduDate()));
        msg.put("pduType", pdu.getPduType());

        jmsWriter.write(msg);

        log.debug("{ msisdn : " + pdu.getSourceId() + ", payload : " + pdu.getShortMessage()  + "}");

//        return createResponse(
//                getSessionId(pdu.getShortMessage()),
//                "Service not yet implemented.");
        String response = service.process(pdu.getSourceId(),  getContent(pdu.getShortMessage()));
        return createResponse(
                getSessionId(pdu.getShortMessage()), response, response.startsWith("-"));
    }

    protected static String getContent( String payload ) {
        String[] tokens = payload.split(" ");
        return tokens.length > 6 ? str(Arrays.copyOfRange(tokens, 7, tokens.length)) : "";
    }

    protected static String str(String[] a) {
        StringBuilder sb = new StringBuilder();
        for (String s : a) {
            sb.append(s + " ");
        }
        return sb.toString().trim();
    }

}
