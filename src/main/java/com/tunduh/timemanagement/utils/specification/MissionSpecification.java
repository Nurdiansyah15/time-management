package com.tunduh.timemanagement.utils.specification;

import com.tunduh.timemanagement.entity.MissionEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MissionSpecification {

    public static Specification<MissionEntity> getSpecification(String name, String progress, String status, String userId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (progress != null && !progress.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("progress")), "%" + progress.toLowerCase() + "%"));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), "%" + status.toLowerCase() + "%"));
            }

            if (userId != null && !userId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.join("users").get("id"), userId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}