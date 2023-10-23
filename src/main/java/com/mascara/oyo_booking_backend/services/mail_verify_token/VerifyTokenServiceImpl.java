package com.mascara.oyo_booking_backend.services.mail_verify_token;

import com.mascara.oyo_booking_backend.dtos.response.MessageResponse;
import com.mascara.oyo_booking_backend.entities.MailConfirmToken;
import com.mascara.oyo_booking_backend.entities.User;
import com.mascara.oyo_booking_backend.enums.UserStatusEnum;
import com.mascara.oyo_booking_backend.exceptions.ResourceNotFoundException;
import com.mascara.oyo_booking_backend.mail.EmailDetails;
import com.mascara.oyo_booking_backend.mail.service.EmailService;
import com.mascara.oyo_booking_backend.repositories.MailConfirmTokenRepository;
import com.mascara.oyo_booking_backend.repositories.UserRepository;
import com.mascara.oyo_booking_backend.utils.AppContants;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Created by: IntelliJ IDEA
 * User      : boyng
 * Date      : 19/10/2023
 * Time      : 3:02 SA
 * Filename  : VerifyTokenServiceImpl
 */
@Service
public class VerifyTokenServiceImpl implements VerifyTokenService {

    @Value("${token.expired}")
    private Integer EXPIRATION_TIME;

    @Autowired
    private MailConfirmTokenRepository mailConfirmTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public MailConfirmToken generateTokenConfirmMail(String token, User user) {
        if(mailConfirmTokenRepository.existsByUserId(user.getId())) {
            mailConfirmTokenRepository.updateVerifyToken(user.getId(), token);
            return null;
        }
        MailConfirmToken mailConfirmToken = new MailConfirmToken(token, user, EXPIRATION_TIME);
        return mailConfirmTokenRepository.save(mailConfirmToken);
    }

    Logger logger = LoggerFactory.getLogger(VerifyTokenServiceImpl.class);

    @Override
    public MessageResponse verifyMailUser(String mail, String token) throws MessagingException, TemplateException, IOException {
        MailConfirmToken mailConfirmToken = mailConfirmTokenRepository.findByVerifyToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(AppContants.CONFIRM_TOKEN_NOT_FOUND));
        User user = userRepository.findByMailConfirmTokenId(1L)
                .orElseThrow(() -> new ResourceNotFoundException(AppContants.USER_NOT_FOUND));
        if (!mailConfirmToken.getDateExpired().isBefore(LocalDateTime.now())) {
            logger.info(LocalDateTime.now().toString());

            logger.error(mail);
            logger.error(user.getMail());
            if (user.getMail().equals(mail)) {
                user.setStatus(UserStatusEnum.ACTIVE);
                userRepository.save(user);
                return new MessageResponse(AppContants.ACTIVE_USER_SUCCESS);
            }
            return new MessageResponse(AppContants.ACTIVE_USER_MAIL_INVALID);
        }
        sendMailVerifyToken(user);
        return new MessageResponse(AppContants.ACTIVE_USER_TOKEN_EXPIRED);
    }

    @Override
    public void sendMailVerifyToken(User user) throws MessagingException, TemplateException, IOException {
        String codeConfirm = getRandomNumberString();
        generateTokenConfirmMail(codeConfirm, user);
        String objectSend = "email=" + user.getMail() + "&token=" + codeConfirm;
        String baseURL = "http://localhost:8080/api/v1/auth/verify?";
        String message = baseURL + objectSend;
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getMail())
                .subject("Xác nhận đăng kí")
                .msgBody(message).build();
        emailService.sendMailWithTemplate(emailDetails);
    }

    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}
