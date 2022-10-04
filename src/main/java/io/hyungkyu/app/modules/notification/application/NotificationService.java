package io.hyungkyu.app.modules.notification.application;

import io.hyungkyu.app.modules.notification.domain.entity.Notification;
import io.hyungkyu.app.modules.notification.infra.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void markAsRead(List<Notification> notifications) {
        notifications.forEach(Notification::read);
    }
}
