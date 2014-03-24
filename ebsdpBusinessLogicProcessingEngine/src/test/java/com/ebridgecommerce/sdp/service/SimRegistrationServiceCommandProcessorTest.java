package com.ebridgecommerce.sdp.service;

import com.ebridgecommerce.sdp.dao.SimRegistrationDAO;
import com.ebridgecommerce.sdp.disruptor.SimRegistrationService;
import com.ebridgecommerce.sdp.domain.Messages;
import com.ebridgecommerce.sdp.dto.Request;
import com.ebridgecommerce.sdp.dto.Response;
import com.ebridgecommerce.sdp.dto.SimRegistrationDTO;
import com.ebridgecommerce.sdp.util.TransactionFailedException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
* Created with IntelliJ IDEA.
* User: David
* Date: 6/28/12
* Time: 5:39 PM
* To change this template use File | Settings | File Templates.
*/
public class SimRegistrationServiceCommandProcessorTest {

    private SimRegistrationServiceCommandProcessor processor;
    private SimRegistrationService service;
    private SimRegistrationDAO dao;

    @Before
    public void setup(){
        service = mock(SimRegistrationService.class);
        dao = mock(SimRegistrationDAO.class);
        processor = new SimRegistrationServiceCommandProcessor( service);
    }

    @Test
    public void testInit(){
        assertNotNull(processor);
    }

    @Test
    public void testBufferCheckForPendingSimRegistrationInBufferAndWebService() throws TransactionFailedException {

        // arrange
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        SimRegistrationDTO sim = mock(SimRegistrationDTO.class);
        when(dao.getPendingSimRegistration(anyString())).thenReturn(null);

        // act
        processor.process(request, response);

        // verify
        verify(dao).getPendingSimRegistration(anyString());
        verify(service).getSubscriber(anyString());
    }

//    @Test
//    public void testBufferCheckForPendingSimRegistrationInBufferAndNotWebServiceIfBufferEntryExists() throws TransactionFailedException {
//
//        // arrange
//        Request request = mock(Request.class);
//        Response response = mock(Response.class);
//        SimRegistrationDTO sim = mock(SimRegistrationDTO.class);
//        when(dao.getPendingSimRegistration(anyString())).thenReturn(sim);
//
//        // act
//        processor.process(request, response);
//
//        // verify
//        verify(dao).getPendingSimRegistration(anyString());
//        verify(service, never()).getSubscriber(anyString());
//    }
//
//    @Test
//    public void testDisplayMainMenuIfNoPendingSimRegistration() throws TransactionFailedException {
//
//        // arrange
//        Request request = mock(Request.class);
//        Response response = mock(Response.class);
//
//        SimRegistrationDTO sim = mock(SimRegistrationDTO.class);
//        when(dao.getPendingSimRegistration(anyString())).thenReturn(null);
//        when(service.getSubscriber(anyString())).thenReturn(null);
//
//        // act
//        processor.process(request, response);
//
//        // verify
//        verify(dao).getPendingSimRegistration(anyString());
//        verify(service).getSubscriber(anyString());
//        verify(response).setPayload(Messages.SIM_REG_MAIN_MENU);
//    }

//    @Test
//    public void testCallSimRegistrationServiceIfNoSimRegistrationIsInBuffer() {
//
//        TransactionDTO txn = new TransactionDTO();
//
//        /* Does the No pending transaction is in buffer. */
//        // arrange
//        SimRegistrationDAO dao = mock(SimRegistrationDAO.class);
//
//        SimRegistrationDTO sim = null;
//        when(dao.getPendingTransaction(anyString())).thenReturn(sim);
//
//        // act
//
////        sim = dao.getPendingTransaction("");
//
//        // assert
////        assertNull(sim);
//    }
}
