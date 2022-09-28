package io.hyungkyu.app.account.endpoint.controller;

import io.hyungkyu.app.account.application.AccountService;
import io.hyungkyu.app.account.domain.entity.Account;
import io.hyungkyu.app.account.endpoint.controller.validator.SignUpFormValidator;
import io.hyungkyu.app.account.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
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
    private final AccountService accountService;
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
        accountService.signUp(signUpForm);
        return "redirect:/";
    }

    private final AccountRepository accountRepository;

    @GetMapping("/check-email-token")
    public String verifyEmail(String token, String email, Model model) {
        Account account = accountService.findAccountByEmail(email);
        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return "account/email-verification";
        }
        if (!token.equals(account.getEmailToken())) {
            model.addAttribute("error", "wrong.token");
            return "account/email-verification";
        }
        account.verified();
        model.addAttribute("numberOfUsers", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return "account/email-verification";
    }
}
