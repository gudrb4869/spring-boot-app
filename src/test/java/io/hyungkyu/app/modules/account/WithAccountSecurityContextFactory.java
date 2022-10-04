package io.hyungkyu.app.modules.account;

import io.hyungkyu.app.modules.account.application.AccountService;
import io.hyungkyu.app.modules.account.endpoint.controller.form.SignUpForm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    public WithAccountSecurityContextFactory(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {
        String[] nicknames = annotation.value();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        for (String nickname : nicknames) {
            SignUpForm signUpForm = new SignUpForm();
            signUpForm.setNickname(nickname);
            signUpForm.setEmail(nickname + "@gmail.com");
            signUpForm.setPassword("1234asdf");
            accountService.signUp(signUpForm);
            UserDetails principal = accountService.loadUserByUsername(nickname);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
            context.setAuthentication(authentication);
        }
        // 테스트 주체는 마지막에 등록되는 nickname임.
        return context;
    }
}
