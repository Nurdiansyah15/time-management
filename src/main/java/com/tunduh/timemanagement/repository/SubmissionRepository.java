package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.SubmissionEntity;
import com.tunduh.timemanagement.dto.response.SubmissionResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, String>, JpaSpecificationExecutor<SubmissionEntity> {
    @Query("SELECT new com.tunduh.timemanagement.dto.response.SubmissionResponse(s.id, s.name, s.description, s.point, s.criteriaCompleted, s.type, s.icon, s.createdAt, s.updatedAt) " +
            "FROM SubmissionEntity s " +
            "WHERE (:title IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:status IS NULL OR s.type = :status)")
    CustomPagination<SubmissionResponse> findAllSubmissions(
            @Param("page") int page,
            @Param("size") int size,
            @Param("sort") String sort,
            @Param("title") String title,
            @Param("status") String status);
}