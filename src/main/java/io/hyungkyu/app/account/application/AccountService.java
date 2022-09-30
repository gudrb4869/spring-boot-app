package io.hyungkyu.app.account.application;

import io.hyungkyu.app.account.domain.UserAccount;
import io.hyungkyu.app.account.domain.entity.Account;
import io.hyungkyu.app.account.endpoint.controller.SignUpForm;
import io.hyungkyu.app.account.infra.repository.AccountRepository;
import io.hyungkyu.app.settings.controller.NotificationForm;
import io.hyungkyu.app.settings.controller.Profile;
import io.hyungkyu.app.tag.domain.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public Account signUp(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        sendVerificationEmail(newAccount);
        return newAccount;
    }

    public Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.with(signUpForm.getEmail(), signUpForm.getNickname(), passwordEncoder.encode(signUpForm.getPassword()));
        // static 생성자를 이용해 객체를 생성함. builder를 사용할 경우 클래스 내에서 설정한 기본 값이 동작하지 않으므로 객체를 생성하는 방식으로 수정하였음.
        account.generateToken(); // 이메일 인증용 토큰 생성
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

    public Account findAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserAccount(account),
                account.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token); // AuthenticationManager를 쓰는 방법이 정석적인 방법
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = Optional.ofNullable(accountRepository.findByEmail(username))
                .orElse(accountRepository.findByNickname(username));
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserAccount(account);
    }

    public void verify(Account account) {
        account.verified();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        account.updateProfile(profile);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.updatePassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotification(Account account, NotificationForm notificationForm) {
        account.updateNotification(notificationForm);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, String nickname) {
        account.updateNickname(nickname);
        accountRepository.save(account);
        login(account); // 중요! 로그인을 다시 호출해서 인증정보를 갱신하여 내비게이션 바에 변경된 닉네임을 표시하도록 해줌.
    }

    public void sendLoginLink(Account account) {
        account.generateToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("[Webluxible] 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailToken() + "&email=" + account.getEmail());
        mailSender.send(mailMessage);
    }

    public Set<Tag> getTags(Account account) {
        return accountRepository.findById(account.getId()).orElseThrow().getTags();
    }

    public void addTag(Account account, Tag tag) {
        accountRepository.findById(account.getId())
                .ifPresent(a -> a.getTags().add(tag));
    }

    public void removeTag(Account account, Tag tag) {
        accountRepository.findById(account.getId())
                .map(Account::getTags)
                .ifPresent(tags -> tags.remove(tag));
    }
}