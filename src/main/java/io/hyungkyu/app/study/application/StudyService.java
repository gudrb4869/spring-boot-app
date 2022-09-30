package io.hyungkyu.app.study.application;

import io.hyungkyu.app.account.domain.entity.Account;
import io.hyungkyu.app.study.domain.entity.Study;
import io.hyungkyu.app.study.endpoint.StudyForm;
import io.hyungkyu.app.study.infra.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {

    private final StudyRepository studyRepository;

    public Study createNewStudy(StudyForm studyForm, Account account) {
        Study study = Study.from(studyForm);
        study.addManager(account);
        return studyRepository.save(study);
    }
}
