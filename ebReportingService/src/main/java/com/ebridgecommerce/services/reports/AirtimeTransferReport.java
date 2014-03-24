package com.ebridgecommerce.services.reports;

import com.ebridgecommerce.dao.VasGatewayDao;
import com.ebridgecommerce.domain.TransactionDTO;
import com.ebridgecommerce.dto.DailyTransactionStatsDTO;
import com.ebridgecommerce.dto.Txn;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/12/12
 * Time: 12:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class AirtimeTransferReport {

    public static String createReport() {

        Workbook workbook = null;


        FileInputStream in = null;
//        HSSFWorkbook lWorkBook = null;
//        HSSFSheet lWorkSheet = null;
        FileOutputStream out = null;
        POIFSFileSystem lPOIfs = null;

        try {
            Date reportDate = new DateTime().minusDays(1).toDate();
            String filename = "/prod/ebridge/spool/airtime_transfer_stats_" + new SimpleDateFormat("dd_MM_yyyy").format(reportDate) + ".xlsx";
            String template = "/prod/ebridge/templates/airtime_transfer_template.xlsx";
            new File(filename).delete();
            out = new FileOutputStream(filename, true);

            in = new FileInputStream(template);
//            lPOIfs = new POIFSFileSystem(lFin);

//            lWorkBook = new HSSFWorkbook(lPOIfs);
            workbook = new XSSFWorkbook(in);
            Sheet sheet = workbook.getSheet("AirtimeTransferStats");
            NumberFormat numberFormat = new DecimalFormat("###,##0.00");

            sheet.getRow(0).getCell(7).setCellValue(new SimpleDateFormat("MMMMM yyyy").format(reportDate));

            DateTime yesterday = new DateTime().minusDays(1);
            DateTime fromDate = yesterday.withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
            DateTime toDate = yesterday.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);

            Map<String, DailyTransactionStatsDTO> txns = VasGatewayDao.collateTxns();

            System.out.println("#### txns.length : " + txns.size());
            System.out.println("#### txns.keys : " + txns.keySet());

            int idx = 1;
            int subscribersToDate = 0;
            for ( DateTime date = fromDate; date.isBefore(toDate); date = date.plusDays(1) ) {
                try {
                    sheet.getRow(1).getCell( idx ).setCellValue(new SimpleDateFormat("dd-MMM").format(date.toDate()));
                } catch(Exception e) {
                }

                String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date.toDate());
                System.out.println("#### key : " + strDate );
                DailyTransactionStatsDTO stats = txns.get( strDate );
                System.out.println("#### stats : " + stats);
                System.out.println("#### stats : " + (stats != null ? stats.getDaySuccessfulTxns() : "0" ));
                if ( stats != null ) {

                    System.out.println("#### printing txns : " + stats.getDaySuccessfulTxns());
                    try {
                        sheet.getRow(3).getCell( idx ).setCellValue( stats.getDaySuccessfulTxns());
                        System.out.println("#### printed column : " + idx + " --- " + stats.getDaySuccessfulTxns());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        sheet.getRow(4).getCell( idx ).setCellValue(numberFormat.format(stats.getDayTxnRevenue()));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        sheet.getRow(5).getCell( idx ).setCellValue(stats.getDaySubscribers());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    subscribersToDate += stats.getDaySubscribers();

                    try {
                        sheet.getRow(6).getCell( idx ).setCellValue(subscribersToDate );
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        sheet.getRow(7).getCell( idx ).setCellValue(numberFormat.format(stats.getDayTxnCharges()));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        sheet.getRow(8).getCell( idx ).setCellValue(stats.getDayFailedTxns());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                ++idx;
            }

//
//            DailyTransactionStatsDTO stats = null;
//
//            for ( Txn txn : VasGatewayDao.getTxns()) {
//                try {
//                    sheet.getRow(2).getCell( idx ).setCellValue(new SimpleDateFormat("dd-MMM-yy").format(txn.getTxnDate()));
//                } catch(Exception e) {
//                }
//
//                try {
//                    sheet.getRow(4).getCell( idx ).setCellValue(txn.getTxnCount());
//                } catch(Exception e) {
//                }
//
//                try {
//                    sheet.getRow(5).getCell( idx ).setCellValue(txn.getTxnValue().doubleValue());
//                } catch(Exception e) {
//                }
//
////                try {
////                    sheet.getRow(6).getCell( idx ).setCellValue(txn.g);
////                } catch(Exception e) {
////                }
//
//                ++idx;
//            }
            System.out.println("Read " + idx + " transactions " );
            workbook.write(out);
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { out.close(); } catch (Exception e1){};
        }
        return "";
    }
    public static void main(String[] args) {
        createReport();
    }
}
