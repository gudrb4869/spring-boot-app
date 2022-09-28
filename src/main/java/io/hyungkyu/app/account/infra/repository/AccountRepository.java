package io.hyungkyu.app.account.infra.repository;

import io.hyungkyu.app.account.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true) // 성능에 이점을 가져오기 위해 readOnly 옵션을 true로 지정함.
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
