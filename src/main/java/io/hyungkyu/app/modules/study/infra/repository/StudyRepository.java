package io.hyungkyu.app.modules.study.infra.repository;

import io.hyungkyu.app.modules.study.domain.entity.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

    // Study.withAll EntityGraph를 findByPath 메소드를 사용할 때 적용한다는 뜻임.
    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(value = "Study.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithZonesByPath(String path);

    @EntityGraph(value = "Study.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(value = "Study.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithMembersByPath(String path);

    Optional<Study> findStudyOnlyByPath(String path);

    @EntityGraph(value = "Study.withTagsAndZones", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithTagsAndZonesById(Long id);

    /* find, ByColumnName 과 같은 정해진 문구는 SQL문을 생성할 때 영향을 주지만 그 사이에 있는 값은 메소드를 구분하는
    기능만 가지고 있을 뿐 쿼리에는 영향을 주지 않음.
    따라서 findByPath, findStudyWithTagsByPath, findStudyWithZonesByPath 이 세 가지 쿼리는 @EntityGraph 설정이
    없다면 동일한 쿼리(findByPath)를 나타냄.

    하지만 @EntityGraph 내에서 설정한 @NamedEntityGraph를 따르기 때문에 세 가지 쿼리는 달라지게 됨.
    @EntityGraph 어노테이션의 attribute인 type에 들어갈 수 있는 타입은 EntityGraph.EntityGraphType 타입으로 해당 타입은 두 가지의 값을 가짐.

     - EntityGraph.EntityGraphType.LOAD: Entity 그래프의 속성 노드에의해 지정된 속성은 FetchType.EAGER로 처리되고,
     그렇지 않은 속성은 지정되어있는 속성으로, 지정되어있지 않다면 기본 FetchType에 따라 처리
     - EntityGraph.EntityGraphType.FETCH: Entity 그래프의 속성 노드에의해 지정된 속성은 FetchType.EAGER로 처리되고,
     그렇지 않은 속성은 FetchType.LAZY로 처리

     FetchType을 지정한 경우 LOAD를 쓰고, 그렇지 않은 경우 FETCH를 쓰면 얼추 대다수 상황에 적용할 수 있음.
     */

}
