package io.hyungkyu.app.modules.study.infra.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import io.hyungkyu.app.modules.account.domain.entity.QAccount;
import io.hyungkyu.app.modules.study.domain.entity.QStudy;
import io.hyungkyu.app.modules.study.domain.entity.Study;
import io.hyungkyu.app.modules.tag.domain.entity.QTag;
import io.hyungkyu.app.modules.tag.domain.entity.Tag;
import io.hyungkyu.app.modules.zone.domain.entity.QZone;
import io.hyungkyu.app.modules.zone.domain.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {

    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study)
                .where(study.published.isTrue()
                        .and(study.title.containsIgnoreCase(keyword))
                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin() // zones join 및 fetchJoin
                .leftJoin(study.members, QAccount.account).fetchJoin()
                .distinct(); // members join 및 fetchJoin
        JPQLQuery<Study> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Study> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
        /**
         * getQuerydsl을 이용해 QuerydslRepositorySupport 가 제공하는 기능을 사용할 수 있는데 페이징을 적용하기 위해 applyPagination을 호출함.
         * fetchResults 를 이용해 조회한 결과를 얻을  수 있음.
         * 반환해야 할 타입이 Page 이므로 구현체인 PageImpl 을 이용해 반환함.
         * 결과 데이터, pageable, 전체 데이터 수를 생성자로 전달해주어야 함.
         * 마지막으로 MainController 에서 view 로 전달해주는 이름이 바뀌었기 때문에 search.html 파일도 수정해주어야 함.
         */
    }

    @Override
    public List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                        .and(study.closed.isFalse())
                        .and(study.tags.any().in(tags))
                        .and(study.zones.any().in(zones)))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .orderBy(study.publishedDateTime.desc())
                .distinct()
                .limit(9);
        return query.fetch();
    }

}
