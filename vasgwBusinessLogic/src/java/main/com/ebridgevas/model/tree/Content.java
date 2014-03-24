package com.ebridgevas.model.tree;

import com.ebridgevas.commands.ServiceCommandProcessor;

/**
 * david@ebridgevas.com
 *
 */

public class Content {

    private final String serviceId;
    private final Product product;
    private final ServiceCommandProcessor serviceCommandProcessor;
    private final String narrative;

    public Content(String serviceId,
                   Product product,
                   ServiceCommandProcessor serviceCommandProcessor,
                   String narrative) {

        this.serviceId = serviceId;
        this.product = product;
        this.serviceCommandProcessor = serviceCommandProcessor;
        this.narrative = narrative;
    }

    public String getServiceId() {
        return serviceId;
    }

    public Product getProduct() {
        return product;
    }

    public ServiceCommandProcessor getServiceCommandProcessor() {
        return serviceCommandProcessor;
    }

    public String getNarrative() {
        return narrative;
    }
}


