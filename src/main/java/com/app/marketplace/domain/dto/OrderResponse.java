package com.app.marketplace.domain.dto;

import java.util.UUID;

public record OrderResponse(
        UUID publicId,
        String status
) {
}
