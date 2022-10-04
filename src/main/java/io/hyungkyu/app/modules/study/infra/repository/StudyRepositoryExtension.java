package io.hyungkyu.app.modules.study.infra.repository;

import io.hyungkyu.app.modules.study.domain.entity.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {
    Page<Study> findByKeyword(String keyword, Pageable pageable);
}
