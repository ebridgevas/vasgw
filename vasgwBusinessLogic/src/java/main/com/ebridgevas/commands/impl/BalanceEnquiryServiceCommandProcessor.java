//package com.ebridgevas.commands.impl;
//
//import com.ebridgevas.commands.ServiceCommandProcessor;
//import com.ebridgevas.model.MTSM;
//import com.ebridgevas.model.ServiceCommandRequest;
//import com.ebridgevas.model.ServiceCommandResponse;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.ebridgevas.services.ServerPDUEventListenerImpl.PREPAID_ACCOUNT_MANAGER;
//
///**
// * david@ebridgevas.com
// *
// */
//public class BalanceEnquiryServiceCommandProcessor implements ServiceCommandProcessor {
//
//    // TODO dependency injection - Google guice
//
//    @Override
//    public ServiceCommandResponse process(ServiceCommandRequest request) {
//
//        List<MTSM> result = new ArrayList<MTSM>();
//        result.add(new MTSM(
//                        request.getSourceId(),
//                        request.getDestinationId(),
//                        PREPAID_ACCOUNT_MANAGER.getAccountBalance(pdu.getSourceId())));
//        return null;
//    }
//}
