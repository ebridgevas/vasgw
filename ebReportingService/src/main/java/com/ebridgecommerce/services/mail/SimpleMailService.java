package com.ebridgecommerce.services.mail;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/11/12
 * Time: 11:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleMailService {
    public static void main(String[] args) {

        try {
            Email email = new SimpleEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setAuthenticator(new DefaultAuthenticator("david@ebridge-zw.com", "london14"));
            email.setFrom("david@ebridge-zw.com");
            email.setSubject("TestMail");
            email.setMsg("This is a test mail ... :-)");
            email.addTo("david@ebridge-zw.com");
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }

    }
}
