package com.ebridgecommerce.services.reports;

import com.ebridgecommerce.dao.ReportingEngineDAO;
//import com.ebridgecommerce.dao.ReportingEngineStubDAO;
import com.ebridgecommerce.dao.VasGatewayDao;
import com.ebridgecommerce.domain.TransactionDTO;
import com.ebridgecommerce.dto.SimRegistrationStatsDTO;
//import com.ebridgecommerce.sdp.dto.SimRegistrationDTO;
import com.ebridgecommerce.util.XSSFStyleFactory;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
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
public class SimRegistrationsReport {

    private Workbook workbook = null;
    private Sheet summarySheet = null;
    private Sheet registeredSheet = null;
    private Sheet pendingSheet = null;
    private Sheet rejectedSheet = null;

    private FileInputStream in = null;
    private FileOutputStream out = null;
    private Map<String, CellStyle> styles;
    private String filename;
    private Date reportDate;

    public SimRegistrationsReport(Date reportDate) {
        try {
            this.reportDate = reportDate;
            filename = "/ebridge/production/vas/spool/sim_registration_stats_for_" + new SimpleDateFormat("dd_MM_yyyy").format(reportDate) + ".xlsx";
            String template = "/ebridge/production/vas/templates/sim_registration_stats_template.xlsx";
            new File(filename).delete();
            out = new FileOutputStream(filename, true);
            in = new FileInputStream(template);
            workbook = new XSSFWorkbook(in);
            summarySheet = workbook.getSheet("Summary");
            registeredSheet = workbook.getSheet("Registered");
            pendingSheet = workbook.getSheet("Pending");
            rejectedSheet = workbook.getSheet("Rejections");
            styles = XSSFStyleFactory.createStyles(workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String createReport() {

        try {
            long startAt = System.currentTimeMillis();
            createStats();
            System.out.println("Starts created in " + ((System.currentTimeMillis() - startAt)/1000) + " second");
//            createDetailedListing("registered", registeredSheet);
//            createDetailedListing("!registered", pendingSheet);
//            createErrorListing(rejectedSheet);
            workbook.write(out);
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { out.close(); } catch (Exception e1){};
        }
        return filename;
    }

    private void createStats() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);

        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setAlignment(CellStyle.ALIGN_CENTER);

        NumberFormat numberFormat = DecimalFormat.getNumberInstance();
        Integer total = new Integer(0);
        Integer totalPending = new Integer(0);
        Integer totalErrors = new Integer(0);
        int idx = 8;
        for ( SimRegistrationStatsDTO item : ReportingEngineDAO.findSimRegStats(reportDate) ) {
            Row row = summarySheet.createRow(idx);

            Cell dateCell = row.createCell(1);
            dateCell.setCellStyle(styles.get("date"));
            dateCell.setCellValue(new SimpleDateFormat("dd MMM").format(item.getTxnDate()));

            Cell rejectedCell = row.createCell(2);
            rejectedCell.setCellStyle(styles.get("detail"));
            rejectedCell.setCellValue(numberFormat.format(item.getRejected()));

            Cell pendingCell = row.createCell(3);
            pendingCell.setCellStyle(styles.get("detail"));
            pendingCell.setCellValue(numberFormat.format(item.getPending()));

            Cell registeredCell = row.createCell(4);
            registeredCell.setCellStyle(styles.get("detail"));
            registeredCell.setCellValue(numberFormat.format(item.getRegistered()));

            total += item.getRegistered();
            totalPending += item.getPending();
            totalErrors += item.getRejected();
            ++idx;
        }
        Row row = summarySheet.createRow(idx);
        Cell totalCell = row.createCell( 1 );
        totalCell.setCellStyle(styles.get("totals"));
        totalCell.setCellValue("Total To Date");
//        fill(row, 2);

        totalCell = row.createCell(2);
        totalCell.setCellStyle(styles.get("totals"));
        totalCell.setCellValue(numberFormat.format(totalErrors));

        totalCell = row.createCell(3);
        totalCell.setCellStyle(styles.get("totals"));
        totalCell.setCellValue(numberFormat.format(totalPending));

        totalCell = row.createCell(4);
        totalCell.setCellStyle(styles.get("totals"));
        totalCell.setCellValue(numberFormat.format(total));
    }

//    private void createDetailedListing(String state, Sheet sheet) {
//        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
//
//        CellStyle dateCellStyle = workbook.createCellStyle();
//        dateCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
//
//        NumberFormat numberFormat = DecimalFormat.getNumberInstance();
//        Integer total = new Integer(0);
//        int idx = 8;
//        for ( SimRegistrationDTO item : ReportingEngineDAO.findSimRegistrationDetails(state, reportDate) ) {
//            Row row = sheet.createRow(idx);
//
//            Cell dateCell = row.createCell(1);
//            dateCell.setCellStyle(styles.get("date"));
//            dateCell.setCellValue(new SimpleDateFormat("dd MMM hh:mm:ss").format(item.getDateCreated()));
//
//            Cell msisdnCell = row.createCell(2);
//            msisdnCell.setCellStyle(styles.get("listing"));
//            msisdnCell.setCellValue(item.getMsIsdn());
//
//            Cell firstnameCell = row.createCell(3);
//            firstnameCell.setCellStyle(styles.get("listing"));
//            firstnameCell.setCellValue(!"null".equals(item.getFirstname()) ? item.getFirstname() : "");
//
//            Cell surnameCell = row.createCell(4);
//            surnameCell.setCellStyle(styles.get("listing"));
//            surnameCell.setCellValue(!"null".equals(item.getLastname()) ? item.getLastname() : "");
//
//            Cell idNumberCell = row.createCell(5);
//            idNumberCell.setCellStyle(styles.get("listing"));
//            idNumberCell.setCellValue(!"null".equals(item.getIdNumber()) ? item.getIdNumber() : "");
//
//            Cell physicalAddressCell = row.createCell(6);
//            physicalAddressCell.setCellStyle(styles.get("listing"));
//            physicalAddressCell.setCellValue(!"null".equals(item.getPhysicalAddress()) ? item.getPhysicalAddress() : "");
//            ++idx;
//            ++total;
//        }
//        Row row = sheet.createRow(idx);
//        Cell totalCell = row.createCell( 1 );
//        totalCell.setCellStyle(styles.get("totals"));
//        totalCell.setCellValue("Total");
//        fill(row, 2, 3, 4);
//
//        totalCell = row.createCell(5);
//        totalCell.setCellStyle(styles.get("totals"));
//        totalCell.setCellValue(numberFormat.format(total));
//    }

//    private void createErrorListing(Sheet sheet) {
//        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
//
//        CellStyle dateCellStyle = workbook.createCellStyle();
//        dateCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
//
//        NumberFormat numberFormat = DecimalFormat.getNumberInstance();
//        Integer total = new Integer(0);
//        int idx = 8;
//        for ( SimRegistrationStatsDTO item : ReportingEngineDAO.findSimRegistrationErrorDetails(reportDate) ) {
//            Row row = sheet.createRow(idx);
//
//            Cell dateCell = row.createCell(1);
//            dateCell.setCellStyle(styles.get("date"));
//            dateCell.setCellValue(new SimpleDateFormat("dd MMM hh:mm:ss").format(item.getTxnDate()));
//
//            Cell msisdnCell = row.createCell(2);
//            msisdnCell.setCellStyle(styles.get("listing"));
//            msisdnCell.setCellValue(item.getMsisdn());
//
//            Cell firstnameCell = row.createCell(3);
//            firstnameCell.setCellStyle(styles.get("listing"));
//            firstnameCell.setCellValue(item.getStatusText());
//            ++idx;
//            ++total;
//        }
//        Row row = sheet.createRow(idx);
//        Cell totalCell = row.createCell( 1 );
//        totalCell.setCellStyle(styles.get("totals"));
//        totalCell.setCellValue("Total");
//        fill(row, 2);
//
//        totalCell = row.createCell(3);
//        totalCell.setCellStyle(styles.get("totals"));
//        totalCell.setCellValue(numberFormat.format(total));
//    }

    protected void fill(Row row, Integer ... cols ){
        for (Integer colNum : cols) {
            Cell totalCell = row.createCell( colNum );
            totalCell.setCellStyle(styles.get("totals"));
            totalCell.setCellValue("");
        }
    }

    public static void main(String[] args) {
        new SimRegistrationsReport(new DateTime().minusDays(1).toDate()).createReport();
    }
}
