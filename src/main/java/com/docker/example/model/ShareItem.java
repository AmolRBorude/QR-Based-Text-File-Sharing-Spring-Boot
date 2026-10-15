package com.docker.example.model;



import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "share_items")
public class ShareItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "LONGTEXT")
    private String textContent;

    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long fileSize;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    public Long getId() { return id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String name) {
        this.originalFileName = name;
    }

    public String getStoredFileName() { return storedFileName; }
    public void setStoredFileName(String name) {
        this.storedFileName = name;
    }

    public String getContentType() { return contentType; }
    public void setContentType(String type) { this.contentType = type; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
