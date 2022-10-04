package io.hyungkyu.app.modules.study.event;

import io.hyungkyu.app.modules.study.domain.entity.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Transactional(readOnly = true)
@Component
public class StudyEventListener {
    @EventListener // @EventListener 어노테이션을 이용해 이벤트 리스너를 명시함.
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        // EventPublisher를 통해 이벤트가 발생될 때 전달한 파라미터가 StudyCreatedEvent일 때 해당 메서드가 호출됨.
        Study study = studyCreatedEvent.getStudy();
        log.info(study.getTitle() + " is created");
    }
}
