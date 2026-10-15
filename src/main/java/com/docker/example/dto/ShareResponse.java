package com.docker.example.dto;

import java.time.Instant;

import com.docker.example.model.ShareItem;



public record ShareResponse(
        String token,
        String type,
        String textContent,
        String originalFileName,
        Long fileSize,
        Instant createdAt,
        Instant expiresAt
) {
    public static ShareResponse from(ShareItem item) {
        return new ShareResponse(
                item.getToken(),
                item.getContentType(),
                item.getTextContent(),
                item.getOriginalFileName(),
                item.getFileSize(),
                item.getCreatedAt(),
                item.getExpiresAt()
        );
    }
}

