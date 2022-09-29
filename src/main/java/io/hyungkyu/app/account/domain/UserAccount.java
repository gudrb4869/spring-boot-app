package io.hyungkyu.app.account.domain;

import io.hyungkyu.app.account.domain.entity.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class UserAccount extends User {

    @Getter
    private final Account account; // @CurrentUser 어노테이션에서 account를 반환하도록 하였기 때문에 변수 이름을 반드시 account로 설정해야함

    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        // User 객체를 생성하기 위해선 username, password, authorities가 필요한데 우리가 사용하는 객체인 Account에서 각각 추출해줌.
        this.account = account;
    }
}
