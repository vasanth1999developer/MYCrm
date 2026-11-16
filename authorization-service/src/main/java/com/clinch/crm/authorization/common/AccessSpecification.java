package com.clinch.crm.authorization.common;

import com.clinch.crm.authorization.entity.AccessVo;
import com.clinch.crm.authorization.model.AccessBo;
import org.springframework.data.jpa.domain.Specification;

/**
 * Reusable query filters ("specifications") for {@link AccessVo}.
 *
 * <p>A JPA {@link Specification} is one {@code WHERE} condition built in Java. Conditions can be
 * combined with {@code and}/{@code or}, so a search screen can add filters only when the user
 * fills them in, without writing one repository method per combination. Used by
 * {@code AccessServiceImpl.getAccess}.
 *
 * <p>In each lambda, {@code root} is the entity being queried, {@code query} the whole query and
 * {@code cb} the {@code CriteriaBuilder} that creates conditions.
 */
public class AccessSpecification {

    /**
     * Case-insensitive "name contains" filter:
     * {@code LOWER(access_name) LIKE '%text%'}.
     *
     * @param searchText the text to look for; {@code null} or empty means no filter
     * @return the condition, which yields {@code null} (ignored by Spring) when there's no text
     */
    public static Specification<AccessVo> searchText(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.isEmpty()) return null;
            return cb.like(
                    cb.lower(root.get("accessName")),  // ✅ correct field
                    "%" + searchText.toLowerCase() + "%"
            );
        };
    }

    // always exclude soft deleted records
    /**
     * Excludes soft-deleted entries: {@code is_deleted = false}.
     *
     * @return the condition
     */
    public static Specification<AccessVo> notDeleted() {
        return (root, query, cb) ->
                cb.equal(root.get("isDeleted"), false);  // ✅ correct field
    }
}

