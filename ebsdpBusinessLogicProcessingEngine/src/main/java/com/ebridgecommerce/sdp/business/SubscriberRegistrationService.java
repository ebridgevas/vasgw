package com.ebridgecommerce.sdp.business;

import com.ebridgecommerce.sdp.dao.VasGatewayDao;
import com.ebridgecommerce.sdp.disruptor.SimRegistrationService;
import com.ebridgecommerce.sdp.domain.StatusDTO;
import com.ebridgecommerce.sdp.domain.SubscriberInfo;
import com.ebridgecommerce.sdp.domain.USSDResponse;
import com.ebridgecommerce.sdp.domain.UssdMenuItem;
import com.ebridgecommerce.sdp.util.BlankNameException;
import com.ebridgecommerce.sdp.util.InvalidNameException;
import com.ebridgecommerce.sdp.util.SubscriberNameValidator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/22/12
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubscriberRegistrationService {

    private SimRegistrationService simRegistrationService;
    private Map<StatusDTO, UssdMenuItem> menuItems;
    private SubscriberNameValidator subscriberNameValidator;

    public SubscriberRegistrationService() {
        // spawn new thread that starts WS or keeps trying.
        menuItems = VasGatewayDao.getUssdMenuItems();
    }


    public USSDResponse process(String msisdn, Map<String, String> msg) {

        /* Is it an initial dial? */



        StatusDTO pendingStatusDTO = getPending( msisdn );
        System.out.println("############# prompted for " + pendingStatusDTO );

        UssdMenuItem pendingMenuItem = menuItems.get(pendingStatusDTO);

        String requestPayload = msg.get("request-payload");
        if (requestPayload != null) {
            requestPayload = requestPayload.trim();
        }
        String responsePayload = "";
        try {
            if ( pendingMenuItem.getValidator().isValid(requestPayload) ) {
                System.out.println("############# adding" );
                Map<StatusDTO, String> info = new HashMap<StatusDTO, String>();
                info.put(pendingStatusDTO, requestPayload);
                VasGatewayDao.addInfo(msisdn, info);
                System.out.println("############# pending = " + getPending(msisdn) );
                if (getPending(msisdn) == StatusDTO.CAPTURED) {
//                    simRegistrationService.createSubscriber(subscriber);
                }
                responsePayload = menuItems.get(getPending(msisdn)).getMenuText();
            } else {
                System.out.println("############# invalid" );
            }

        } catch (BlankNameException e) {
            responsePayload = pendingMenuItem.getBlankErrorText();
        } catch (InvalidNameException e) {
            responsePayload = pendingMenuItem.getErrorText();
        }
        System.out.println("############## " + responsePayload);
        /* Validate */

//        if (subscriber == null) {
//            subscriber = new SubscriberInfo(msg.get("sourceId"), StatusDTO.MAIN);
//            VasGatewayDao.create(subscriber);
//            StatusDTO status = subscriber.getStatus();
//            return getUSSDResponse(msg, menuItems.get(status).getMenuText(), StatusDTO.isLast(status));
//        } else {
//
//        }
//
//        String shortCode = null;
//        String msisdn = null;
//        String sessionId = null;
//        Integer menuLevel = getMenuLevel(msisdn, sessionId);
//        Integer selection = null;
//        processMenuLevel(shortCode, msisdn, sessionId, menuLevel, selection);
        return null;
    }

    private StatusDTO getPending( String msisdn ) {

        Map<StatusDTO, String> info = VasGatewayDao.getSubscriberInfo( msisdn );
        for (StatusDTO item : StatusDTO.values()) {
            if ( ! info.containsKey(item) ) {
                return item;
            }
        }
        return null;
    }

    private Integer getMenuLevel(String msisdn, String sessionId) {
        // getMenuLevel(msisdn, session)
        //   if none, return 0
        return 0;
    }

    private USSDResponse processMenuLevel(Map<String, String> msg, Integer menuLevel, Integer selection) {
//        // String shortCode, String msisdn, String sessionId
//        switch (menuLevel) {
//            case 0:
//                return getUSSDResponse(shortCode, msisdn, sessionId, SIM_REGISTRATION_MENU, false);
//            case 1:
//                switch (selection) {
//                    case 1:
//                        if( mobileAlreadyRegistered(msisdn)) {
//                            return getUSSDResponse(shortCode, msisdn, sessionId, SIM_ALREADY_REGISTERED, true);
//                        }
//                        return null;
//                    case 2:
//                        return getUSSDResponse(shortCode, msisdn, sessionId, EXITING_SIM_REGISTRATION, true);
//                    default:
//                        return getUSSDResponse(shortCode, msisdn, sessionId, INVALID_SELECTION, false);
//                }
//            default:
//                return null;
//        }
        return null;
    }

    private boolean mobileAlreadyRegistered(String msisdn) {

//        if (service == null ) {
//            // check db
//        }
//        if ( service.getSubscriber(msisdn) == null) {
//            return false;
//        }
        return false;
    }

    protected USSDResponse getUSSDResponse(Map<String, String> msg, String payload, Boolean closeSession){
        USSDResponse response = new USSDResponse();
//        response.setMobileNumber(msisdn);
//        response.setShortCode(shortCode);
//        response.setSessionId(sessionId);
//        response.setPayload(
//                closeSession ?
//                        "81" + " " + sessionId + " " + "0 " + payload + "." :
//                        "72" + " " + sessionId + " " + 30000 + " 0 " + payload + ".");
        return response;
    }

    public static void main(String[] args) {
        SubscriberRegistrationService test = new SubscriberRegistrationService();
//        System.out.println(test.menuItems);
//
        String msisdn = "263733661588";
        Map<StatusDTO, String> subscriber = VasGatewayDao.getSubscriberInfo(msisdn);

        Map<String, String> msg = new HashMap<String, String>();
        msg.put("sourceId", msisdn);
        msg.put("request-payload", " Tekeshe                            ");


        test.process(msisdn, msg);
//        System.out.println("##########" + subscriber);
////        System.out.println(StatusDTO.next(subscriber.getStatus()));
////        System.out.println(test.menuItems.get(StatusDTO.next(subscriber.getStatus())).getMenuId());
//        if (subscriber == null) {
//            System.out.println("Creating ....");
//            Map<StatusDTO, String> info = new HashMap<StatusDTO, String>();
//            info.put(StatusDTO.MAIN, msisdn);
//            VasGatewayDao.create( msisdn, info);
//            subscriber = VasGatewayDao.getSubscriberInfo(msisdn);
//        }
//        System.out.println("##########" + subscriber);
//        System.out.println(test.menuItems.get(subscriber.getStatus()));
//
//        System.out.println(test.menuItems.get(subscriber.getStatus()).getMenuText());
//          Map<StatusDTO, String> info = new HashMap<StatusDTO, String>();
//            info.put(StatusDTO.next(StatusDTO.PROMPT_FIRSTNAME), "");
//          VasGatewayDao.addInfo(msisdn, info);
//        for (StatusDTO statusDTO : StatusDTO.values()) {

//        }
    }
}
