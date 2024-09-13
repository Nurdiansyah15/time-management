package com.tunduh.timemanagement.utils.specification;

import com.tunduh.timemanagement.entity.MissionEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MissionSpecification {
    public static Specification<MissionEntity> getSpecification(String name, String progress, String status) {
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

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
