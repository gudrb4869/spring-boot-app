package io.hyungkyu.app.modules.event.event;

import io.hyungkyu.app.modules.event.domain.entity.Enrollment;

public class EnrollmentRejectedEvent extends EnrollmentEvent {
    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청이 거절되었습니다.");
    }
}
