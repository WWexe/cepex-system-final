package com.biopark.cepex_system.domain.approval;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApprovalItemDTO(
        UUID id,
        String title,
        String type, // "MONITORIA" ou "PROJETO"
        String creatorName,
        String departmentOrArea,
        LocalDateTime submittedAt,
        String currentStatus
) {}