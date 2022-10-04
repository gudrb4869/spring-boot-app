package io.hyungkyu.app.modules.notification.infra.repository;

import io.hyungkyu.app.modules.account.domain.entity.Account;
import io.hyungkyu.app.modules.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByAccountAndChecked(Account account, boolean checked);
}
