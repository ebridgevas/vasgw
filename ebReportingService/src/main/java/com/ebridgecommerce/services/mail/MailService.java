package com.ebridgecommerce.services.mail;

/**
 * MailService
 */
import com.ebridgecommerce.services.domain.MailSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Service("mailService")
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SimpleMailMessage alertMailMessage;

    @Autowired
    private MailSetting mailSetting;

    @Resource(name="mailingList")
    private List<String> mailingList;

    public void sendMail(String... attachmentFilenames) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailSetting.getMailFrom());
            helper.setTo(mailingList.toArray(new String[mailingList.size()]));
            helper.setSubject(mailSetting.getMailSubject());
            helper.setText(mailSetting.getMailBody());

            for (String attachmentFilename : attachmentFilenames) {
                FileSystemResource file = new FileSystemResource(new File(attachmentFilename));
                helper.addAttachment(attachmentFilename, file);
            }
        } catch (MessagingException e) {
            sendAlertMail(e.getMessage());
        }
        mailSender.send(message);
    }

    public void sendAlertMail(String alert) {

        SimpleMailMessage mailMessage = new SimpleMailMessage(alertMailMessage);
        mailMessage.setText(alert);
        mailSender.send(mailMessage);
    }

}

