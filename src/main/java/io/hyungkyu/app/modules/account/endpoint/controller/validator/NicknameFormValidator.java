package io.hyungkyu.app.modules.account.endpoint.controller.validator;

import io.hyungkyu.app.modules.account.domain.entity.Account;
import io.hyungkyu.app.modules.account.endpoint.controller.form.NicknameForm;
import io.hyungkyu.app.modules.account.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component // AccountRepository를 주입받기 위해 컴포넌트로 등록했음.
// Validator를 Component로 등록한 김에, PasswordFormValidator 또한 @Component 어노테이션을 추가해줬음.
@RequiredArgsConstructor
public class NicknameFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        Account account = accountRepository.findByNickname(nicknameForm.getNickname());
        if (account != null) {
            errors.rejectValue("nickname", "wrong.value", "이미 사용중인 닉네임입니다.");
        }
    }
}