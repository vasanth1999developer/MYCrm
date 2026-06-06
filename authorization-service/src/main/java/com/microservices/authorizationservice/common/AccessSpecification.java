package com.microservices.authorizationservice.common;

import com.microservices.authorizationservice.entity.AccessVo;
import com.microservices.authorizationservice.model.AccessBo;
import org.springframework.data.jpa.domain.Specification;

public class AccessSpecification {

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
    public static Specification<AccessVo> notDeleted() {
        return (root, query, cb) ->
                cb.equal(root.get("isDeleted"), false);  // ✅ correct field
    }
}

