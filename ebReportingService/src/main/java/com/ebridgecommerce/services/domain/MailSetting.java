package com.ebridgecommerce.services.domain;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/10/12
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class MailSetting {

    public MailSetting() {
    }

    private String mailFrom;
    private String mailSubject;
    private String mailBody;

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }
}
