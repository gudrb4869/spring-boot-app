package io.hyungkyu.app.modules.study.event;

import io.hyungkyu.app.infra.config.AppProperties;
import io.hyungkyu.app.infra.mail.EmailMessage;
import io.hyungkyu.app.infra.mail.EmailService;
import io.hyungkyu.app.modules.account.domain.entity.Account;
import io.hyungkyu.app.modules.account.infra.predicates.AccountPredicates;
import io.hyungkyu.app.modules.account.infra.repository.AccountRepository;
import io.hyungkyu.app.modules.notification.domain.entity.Notification;
import io.hyungkyu.app.modules.notification.domain.entity.NotificationType;
import io.hyungkyu.app.modules.notification.infra.repository.NotificationRepository;
import io.hyungkyu.app.modules.study.domain.entity.Study;
import io.hyungkyu.app.modules.study.infra.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Async
@Transactional
@Component
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    @EventListener // @EventListener 어노테이션을 이용해 이벤트 리스너를 명시함.
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        // EventPublisher를 통해 이벤트가 발생될 때 전달한 파라미터가 StudyCreatedEvent일 때 해당 메서드가 호출됨.
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId()); // 관심사와 지역 정보를 추가로 조회함.
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        for (Account account : accounts) {
            Account.NotificationSetting notificationSetting = account.getNotificationSetting();
            if (notificationSetting.isStudyCreatedByEmail()) {
                sendEmail(study, account);
            }
            if (notificationSetting.isStudyCreatedByWeb()) {
                saveNotification(study, account);
            }
        }
    }

    private void sendEmail(Study study, Account account) {
        Context context = new Context();
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", "새로운 스터디가 오픈하였습니다.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);
        emailService.sendEmail(EmailMessage.builder()
                .to(account.getEmail())
                .subject("[Webluxible] " + study.getTitle() + " 스터디가 오픈하였습니다.")
                .message(message)
                .build());
    }

    private void saveNotification(Study study, Account account) {
        notificationRepository.save(Notification.from(study.getTitle(), "/study/" + study.getEncodedPath(),
                false, LocalDateTime.now(), study.getShortDescription(), account, NotificationType.STUDY_CREATED));
    }
}
