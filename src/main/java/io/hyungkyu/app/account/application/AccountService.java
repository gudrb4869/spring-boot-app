package io.hyungkyu.app.account.application;

import io.hyungkyu.app.account.domain.entity.Account;
import io.hyungkyu.app.account.endpoint.controller.SignUpForm;
import io.hyungkyu.app.account.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender;

    public void signUp(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateToken(); // 이메일 인증용 토큰 생성
        sendVerificationEmail(newAccount);
    }

    public Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(signUpForm.getPassword())
                .notificationSetting(Account.NotificationSetting.builder() // 알림설정 중 웹 알림은 true로 설정해줌
                        .studyCreatedByWeb(true)
                        .studyUpdatedByWeb(true)
                        .studyRegistrationResultByEmailByWeb(true)
                        .build())
                .build();
        return accountRepository.save(account);
    }

    public void sendVerificationEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage(); // 이메일 객체를 생성하고 이메일의 내용을 채움
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("Webluxible 회원 가입 인증");
        mailMessage.setText(String.format("/check-email-token?token=%s&email=%s", newAccount.getEmailToken(),
                newAccount.getEmail())); // 이메일 본문에 추가할 링크를 작성함. 나중에 사용자가 링크를 클릭했을 때 다시 서버로 요청해야하고 이 부분에 대한 구현이 되어있어야 이메일 인증을 마칠 수 있음
        mailSender.send(mailMessage); // 메일을 보냄
    }
}
