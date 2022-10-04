package io.hyungkyu.app.modules.account.infra.predicates;

import com.querydsl.core.types.Predicate;
import io.hyungkyu.app.modules.account.domain.entity.QAccount;
import io.hyungkyu.app.modules.tag.domain.entity.Tag;
import io.hyungkyu.app.modules.zone.domain.entity.Zone;

import java.util.Set;

public class AccountPredicates {
    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
}
