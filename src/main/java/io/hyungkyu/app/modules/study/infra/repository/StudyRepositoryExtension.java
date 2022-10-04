package io.hyungkyu.app.modules.study.infra.repository;

import io.hyungkyu.app.modules.study.domain.entity.Study;
import io.hyungkyu.app.modules.tag.domain.entity.Tag;
import io.hyungkyu.app.modules.zone.domain.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {
    Page<Study> findByKeyword(String keyword, Pageable pageable);

    List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones);
}
