package io.hyungkyu.app.infra.mail;

public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
