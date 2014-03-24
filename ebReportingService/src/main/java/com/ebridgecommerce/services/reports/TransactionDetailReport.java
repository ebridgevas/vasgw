package com.ebridgecommerce.services.reports;

import com.ebridgecommerce.dao.VasGatewayDao;
import com.ebridgecommerce.domain.TransactionDTO;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/12/12
 * Time: 12:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionDetailReport {

    public static String createReport(String txnType) {

        Workbook workbook = null;


        FileInputStream in = null;
//        HSSFWorkbook lWorkBook = null;
//        HSSFSheet lWorkSheet = null;
        FileOutputStream out = null;
        POIFSFileSystem lPOIfs = null;

        try {
            Date reportDate = new DateTime().minusDays(1).toDate();
            String filename = "/prod/ebridge/spool/data_bundles_detailed_report_" + new SimpleDateFormat("dd_MM_yyyy").format(reportDate) + ".xlsx";
            String template = "/prod/ebridge/templates/bundle_management_service_detailed_report.xlsx";
            new File(filename).delete();
            out = new FileOutputStream(filename, true);

            in = new FileInputStream(template);
//            lPOIfs = new POIFSFileSystem(lFin);

//            lWorkBook = new HSSFWorkbook(lPOIfs);
            workbook = new XSSFWorkbook(in);
            Sheet sheet = workbook.getSheet("Transacted Subscribers");
            NumberFormat numberFormat = DecimalFormat.getCurrencyInstance();

            System.out.println("Getting transactions : *" + txnType + "*" );

            int idx = 8;
            for ( TransactionDTO transaction : VasGatewayDao.findTransactionDetails(reportDate, txnType) ) {
                Row row = sheet.createRow(idx);
                row.createCell(1).setCellValue(new SimpleDateFormat("dd MMM yy hh:mm:ss").format(transaction.getTransactionDate()));
                row.createCell(2).setCellValue(transaction.getSubscriberMsisdn());

                Cell amountCell = row.createCell(7);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
                amountCell.setCellStyle(cellStyle);
                amountCell.setCellValue(numberFormat.format(transaction.getAmount()));
                ++idx;
            }
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
        createReport(args[0]);
    }
}
