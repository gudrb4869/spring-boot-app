package io.hyungkyu.app.modules.study.event;

import io.hyungkyu.app.modules.study.domain.entity.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyUpdateEvent {

    private final Study study;
    private final String message;
}
