package io.hyungkyu.app.event.application;

import io.hyungkyu.app.account.domain.entity.Account;
import io.hyungkyu.app.event.domain.entity.Event;
import io.hyungkyu.app.event.endpoint.form.EventForm;
import io.hyungkyu.app.event.infra.repository.EventRepository;
import io.hyungkyu.app.study.domain.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Study study, EventForm eventForm, Account account) {
        Event event = Event.from(eventForm, account, study);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        event.updateFrom(eventForm);
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
}