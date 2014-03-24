package com.ebridgecommerce.services.reports;

import com.ebridgecommerce.dao.VasGatewayDao;
import com.ebridgecommerce.services.mail.MailService;
import com.zw.ebridge.factory.ReportFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import zw.co.ebridge.shared.dto.StatsDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/9/12
 * Time: 8:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionSummaryReport {

    private  MailService mailService;

    private ReportFactory reportFactory;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");

    public TransactionSummaryReport(/* ApplicationContext context */) {
        System.out.println("Reporting Engine version 2.0");
        System.out.println("Released: 25 September 2013");
//        mailService = (MailService) context.getBean("mailService");
        reportFactory = new ReportFactory();
    }

    public void sendReport(String... attachmentPath){
//        mailService.sendMail(attachmentPath);
    }

    private String generateReport() throws Exception {
        Date startDateThisWeek = null;

        Date startDate = new DateTime()
                                .withDayOfMonth(01)
                                .withMonthOfYear(03)
                                .withYear(2014)
                                .withHourOfDay(0)
                                .withMinuteOfHour(0)
                                .withSecondOfMinute(0)
                                .toDate();
        Date endDate = new DateTime()
                            .minusDays(1)
                            .withHourOfDay(23)
                            .withMinuteOfHour(59)
                            .withSecondOfMinute(59)
                            .toDate();

        List<StatsDTO> statsList = VasGatewayDao.findTransactionSummary(startDate, endDate);
        Map<Date, List<StatsDTO>> detailedStats = VasGatewayDao.findTransactionDetails(startDate, endDate);
        Map<Date, List<StatsDTO>> rejects = VasGatewayDao.findDailyRejections(startDate, endDate);

        String template = "/prod/ebridge/templates/bundle_management_service.xlsx";
        try {
            XSSFWorkbook reportExport = reportFactory.createXls(statsList, detailedStats, rejects, template);
            String filename = "/prod/ebridge/spool/bundle_service_report_" + dateFormat.format(startDate) + "_to_" + dateFormat.format(endDate) + ".xlsx";
            reportExport.write(new FileOutputStream(new File(filename)));
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            mailService.sendAlertMail(e.getMessage());
        }
        return "";
    }

    public static void main(String[] args) {
//        if (args.length < 1) {
//            System.out.println("Usage MailServiceTest <configFile> txnType");
//            System.exit(1);
//        }
//        String txnType = args[1].trim();
//        System.out.println("*" + txnType + "*");
        TransactionSummaryReport report = new TransactionSummaryReport(/*new FileSystemXmlApplicationContext(args[0])*/);
        try {
            report.sendReport(report.generateReport()/*, TransactionDetailReport.createReport(txnType)*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
