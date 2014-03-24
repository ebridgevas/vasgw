package com.ebridgevas.model;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: david
 * Date: 2/19/14
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class Rules {

    private String txnType;
    private String productCode;

    private String resetFrequency; /* e.g. daily, monthly */
    private BigDecimal limit;

}
