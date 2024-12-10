package com.tunduh.timemanagement.utils.specification;

import com.tunduh.timemanagement.entity.TaskEntity;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {
    private static final Logger logger = LoggerFactory.getLogger(TaskSpecification.class);

    public static Specification<TaskEntity> getSpecification(String title, String status, String userId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            logger.debug("Building specification - Title: {}, Status: {}, UserId: {}", title, status, userId);

            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
                logger.debug("Added title predicate: {}", title);
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
                logger.debug("Added status predicate: {}", status);
            }

            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            logger.debug("Added userId predicate: {}", userId);

            Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
            logger.debug("Final predicate count: {}", predicateArray.length);

            return criteriaBuilder.and(predicateArray);
        };
    }
}