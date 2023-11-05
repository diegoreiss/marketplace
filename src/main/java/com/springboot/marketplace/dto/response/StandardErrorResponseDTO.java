package com.springboot.marketplace.dto.response;

import java.time.Instant;
import java.util.List;

public record StandardErrorResponseDTO(
        Instant timestamp,
        Integer status,
        List<String> errors,
        String message,
        String path
) {
}
