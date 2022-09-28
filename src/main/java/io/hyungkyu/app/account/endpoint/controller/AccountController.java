package io.hyungkyu.app.account.endpoint.controller;

import io.hyungkyu.app.account.domain.entity.Account;
import io.hyungkyu.app.account.endpoint.controller.validator.SignUpFormValidator;
import io.hyungkyu.app.account.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender; // 인증 메일을 보내기 위해 JavaMailSender 주입

    /* @InitBinder 어노테이션을 사용해 attribute로 바인딩 할 객체를 지정하고 WebDataBinder를 이용해
    Validator를 추가해주면 해당 객체가 들어왔을 때 검증하는 로직을 직접 추가할 필요가 없음.
     */
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
        /* 이 방식 대신 @InitBinder 사용해서 처리 가능.
        signUpFormValidator.validate(signUpForm, errors);
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
        */
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
        Account newAccount = accountRepository.save(account);

        newAccount.generateToken(); // 이메일 인증용 토큰 생성
        SimpleMailMessage mailMessage = new SimpleMailMessage(); // 이메일 객체를 생성하고 이메일의 내용을 채움
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("Webluxible 회원 가입 인증");
        mailMessage.setText(String.format("/check-email-token?token=%s&email=%s", newAccount.getEmailToken(),
                newAccount.getEmail())); // 이메일 본문에 추가할 링크를 작성함. 나중에 사용자가 링크를 클릭했을 때 다시 서버로 요청해야하고 이 부분에 대한 구현이 되어있어야 이메일 인증을 마칠 수 있음
        mailSender.send(mailMessage); // 메일을 보냄

        return "redirect:/";
    }
}
