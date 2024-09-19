package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.SubmissionRequest;
import com.tunduh.timemanagement.dto.response.AnalyticsResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.SubmissionResponse;
import com.tunduh.timemanagement.dto.response.UserResponse;
import com.tunduh.timemanagement.entity.SubmissionEntity;
import com.tunduh.timemanagement.entity.ShopItemEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.ShopItemRepository;
import com.tunduh.timemanagement.repository.SubmissionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.AdminService;
import com.tunduh.timemanagement.service.CloudinaryService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.specification.ShopItemSpecification;
import com.tunduh.timemanagement.utils.specification.SubmissionSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ShopItemRepository shopItemRepository;
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
                .orElseThrow(() -> new EntityNotFoundException("Submission item with id " + id + " not found"));
        String url = cloudinaryService.uploadFile(file, "submission");
        submissionItem.setIcon(url);
        SubmissionEntity savedSubmission = submissionRepository.save(submissionItem);
        return mapToSubmissionResponse(savedSubmission);
    }

    @Override
    public CustomPagination<SubmissionResponse> getAllSubmissions(int page, int size, String sort, String title, String status) {
        Pageable pageable = createPageable(page, size, sort);
        Specification<SubmissionEntity> spec = SubmissionSpecification.getSpecification(title, status);

        Page<SubmissionEntity> submissionPage = submissionRepository.findAll(spec, pageable);
        List<SubmissionResponse> submissionResponses = submissionPage.getContent().stream()
                .map(this::mapToSubmissionResponse)
                .toList();

        return new CustomPagination<>(submissionPage.map(this::mapToSubmissionResponse));
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

    @Override
    public CustomPagination<ShopItemResponse> getAllShopItems(int page, int size, String sort, String name, Integer maxPrice) {
        Pageable pageable = createPageable(page, size, sort);
        Specification<ShopItemEntity> spec = ShopItemSpecification.getSpecification(name, maxPrice);

        Page<ShopItemEntity> shopItemPage = shopItemRepository.findAll(spec, pageable);
        return new CustomPagination<>(shopItemPage.map(this::mapToShopItemResponse));
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


    private ShopItemResponse mapToShopItemResponse(ShopItemEntity shopItem) {
        return ShopItemResponse.builder()
                .id(shopItem.getId())
                .name(shopItem.getName())
                .itemPicture(shopItem.getItemPicture())
                .price(shopItem.getPrice())
                .createdAt(shopItem.getCreatedAt())
                .updatedAt(shopItem.getUpdatedAt())
                .build();
    }

    private Pageable createPageable(int page, int size, String sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            for (String param : sortParams) {
                String[] keyDirection = param.split(":");
                String key = keyDirection[0];
                Sort.Direction direction = keyDirection.length > 1 && keyDirection[1].equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, key));
            }
        }
        return PageRequest.of(page, size, Sort.by(orders));
    }
}