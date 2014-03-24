package com.ebridgecommerce.sdp.service;

import com.ebridgecommerce.sdp.dto.Request;
import com.ebridgecommerce.sdp.dto.Response;
import com.ebridgecommerce.sdp.util.TransactionFailedException;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/28/12
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ServiceCommandProcessor {
    Response process(Request request, Response response) throws TransactionFailedException;
}
