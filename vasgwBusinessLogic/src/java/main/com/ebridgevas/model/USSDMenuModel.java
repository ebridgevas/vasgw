//package com.ebridgevas.model;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * david@ebridgevas.com
// *
// */
//public class USSDMenuModel {
//
//    /* ShortCode. */
//
//    /* -- Menu. */
//
//    /* ---- Products. */
//
//    /* ----- Processor. */
//
//    /*
//
//        Message Flow Specification:
//
//        2013
//            1. Subscription Request
//            2. Balance
//            3. Cancel Subscription
//                1.
//                    1. $0.50
//                        1. <DataBundlePurchase, $0.50>
//                    2. $1.00
//                        2. <DataBundlePurchase, $0.50>
//                2. <BalanceEnquiry>
//                3. <CancelSubscription>
//        144
//
//        Data Model:
//
//        ussdMenus
//
//        nodelId,    parentNodeId,   productId,  nodeDescription,            childType,  serviceCommandProcessClassName
//        2013,       0,              0,          Millennium service,         SUB_NODE,   null
//        144,        0,              0,          Data Bundle Purchase,       SUB_NODE,   null
//        1000,       2013,           0,          Subscription Request,       SUB_NODE,   null
//        1001,       2013,           0,          Balance,                    PROCESSOR,  com.ebridge.vasgw.command.BalanceEnquiryCommandProcessor
//        1002,       2013,           0,          Cancel Subscription,        PROCESSOR,  com.ebridge.vasgw.command.CancelSubscriptionCommandProcessor
//        1003,       1000            1,          null,                       PRODUCT,    com.ebridge.vasgw.command.VoiceBundlePurchaseCommandProcessor,2000
//        1004,       1000,           2,          null,                       PRODUCT,    com.ebridge.vasgw.command.VoiceBundlePurchaseCommandProcessor,2001
//        1005,       144,            1,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2002
//        1006,       144,            2,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2003
//        1007,       144,            3,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2004
//        1008,       144,            4,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2005
//        1009,       144,            5,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2006
//        1010,       144,            6,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2007
//        1011,       144,            7,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2008
//        1012,       144,            8,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2009
//
//        product
//        serviceId,  parentNodeId,     productId,  productDescription, debit,  credit
//        2013,       1000,               1,          $0.50,              0.50,   0.50
//        2013,       1000,               2,          $1.00,              1.00,   1.00
//        144,        144,                1,          6MB Data Bundle,    0.65,   0.66
//        144,        144,                2,          10MB Data Bundle,   0.65,   0.66
//        144,        144,                3,          40MB Data Bundle,   0.65,   0.66
//        144,        144,                4,          150MB Data Bundle,  0.65,   0.66
//        144,        144,                5,          320MB Data Bundle,  0.65,   0.66
//        144,        144,                6,          800MB Data Bundle,  0.65,   0.66
//        144,        144,                7,          2GB Data Bundle,    0.65,   0.66
//        144,        144,                8,          4GBB Data Bundle,   0.65,   0.66
//     */
//
//    private static final Map<String, Node> SERVICE_COMMANDS_MENU_NODES;
//
//    static {
//
//        SERVICE_COMMANDS_MENU_NODES = new HashMap<String, Node>();
//
//        try {
//            // TODO store in database
//            //                                               serviceId, nodelId,   parentNodeId,   productId,  nodeDescription,            childType,          serviceCommandProcessClassName
//            SERVICE_COMMANDS_MENU_NODES.put("2013", new Node("2013",    "2013",    "0",            "0",       "Millennium service",       ChildType.SUB_NODE,  null));
//            SERVICE_COMMANDS_MENU_NODES.put("144",  new Node("144",     "144",     "0",            "0",       "Data Bundle Purchase",     ChildType.SUB_NODE,  null));
//
//            SERVICE_COMMANDS_MENU_NODES.put("1000", new Node("2013",    "1000",    "2013",         "0",       "Subscription Request",     ChildType.SUB_NODE,  null));
//            SERVICE_COMMANDS_MENU_NODES.put("1001", new Node("2013",    "1001",    "2013",         "0",       "Balance",                  ChildType.PROCESSOR, create("com.ebridge.vasgw.command.BalanceEnquiryCommandProcessor",        ServiceCommandProcessor.class)));
//            SERVICE_COMMANDS_MENU_NODES.put("1002", new Node("2013",    "1002",    "2013",         "0",       "Cancel Subscription",      ChildType.PROCESSOR, create("com.ebridge.vasgw.command.CancelSubscriptionCommandProcessor",    ServiceCommandProcessor.class)));
//
//            SERVICE_COMMANDS_MENU_NODES.put("1003", new Node("2013",    "1003",    "1000",         "1",        null,                       ChildType.PRODUCT,   create("com.ebridge.vasgw.command.VoiceBundlePurchaseCommandProcessor",   ServiceCommandProcessor.class)));
//            SERVICE_COMMANDS_MENU_NODES.put("1004", new Node("2013",    "1004",    "1000",         "2",        null,                       ChildType.PRODUCT,   create("com.ebridge.vasgw.command.VoiceBundlePurchaseCommandProcessor",   ServiceCommandProcessor.class)));
//
//            SERVICE_COMMANDS_MENU_NODES.put("1005", new Node("144",     "1005",    "144",          "1",        null,                       ChildType.PRODUCT,   create("com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor",    ServiceCommandProcessor.class)));
//            SERVICE_COMMANDS_MENU_NODES.put("1006", new Node("144",     "1006",    "144",          "2",        null,                       ChildType.PRODUCT,   create("com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor",    ServiceCommandProcessor.class)));
//            SERVICE_COMMANDS_MENU_NODES.put("1007", new Node("144",     "1007",    "144",          "3",        null,                       ChildType.PRODUCT,   create("com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor",    ServiceCommandProcessor.class)));
//            SERVICE_COMMANDS_MENU_NODES.put("1008", new Node("144",     "1008",    "144",          "4",        null,                       ChildType.PRODUCT,   create("com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor",    ServiceCommandProcessor.class)));
//            SERVICE_COMMANDS_MENU_NODES.put("1009", new Node("144",     "1009",    "144",          "5",        null,                       ChildType.PRODUCT,   create("com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor",    ServiceCommandProcessor.class)));
//            SERVICE_COMMANDS_MENU_NODES.put("1010", new Node("144",     "1010",    "144",          "6",        null,                       ChildType.PRODUCT,   create("com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor",    ServiceCommandProcessor.class)));
//            SERVICE_COMMANDS_MENU_NODES.put("1011", new Node("144",     "1011",    "144",          "7",        null,                       ChildType.PRODUCT,   create("com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor",    ServiceCommandProcessor.class)));
//            SERVICE_COMMANDS_MENU_NODES.put("1012", new Node("144",     "1012",    "144",          "8",        null,                       ChildType.PRODUCT,   create("com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor",    ServiceCommandProcessor.class)));
//
//        } catch (ClassNotFoundException e) {
//            // TODO handle exception.
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO handle exception.
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            // TODO handle exception.
//            e.printStackTrace();
//        }
//
//    }
//
//    public List<Node> getChildrenFor( String nodeId ) {
//        return null;
//    }
//
//    private static <T> T create ( final String className, Class<T> interfaceClass )
//                            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
//
//        final Class<T> clazz = (Class<T>) Class.forName(className).asSubclass(interfaceClass);
//        return clazz.newInstance();
//    }
//}
//
//class Node {
//
//    private final String serviceId;
//    private final String nodeId;
//    private final String parentNodeId;
//    private final String productId;
//    private final String nodeDescription;
//    private final ChildType childType;
//    private final ServiceCommandProcessor serviceCommandProcessor;
//
//    Node(
//          String serviceId,
//          String nodeId,
//          String parentNodeId,
//          String productId,
//          String nodeDescription,
//          ChildType childType,
//          ServiceCommandProcessor serviceCommandProcessor) {
//
//        this.serviceId = serviceId;
//        this.nodeId = nodeId;
//        this.parentNodeId = parentNodeId;
//        this.productId = productId;
//        this.nodeDescription = nodeDescription;
//        this.childType = childType;
//        this.serviceCommandProcessor = serviceCommandProcessor;
//    }
//
//    String getServiceId() {
//        return serviceId;
//    }
//
//    String getNodeId() {
//        return nodeId;
//    }
//
//    String getParentNodeId() {
//        return parentNodeId;
//    }
//
//    String getProductId() {
//        return productId;
//    }
//
//    String getNodeDescription() {
//        return nodeDescription;
//    }
//
//    ChildType getChildType() {
//        return childType;
//    }
//
//    ServiceCommandProcessor getServiceCommandProcessor() {
//        return serviceCommandProcessor;
//    }
//}
//
//interface ServiceCommandProcessor {
//    void process();
//}