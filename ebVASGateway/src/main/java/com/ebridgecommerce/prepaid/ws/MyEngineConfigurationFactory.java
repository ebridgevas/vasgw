/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ebridgecommerce.prepaid.client.net;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.configuration.BasicClientConfig;

public class MyEngineConfigurationFactory implements EngineConfigurationFactory {

    public static EngineConfigurationFactory newFactory(Object param) {
        return new MyEngineConfigurationFactory();
    }

    public EngineConfiguration getClientEngineConfig() {
        BasicClientConfig cfg = new BasicClientConfig();
        cfg.deployTransport("MyTransport", new org.apache.axis.transport.http.HTTPSender());

        return cfg;
    }

    public EngineConfiguration getServerEngineConfig() {
        return null;
    }
    // AxisProperties.setProperty(EngineConfigurationFactory.SYSTEM_PROPERTY_NAME, "foo.bar.MyEngineConfigurationFactory");

}
