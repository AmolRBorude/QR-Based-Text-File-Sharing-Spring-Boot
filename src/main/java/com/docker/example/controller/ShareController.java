package com.docker.example.controller;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.docker.example.dto.ShareResponse;
import com.docker.example.model.ShareItem;
import com.docker.example.services.ShareService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShareController {

    private final ShareService service;

    public ShareController(ShareService service) {
        this.service = service;
    }

    public record TextRequest(String text, int expiryMinutes) {}

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "application", "QuickShare");
    }

    @PostMapping("/shares/text")
    public ResponseEntity<ShareResponse> createText(
            @RequestBody TextRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createText(
                        request.text(), request.expiryMinutes()));
    }

    @PostMapping(value = "/shares/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ShareResponse> createFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "60") int expiryMinutes)
            throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createFile(file, expiryMinutes));
    }

    @GetMapping("/shares/{token}")
    public ShareResponse getShare(@PathVariable String token) {
        return service.get(token);
    }

    @GetMapping("/shares/{token}/download")
    public ResponseEntity<Resource> download(
            @PathVariable String token) throws IOException {

        ShareItem item = service.getFile(token);
        Path path = service.filePath(item);
        Resource resource = new UrlResource(path.toUri());

        String name = item.getOriginalFileName() == null
                ? "download" : item.getOriginalFileName();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        item.getContentType() == null
                                ? "application/octet-stream"
                                : item.getContentType()))
                .contentLength(item.getFileSize() == null
                        ? 0 : item.getFileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(name).build().toString())
                .body(resource);
    }

    @DeleteMapping("/shares/{token}")
    public ResponseEntity<Void> deleteShare(
            @PathVariable String token) {
        service.delete(token);
        return ResponseEntity.noContent().build();
    }
}
