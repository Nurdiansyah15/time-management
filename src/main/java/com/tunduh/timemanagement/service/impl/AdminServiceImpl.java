package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.SubmissionRequest;
import com.tunduh.timemanagement.dto.response.AnalyticsResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.SubmissionResponse;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.entity.SubmissionEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.SubmissionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.AdminService;
import com.tunduh.timemanagement.service.CloudinaryService;
import com.tunduh.timemanagement.service.ShopItemService;
import com.tunduh.timemanagement.service.PurchaseService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ShopItemService shopItemService;
    private final PurchaseService purchaseService;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public SubmissionResponse createSubmission(SubmissionRequest submissionRequest) {
        SubmissionEntity submission = SubmissionEntity.builder()
                .name(submissionRequest.getName())
                .description(submissionRequest.getDescription())
                .point(submissionRequest.getPoint())
                .criteriaCompleted(submissionRequest.getCriteriaCompleted())
                .type(SubmissionEntity.Type.valueOf(submissionRequest.getType()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        SubmissionEntity savedSubmission = submissionRepository.save(submission);
        return mapToSubmissionResponse(savedSubmission);
    }

    @Override
    @Transactional
    public SubmissionResponse updatePhoto(MultipartFile file, String id) {
        SubmissionEntity submissionItem = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission item with id " + id + " not found"));
        String url = cloudinaryService.uploadFile(file, "submission");
        submissionItem.setIcon(url);
        SubmissionEntity savedSubmission = submissionRepository.save(submissionItem);
        return mapToSubmissionResponse(savedSubmission);
    }

    @Override
    public CustomPagination<SubmissionResponse> getAllSubmissions(int page, int size, String sort, String title, String status) {
        return submissionRepository.findAllSubmissions(page, size, sort, title, status);
    }

    @Override
    public CustomPagination<ShopItemResponse> getAllShopItems(int page, int size, String sort, String name, Integer maxPrice) {
        return shopItemService.getAllShopItems(page, size, sort, name, maxPrice, null);
    }

    @Override
    public CustomPagination<PurchaseResponse> getAllPurchases(int page, int size, String sort) {
        return purchaseService.getAllPurchases(page, size, sort);
    }

    @Override
    public SubmissionResponse getSubmissionById(String id) {
        SubmissionEntity submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        return mapToSubmissionResponse(submission);
    }

    @Override
    @Transactional
    public SubmissionResponse updateSubmission(String id, SubmissionRequest submissionRequest) {
        SubmissionEntity submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        submission.setName(submissionRequest.getName());
        submission.setDescription(submissionRequest.getDescription());
        submission.setPoint(submissionRequest.getPoint());
        submission.setCriteriaCompleted(submissionRequest.getCriteriaCompleted());
        submission.setType(SubmissionEntity.Type.valueOf(submissionRequest.getType()));
        submission.setUpdatedAt(LocalDateTime.now());
        SubmissionEntity updatedSubmission = submissionRepository.save(submission);
        return mapToSubmissionResponse(updatedSubmission);
    }

    @Override
    @Transactional
    public void deleteSubmission(String id) {
        SubmissionEntity submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        submissionRepository.delete(submission);
    }

    @Override
    public AnalyticsResponse getUserAnalytics() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return AnalyticsResponse.builder()
                .totalUsers(userRepository.count())
                .activeUsers(userRepository.countActiveUsers(thirtyDaysAgo))
                .averageTasksPerUser(userRepository.getAverageTasksPerUser())
                .build();
    }

    private SubmissionResponse mapToSubmissionResponse(SubmissionEntity submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .name(submission.getName())
                .description(submission.getDescription())
                .point(submission.getPoint())
                .criteriaCompleted(submission.getCriteriaCompleted())
                .type(submission.getType().name())
                .icon(submission.getIcon())
                .createdAt(submission.getCreatedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }
}