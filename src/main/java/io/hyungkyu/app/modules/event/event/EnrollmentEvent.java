package io.hyungkyu.app.modules.event.event;

import io.hyungkyu.app.modules.event.domain.entity.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnrollmentEvent {
    private final Enrollment enrollment;
    private final String message;
}
