package com.mascara.oyo_booking_backend.mail.service;

import com.mascara.oyo_booking_backend.dtos.response.MessageResponse;
import com.mascara.oyo_booking_backend.mail.EmailDetails;
import com.mascara.oyo_booking_backend.utils.AppContants;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: IntelliJ IDEA
 * User      : boyng
 * Date      : 18/10/2023
 * Time      : 8:41 CH
 * Filename  : EmailServiceImpl
 */
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private FreeMarkerConfigurer freemarkerConfigurer;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public MessageResponse sendSimpleMessage(EmailDetails emailDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(emailDetails.getRecipient());
        message.setSubject(emailDetails.getSubject());
        message.setText(emailDetails.getMsgBody());
        emailSender.send(message);
        return new MessageResponse(AppContants.SEND_MAIL_SUCCESS);
    }

    @Override
    public void sendMailWithTemplate(EmailDetails emailDetails) throws MessagingException, IOException, TemplateException {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("model", emailDetails);
        Template freemarkerTemplate = freemarkerConfigurer.getConfiguration()
                .getTemplate("Email_Active_Account.ftl");
        String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, templateModel);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(emailDetails.getRecipient());
        helper.setSubject(emailDetails.getSubject());
        helper.setFrom(sender);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }
}
