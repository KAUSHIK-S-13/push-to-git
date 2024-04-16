package com.d2d.util;


import com.d2d.response.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.mail.username}")
    private String fromMail;

    @Value("${app.mail.developerMail}")
    private String developerMail;


    public void sendMail(String exception,String jsonInputFromUser) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        String[] emailIds = new String[4];
        emailIds[0] = "sabarishwaran.manoharan@coherent.in";
        emailIds[1] = "kaushik.subramani@coherent.in";
        emailIds[2] = "sekhar.m@coherent.in";
        emailIds[3] = "Booshnaarthy.s@coherent.in";
        simpleMailMessage.setTo(emailIds);
        simpleMailMessage.setSubject(CommonConstants.MAIL_SUBJECT);
        StringBuilder mailBody = new StringBuilder();
        mailBody.append("Hello Team,\n\n").append(CommonConstants.MAIL_TEXT).append("\n\t").append(exception);
        mailBody.append("\n\n\nRegards,\nD2D Application Team \nThis is automated email, please don't respond ");
        simpleMailMessage.setText(mailBody.toString());

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(Objects.requireNonNull(simpleMailMessage.getFrom()));
            helper.setTo(Objects.requireNonNull(simpleMailMessage.getTo()));
            helper.setSubject(Objects.requireNonNull(simpleMailMessage.getSubject()));
            helper.setText(Objects.requireNonNull(simpleMailMessage.getText()));
            Path tempFile = Files.createTempFile("JsonInput",".json");
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile());
            fileOutputStream.write(jsonInputFromUser.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
            helper.addAttachment("JsonInput.json",tempFile.toFile());
            javaMailSender.send(message);
            Files.deleteIfExists(tempFile);
        } catch (MessagingException | IOException e) {
            throw new MailParseException(e);
        }
    }

}
