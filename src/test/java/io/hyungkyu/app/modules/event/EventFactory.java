package io.hyungkyu.app.modules.event;

import io.hyungkyu.app.modules.account.domain.entity.Account;
import io.hyungkyu.app.modules.event.application.EventService;
import io.hyungkyu.app.modules.event.domain.entity.Event;
import io.hyungkyu.app.modules.event.domain.entity.EventType;
import io.hyungkyu.app.modules.event.endpoint.form.EventForm;
import io.hyungkyu.app.modules.event.infra.repository.EventRepository;
import io.hyungkyu.app.modules.study.domain.entity.Study;
import io.hyungkyu.app.modules.study.infra.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventFactory {
    @Autowired EventRepository eventRepository;
    @Autowired StudyRepository studyRepository;
    @Autowired EventService eventService;

    public Event createEvent(EventType eventType, Account account, String studyPath) {
        Study study = studyRepository.findByPath(studyPath);
        LocalDateTime now = LocalDateTime.now();
        EventForm eventForm = EventForm.builder()
                .description("description")
                .eventType(eventType)
                .endDateTime(now.plusWeeks(3))
                .endEnrollmentDateTime(now.plusWeeks(1))
                .limitOfEnrollments(2)
                .startDateTime(now.plusWeeks(2))
                .title("title")
                .build();
        return eventService.createEvent(study, eventForm, account);
    }
}
