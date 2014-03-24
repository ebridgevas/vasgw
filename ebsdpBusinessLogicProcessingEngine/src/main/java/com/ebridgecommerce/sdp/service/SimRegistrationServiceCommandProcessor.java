package com.ebridgecommerce.sdp.service;

import com.ebridgecommerce.sdp.dao.SimRegistrationDAO;
import com.ebridgecommerce.sdp.disruptor.SimRegistrationService;
import com.ebridgecommerce.sdp.domain.Messages;
import com.ebridgecommerce.sdp.dto.Request;
import com.ebridgecommerce.sdp.dto.Response;
import com.ebridgecommerce.sdp.dto.SimRegistrationDTO;
import com.ebridgecommerce.sdp.util.*;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/28/12
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimRegistrationServiceCommandProcessor implements ServiceCommandProcessor {

    private SimRegistrationService service;

//    public static Statement statement;

    public SimRegistrationServiceCommandProcessor( SimRegistrationService service) {
        this.service = service;
        try {
            SimRegistrationDAO.init();
//            statement = SimRegistrationDAO.getConnection().createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response process(Request request, Response response) throws TransactionFailedException {

        /* Sensible defaults. */
        response.setTerminate(Boolean.FALSE);

        /* Check for pending registration. */
        SimRegistrationDTO pendingRegistration = SimRegistrationDAO.getPendingSimRegistration(request.getMsIsdn());

        if ( pendingRegistration == null ) {
            System.out.println("### Checking if subscriber is not registered on Telecel Service");
            if ( service.getSubscriber(request.getMsIsdn()) == null ) {
                if ( SimRegistrationDAO.initRegistrationFor(request.getMsIsdn()) ) {
                    response.setPayload(Messages.SIM_REG_MAIN_MENU );
                } else {
                    System.out.println("### FATAL : Failed to initialize registration for " + request.getMsIsdn());
                    throw new TransactionFailedException("A problem occured. Please try again.");
                }
                return response;
            } else {
                response.setPayload(Messages.SIM_REG_ALREADY_REGISTERED);
                response.setTerminate(Boolean.TRUE);
                log( request, Messages.SIM_REG_ALREADY_REGISTERED );
                return response;
            }
        } else {
            /* if prodical son.  */
            if (request.getPayload() == null) {
                String ussdMenu = getUssdMenu(pendingRegistration.getState());
                if ( ussdMenu != null) {
                    response.setPayload(ussdMenu);
                    return response;
                } else {
                    response.setPayload(Messages.SIM_REG_ALREADY_REGISTERED);
                    response.setTerminate(Boolean.TRUE);
                    log( request, Messages.SIM_REG_ALREADY_REGISTERED );
                    return response;
                }
            }
            /* Pending registration exists.*/
            /* If main menu */
            if ("main".equals(pendingRegistration.getState())) {
                if ("1".equals(request.getPayload().trim())) {
                    /* prompt for next missing item. */
                    pendingRegistration.setState("firstname");
                    SimRegistrationDAO.updateRegistration( pendingRegistration );
                    response.setPayload(Messages.SIM_REG_PROMPT_FIRSTNAME);
                } else if ("2".equals(request.getPayload().trim())) {
                    /* Exit */
                    response.setPayload(Messages.SIM_REG_EXIT);
                    response.setTerminate(Boolean.TRUE);
                } else {
                    /* Invalid selection. */
                    response.setPayload(Messages.SIM_REG_INVALID_SELECTION);
                    response.setPayload(Messages.SIM_REG_EXIT);
                    log( request, request.getPayload() + " :: " + Messages.SIM_REG_INVALID_SELECTION );
                }
            } else if ("firstname".equals(pendingRegistration.getState())) {
                try {
                    if ( SubscriberNameValidator.isValid(request.getPayload()) ) {
                        pendingRegistration.setFirstname(request.getPayload().trim());
                        pendingRegistration.setState("lastname");
                        SimRegistrationDAO.updateRegistration(pendingRegistration);
                        response.setPayload(Messages.SIM_REG_PROMPT_LASTNAME);
                    }
                } catch (BlankNameException e) {
                    response.setPayload(Messages.SIM_REG_BLANK_FIRSTNAME);
                    log( request, request.getPayload() + " :: " + Messages.SIM_REG_BLANK_FIRSTNAME );
                } catch (InvalidNameException e) {
                    response.setPayload(Messages.SIM_REG_INVALID_FIRSTNAME);
                    log( request, request.getPayload() + " :: " + Messages.SIM_REG_INVALID_FIRSTNAME );
                }
            } else if ("lastname".equals(pendingRegistration.getState())) {
                try {
                    if ( SubscriberNameValidator.isValid(request.getPayload()) ) {
                        pendingRegistration.setLastname(request.getPayload().trim());
                        pendingRegistration.setState("id_number");
                        SimRegistrationDAO.updateRegistration(pendingRegistration);
                        response.setPayload(Messages.SIM_REG_PROMPT_ID_NUMBER);
                    }
                } catch (BlankNameException e) {
                    response.setPayload(Messages.SIM_REG_BLANK_LASTNAME);
                    log( request, request.getPayload() + " :: " + Messages.SIM_REG_BLANK_LASTNAME );
                } catch (InvalidNameException e) {
                    response.setPayload(Messages.SIM_REG_INVALID_LASTNAME);
                    log( request, request.getPayload() + " :: " + Messages.SIM_REG_INVALID_LASTNAME );
                }
            } else if ("id_number".equals(pendingRegistration.getState())) {
                try {
                    String id = SubscriberIdNumberValidator.editIdNumber(request.getPayload());
                    if ( SubscriberIdNumberValidator.isValid(id) ) {
                        pendingRegistration.setIdNumber(id);
                        pendingRegistration.setState("physical_address");
                        SimRegistrationDAO.updateRegistration(pendingRegistration);
                        response.setPayload(Messages.SIM_REG_PROMPT_PHYSICAL_ADDRESS);
                    }
                } catch (BlankNameException e) {
                    System.out.println("Error id number for " + request.getMsIsdn() + ", " + request.getPayload() + " :: " + e.getMessage());
                    response.setPayload(Messages.SIM_REG_BLANK_IDNUMBER);
                    log( request, request.getPayload() + " :: " + Messages.SIM_REG_BLANK_IDNUMBER );
                } catch (InvalidNameException e) {
                    System.out.println("Error id number for " + request.getMsIsdn() + ", " + request.getPayload() + " :: " + e.getMessage());
                    response.setPayload(Messages.SIM_REG_INVALID_IDNUMBER);
                    log( request, request.getPayload() + " :: " + Messages.SIM_REG_INVALID_IDNUMBER );
                }
            } else if ("physical_address".equals(pendingRegistration.getState())) {
                try {
                    if ( SubscriberPhysicalAddressValidator.isValid(request.getPayload()) ) {
                        pendingRegistration.setPhysicalAddress(request.getPayload().trim());
                        pendingRegistration.setState("captured");
                        SimRegistrationDAO.updateRegistration(pendingRegistration);
                        if ( service.createSubscriber( pendingRegistration ) ) {
                            pendingRegistration.setState("registered");
                            SimRegistrationDAO.updateRegistration(pendingRegistration);
                        }
                        response.setPayload("Hi " + pendingRegistration.getFirstname() + ",Thank you for registering your line. It will be active within 1hr, SO GO AHEAD TELL SOMEONE!");
                        response.setTerminate(Boolean.TRUE);
                        System.out.println("**********************************************************************");
                        System.out.println();
                        System.out.println("RECEIVED SUBSCRIBER DETAILS");
                        System.out.println();
                        System.out.println("First Name : " + pendingRegistration.getFirstname());
                        System.out.println("Last Name : " + pendingRegistration.getLastname());
                        System.out.println("ID/Passport Number : " + pendingRegistration.getIdNumber());
                        System.out.println("Address : " + pendingRegistration.getPhysicalAddress());
                        System.out.println("Mobile Number : " + pendingRegistration.getMsIsdn());
                        System.out.println();
                        System.out.println("**********************************************************************");

                    }
                } catch (BlankNameException e) {
                    response.setPayload(Messages.SIM_REG_BLANK_PHYSICAL_ADDRESS);
                    log( request, request.getPayload() + " :: " + Messages.SIM_REG_BLANK_PHYSICAL_ADDRESS );
                } catch (InvalidNameException e) {
                    response.setPayload(Messages.SIM_REG_INVALID_PHYSICAL_ADDRESS);
                    log( request, request.getPayload() + " :: " + Messages.SIM_REG_INVALID_PHYSICAL_ADDRESS );
                }
            } else {
                response.setPayload(Messages.SIM_REG_ALREADY_REGISTERED);
                response.setTerminate(Boolean.TRUE);
                log( request, Messages.SIM_REG_ALREADY_REGISTERED );
            }
            System.out.println("### Received " + request.getMsIsdn() + " : " + pendingRegistration.getState() + " information");
            return response;
        }
    }

    private String getUssdMenu(String state) {
        if ("main".equals(state)) {
            return Messages.SIM_REG_MAIN_MENU;
        } else if ("firstname".equals(state)) {
            return Messages.SIM_REG_PROMPT_FIRSTNAME;
        } else if ("lastname".equals(state)) {
            return Messages.SIM_REG_BLANK_LASTNAME;
        } else if ("id_number".equals(state)) {
            return Messages.SIM_REG_PROMPT_ID_NUMBER;
        } else if ("physical_address".equals(state)) {
            return Messages.SIM_REG_PROMPT_PHYSICAL_ADDRESS;
        } else {
            return null;
        }
    }

    protected void log(Request request, String message ) {
        SimRegistrationDAO.log(
                "" + (System.currentTimeMillis() + 1),
                request.getMsIsdn(),
                "177",
                "simreg",
                BigDecimal.ZERO,
                message,
                "096",
                null,
                "177"
                );
    }
    public static void main(String[] args) {
//        SimRegistrationService service = new SimRegistrationServiceStub("");
//        SimRegistrationServiceCommandProcessor processor = new SimRegistrationServiceCommandProcessor(service);
//
//// Request request = new Request("263733661588", "12345", null);
//// Request request = new Request("263733661588", "12345", "2");
//// Request request = new Request("263733661588", "12345", "1");
//// Request request = new Request("263733661588", "12345", " David ");
//// Request request = new Request("263733661588", "12345", " Tekeshe ");
//// Request request = new Request("263733661588", "12345", " ");
//// Request request = new Request("263733661588", "12345", " 63 788429 Y 50 ");
//        Request request = new Request("263733661588", "12345", " 27 The Oval, 274 West Street, Lyttleton, Pretoria ");
//        Response response = new Response(request);
//
//        try {
//            response = processor.process(request, response);
//            System.out.println(response);
//        } catch (TransactionFailedException e) {
//            e.printStackTrace();
//        }
    }
}
