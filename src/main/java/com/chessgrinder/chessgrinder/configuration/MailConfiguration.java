package com.chessgrinder.chessgrinder.configuration;

import jakarta.annotation.Nonnull;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.InputStream;

@Configuration
public class MailConfiguration {

    @Primary
    @ConditionalOnProperty(name = "chessgrinder.mail.enabled", havingValue = "false", matchIfMissing = true)
    @Bean
    public static JavaMailSender mailSender() {
        return new JavaMailSender() {
            @Nonnull
            @Override
            public MimeMessage createMimeMessage() {
                return new MimeMessage((Session) null);
            }

            @Nonnull
            @Override
            public MimeMessage createMimeMessage(@Nonnull InputStream contentStream) throws MailException {
                return new MimeMessage((Session) null);
            }

            @Override
            public void send(@Nonnull MimeMessage... mimeMessages) throws MailException {
                for (MimeMessage simpleMessage : mimeMessages) {
                    LoggerFactory.getLogger(JavaMailSender.class).info("Mail sent: " + simpleMessage);
                }
            }

            @Override
            public void send(@Nonnull SimpleMailMessage... simpleMessages) throws MailException {
                for (SimpleMailMessage simpleMessage : simpleMessages) {
                    LoggerFactory.getLogger(JavaMailSender.class).info("Mail sent: " + simpleMessage);
                }
            }
        };
    }

}
