package com.docker.example.services;





import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.docker.example.dto.ShareResponse;
import com.docker.example.model.ShareItem;
import com.docker.example.repository.ShareItemRepository;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ShareService {

    private final ShareItemRepository repo;
    private final Path uploadDir;

    public ShareService(
            ShareItemRepository repo,
            @Value("${app.upload-dir:./uploads}") String dir) {
        this.repo = repo;
        this.uploadDir = Paths.get(dir).toAbsolutePath().normalize();
    }

    @PostConstruct
    void init() throws IOException {
        Files.createDirectories(uploadDir);
    }

    public ShareResponse createText(String text, int expiry) {
        if (text == null || text.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Text cannot be empty.");
        }

        if (text.length() > 100000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Text limit is 100,000 characters.");
        }

        ShareItem item = new ShareItem();
        item.setToken(newToken());
        item.setType("TEXT");
        item.setTextContent(text);

        setExpiry(item, expiry);
        return ShareResponse.from(repo.save(item));
    }

    public ShareResponse createFile(
            MultipartFile file, int expiry) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Choose a file.");
        }

        if (file.getSize() > 20L * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE, "Maximum file size is 20 MB.");
        }

        String originalName = Paths.get(
                file.getOriginalFilename() == null
                        ? "shared-file" : file.getOriginalFilename()
        ).getFileName().toString();

        String token = newToken();
        String storedName = token + ".bin";
        Path destination = uploadDir.resolve(storedName).normalize();

        Files.copy(file.getInputStream(), destination,
                StandardCopyOption.REPLACE_EXISTING);

        try {
            ShareItem item = new ShareItem();
            item.setToken(token);
            item.setType("FILE");
            item.setOriginalFileName(originalName);
            item.setStoredFileName(storedName);
            item.setContentType(file.getContentType() == null
                    ? "application/octet-stream" : file.getContentType());
            item.setFileSize(file.getSize());

            setExpiry(item, expiry);
            return ShareResponse.from(repo.save(item));
        } catch (RuntimeException ex) {
            Files.deleteIfExists(destination);
            throw ex;
        }
    }

    public ShareResponse get(String token) {
        return ShareResponse.from(active(token));
    }

    public ShareItem getFile(String token) {
        ShareItem item = active(token);

        if (!"FILE".equals(item.getType())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "This share is not a file.");
        }

        return item;
    }

    public Path filePath(ShareItem item) {
        Path path = uploadDir.resolve(item.getStoredFileName()).normalize();

        if (!path.startsWith(uploadDir) || !Files.exists(path)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "File is missing.");
        }

        return path;
    }

    public void delete(String token) {
        ShareItem item = repo.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Share not found."));
        deleteItem(item);
    }

    private ShareItem active(String token) {
        ShareItem item = repo.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Share not found."));

        if (!item.getExpiresAt().isAfter(Instant.now())) {
            deleteItem(item);
            throw new ResponseStatusException(
                    HttpStatus.GONE, "This share has expired.");
        }

        return item;
    }

    private void setExpiry(ShareItem item, int minutes) {
        if (minutes != 15 && minutes != 60 && minutes != 1440) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expiry must be 15, 60, or 1440 minutes.");
        }

        Instant now = Instant.now();
        item.setCreatedAt(now);
        item.setExpiresAt(now.plus(minutes, ChronoUnit.MINUTES));
    }

    @Scheduled(fixedDelay = 60000)
    public void cleanup() {
        List<ShareItem> expired =
                repo.findByExpiresAtBefore(Instant.now());

        expired.forEach(this::deleteItem);
    }

    private void deleteItem(ShareItem item) {
        if (item.getStoredFileName() != null) {
            try {
                Files.deleteIfExists(
                        uploadDir.resolve(item.getStoredFileName()).normalize());
            } catch (IOException ignored) {
                // Keep cleanup errors from stopping the scheduled task.
            }
        }

        repo.delete(item);
    }

    private String newToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
