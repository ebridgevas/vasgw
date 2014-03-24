//package com.ebridgecommerce.services.mail;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.FileSystemXmlApplicationContext;
//
///**
// * Created with IntelliJ IDEA.
// * User: David
// * Date: 6/10/12
// * Time: 5:36 AM
// * To change this template use File | Settings | File Templates.
// */
//
//public class MailServiceTest {
//
//    public static void main(String[] args) {
//
//        if (args.length < 1) {
//            System.out.println("Usage MailServiceTest <configFile>");
//            System.exit(1);
//        }
//        ApplicationContext context = new FileSystemXmlApplicationContext(args[0]);
//
//        MailService mailService = (MailService) context.getBean("mailService");
//
//        mailService.sendMail("/home/david/prod/templates/bundle_management_service.xlsx");
//
//        mailService.sendAlertMail("Exception occurred");
//    }
//
//}
