package io.hyungkyu.app.zone.infra.repository;

import io.hyungkyu.app.account.domain.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
}
