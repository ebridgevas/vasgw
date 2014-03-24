package com.ebridgevas.model.tree;

import java.util.HashMap;
import java.util.Map;

/**
 * david@ebridgevas.com
 *
 */

/* ShortCode. */

    /* -- Menu. */

    /* ---- Products. */

    /* ----- Processor. */

    /*

        Message Flow Specification:

        2013
            1. Subscription Request
            2. Balance
            3. Cancel Subscription
                1.
                    1. $0.50
                        1. <DataBundlePurchase, $0.50>
                    2. $1.00
                        2. <DataBundlePurchase, $0.50>
                2. <BalanceEnquiry>
                3. <CancelSubscription>
        144

        Data Model:

        ussdMenus

        nodelId,    parentNodeId,   productId,  nodeDescription,            childType,  serviceCommandProcessClassName
        2013,       0,              0,          Millennium service,         SUB_NODE,   null
        144,        0,              0,          Data Bundle Purchase,       SUB_NODE,   null
        1000,       2013,           0,          Subscription Request,       SUB_NODE,   null
        1001,       2013,           0,          Balance,                    PROCESSOR,  com.ebridge.vasgw.command.BalanceEnquiryCommandProcessor
        1002,       2013,           0,          Cancel Subscription,        PROCESSOR,  com.ebridge.vasgw.command.CancelSubscriptionCommandProcessor
        1003,       1000            1,          null,                       PRODUCT,    com.ebridge.vasgw.command.VoiceBundlePurchaseCommandProcessor,2000
        1004,       1000,           2,          null,                       PRODUCT,    com.ebridge.vasgw.command.VoiceBundlePurchaseCommandProcessor,2001
        1005,       144,            1,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2002
        1006,       144,            2,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2003
        1007,       144,            3,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2004
        1008,       144,            4,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2005
        1009,       144,            5,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2006
        1010,       144,            6,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2007
        1011,       144,            7,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2008
        1012,       144,            8,          null,                       PRODUCT,    com.ebridge.vasgw.command.DataBundlePurchaseCommandProcessor,2009

        product
        serviceId,  parentNodeId,     productId,  productDescription, debit,  credit
        2013,       1000,               1,          $0.50,              0.50,   0.50
        2013,       1000,               2,          $1.00,              1.00,   1.00
        144,        144,                1,          6MB Data Bundle,    0.65,   0.66
        144,        144,                2,          10MB Data Bundle,   0.65,   0.66
        144,        144,                3,          40MB Data Bundle,   0.65,   0.66
        144,        144,                4,          150MB Data Bundle,  0.65,   0.66
        144,        144,                5,          320MB Data Bundle,  0.65,   0.66
        144,        144,                6,          800MB Data Bundle,  0.65,   0.66
        144,        144,                7,          2GB Data Bundle,    0.65,   0.66
        144,        144,                8,          4GBB Data Bundle,   0.65,   0.66
     */

public class ServiceCommandTree {

    private static final Map<String, TreeNode> NODES;

    static {

        // TODO load from database
        TreeNode<Content> dataBundleTree =
                new TreeNode<Content>(
                        new Content("144", null, null, "Data Bundle Purchase Service"));

        TreeNode<Content> purchaseOption =
                new TreeNode<Content>(
                        new Content("144", null, null, "Buy a bundle"));


        TreeNode<Content> balanceEnquiryOption =
                new TreeNode<Content>(
                        new Content("144", null, null, "Balance Enquiry"));

        NODES = new HashMap<String, TreeNode>();
        NODES.put("144", dataBundleTree);
    }

    public TreeNode getNode(Integer nodeId) {
        return null;
    }

    public static TreeNode<String> getSet1() {
        TreeNode<String> root = new TreeNode<String>("root");
        {
            TreeNode<String> node0 = root.addChild("node0");
            TreeNode<String> node1 = root.addChild("node1");
            TreeNode<String> node2 = root.addChild("node2");
            {
                TreeNode<String> node20 = node2.addChild(null);
                TreeNode<String> node21 = node2.addChild("node21");
                {
                    TreeNode<String> node210 = node21.addChild("node210");
                    TreeNode<String> node211 = node21.addChild("node211");
                }
            }
            TreeNode<String> node3 = root.addChild("node3");
            {
                TreeNode<String> node30 = node3.addChild("node30");
            }
        }

        return root;
    }

    public static TreeNode<String> getSetSOF() {
        TreeNode<String> root = new TreeNode<String>("root");
        {
            TreeNode<String> node0 = root.addChild("node0");
            TreeNode<String> node1 = root.addChild("node1");
            TreeNode<String> node2 = root.addChild("node2");
            {
                TreeNode<String> node20 = node2.addChild(null);
                TreeNode<String> node21 = node2.addChild("node21");
                {
                    TreeNode<String> node210 = node20.addChild("node210");
                }
            }
        }

        return root;
    }
}
