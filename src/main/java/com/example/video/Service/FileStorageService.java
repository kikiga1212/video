package com.example.video.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("{app.upload.dir:uploads}")
    private String uploadDir;

    /**파일 저장 후 접근 경로 반환 */
    public String store(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) return null;

        try {
            Path dir = Paths.get(uploadDir, subDir).toAbsolutePath();
            Files.createDirectories(dir);

            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID() + ext;

            Path dest = dir.resolve(filename);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            // 웹 접근 경로 반환
            return "/uploads/" + subDir + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }

    /** 파일 삭제 */
    public void delete(String webPath) {
        if (webPath == null || webPath.isBlank()) return;
        try {
            // /uploads/... → uploads/... 로 변환
            String relative = webPath.startsWith("/") ? webPath.substring(1) : webPath;
            Path file = Paths.get(relative).toAbsolutePath();
            Files.deleteIfExists(file);
        } catch (IOException ignored) {}
    }
}
