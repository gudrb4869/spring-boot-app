package io.hyungkyu.app.modules.notification.infra.repository;

import io.hyungkyu.app.modules.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
