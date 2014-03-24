package com.ebridgevas.internaltelecelservices.util;

import com.ebridgevas.internaltelecelservices.model.ServiceCommand;
import com.ebridgevas.internaltelecelservices.model.ServiceCommandRequest;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * david@tekeshe.com
 *
 */
public class ServiceCommandParserTest {

    private ServiceCommandParser parser;

    @Before
    public void init() {
        parser = new ServiceCommandParser();
    }

    @Test
    public void testForInitialDial() {

        ServiceCommandRequest request
                = new ServiceCommandRequest(
                            "263733803480",
                            "2013",
                            "15 2 00 2 2  2",
                            "1234567890");

        assertEquals(ServiceCommand.MENU_LISTING, parser.parse(request));
    }
}