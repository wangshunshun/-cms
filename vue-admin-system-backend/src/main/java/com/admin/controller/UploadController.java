package com.admin.controller;

import com.admin.utils.Result;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "images");

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.error(400, "上传文件不能为空");
        }

        Files.createDirectories(uploadDir);

        String originalFilename = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalFilename.replaceAll("[\\\\/]+", "_");
        Path target = uploadDir.resolve(storedName).normalize();
        if (!target.startsWith(uploadDir)) {
            return Result.error(400, "非法文件路径");
        }

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        Map<String, String> data = new HashMap<>();
        data.put("url", "/api/upload/" + storedName);
        return Result.success(data);
    }

    @GetMapping("/{storedName:.+}")
    public ResponseEntity<Resource> preview(@PathVariable String storedName) {
        Path filePath = uploadDir.resolve(storedName).normalize();
        if (!filePath.startsWith(uploadDir) || !Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(filePath.toFile());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }
}
