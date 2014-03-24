//package com.ebridgecommerce.dao;
//
//import com.ebridgecommerce.domain.TransactionDTO;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created with IntelliJ IDEA.
// * User: David
// * Date: 7/1/12
// * Time: 7:32 AM
// * To change this template use File | Settings | File Templates.
// */
//public class ReportingEngineStubDAO {
//
////    public static List<TransactionDTO> findTransactionDetails(Date reportDate) {
////
////        List<TransactionDTO> result = new ArrayList<TransactionDTO>();
////            for ( int idx = 1; idx <= 100; ++idx ) {
////                TransactionDTO item = new TransactionDTO();
////                item.setTransactionDate(new Date());
////                item.setSubscriberMsisdn("subscriber " + idx);
////                item.setAmount(new BigDecimal("" + idx));
////                result.add(item);
////            }
////            return result;
////    }
//
//    /*
//    select 	date_created,
//	(select count(*) from simregister s2 where state='registered' and s2.date_created = s.date_created) as registered,
//	(select count(*) from simregister s2 where state<>'registered' and s2.date_created = s.date_created) as pending,
//	(select count(*) from txns t where txn_type='simreg' and status_code <> '000' and t.txn_date = s.date_created) as rejects
// from simregister
// s group by date_created;
//     */
//}
