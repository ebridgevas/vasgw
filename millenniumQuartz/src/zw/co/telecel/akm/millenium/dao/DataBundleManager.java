package zw.co.telecel.akm.millenium.dao;//package com.ebridge.sdp.vas.dao;
//
//import com.zw.ebridge.domain.DataBundlePrice;
//import zw.co.ebridge.db.DBAdapter;
//
//import java.util.Map;
//
///**
// * Created with IntelliJ IDEA.
// * User: David
// * Date: 9/1/12
// * Time: 7:23 AM
// * To change this template use File | Settings | File Templates.
// */
//public class DataBundleManager  {
//
//    private Map<Integer,DataBundlePrice> priceList;
//
//    public DataBundleManager() {
//        try {
//            DBAdapter.init();
//            priceList = DBAdapter.getDataBundlePriceList(Boolean.FALSE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String getPriceList() {
//        String result = "";
//        for (int idx = 0; idx <= 7; ++idx) {
//            result += priceList.get(idx).getBundleNarration() + "\n";
//        }
//        return result;
//    }
//
//    public DataBundlePrice getPriceFor(Integer id) {
//        return priceList.get(id);
//    }
//
//}
