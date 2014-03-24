//package com.ebridgevas.model;
//
//import java.io.Serializable;
//
///**
// * david@ebridgevas.com
// *
// */
//
//public class Node implements Serializable {
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
//            String serviceId,
//            String nodeId,
//            String parentNodeId,
//            String productId,
//            String nodeDescription,
//            ChildType childType,
//            ServiceCommandProcessor serviceCommandProcessor) {
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
