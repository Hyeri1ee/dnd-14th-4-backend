package whatsinmypack.mvp.adapter.out.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import whatsinmypack.mvp.domain.item.port.ImageStoragePort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Component
public class ImageStorageAdapter implements ImageStoragePort {

    private final Path uploadPath;
    private final String baseUrl;

    public ImageStorageAdapter(
            @Value("${app.upload.path}") String uploadPath,
            @Value("${app.upload.base-url}") String baseUrl
    ) {
        this.uploadPath = Paths.get(uploadPath).toAbsolutePath().normalize();
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 디렉토리 생성 실패: " + uploadPath, e);
        }
    }

    @Override
    public List<String> store(List<MultipartFile> files, Long userId, Long itemId, String itemName) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        String segmentItemId = itemId != null ? String.valueOf(itemId) : "new";
        String segmentItemName = sanitizePathSegment(itemName != null ? itemName : "item");
        Path dir = uploadPath.resolve(String.valueOf(userId)).resolve(segmentItemId).resolve(segmentItemName);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 디렉토리 생성 실패: " + dir, e);
        }
        return files.stream()
                .filter(f -> f != null && !f.isEmpty())
                .map(f -> storeOne(f, dir, userId, segmentItemId, segmentItemName))
                .toList();
    }

    private String storeOne(MultipartFile file, Path dir, Long userId, String segmentItemId, String segmentItemName) {
        String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
                : ".jpg";
        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename);
        try {
            Files.copy(file.getInputStream(), target);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장 실패: " + file.getOriginalFilename(), e);
        }
        return baseUrl + userId + "/" + segmentItemId + "/" + segmentItemName + "/" + filename;
    }

    private static String sanitizePathSegment(String name) {
        if (name == null || name.isBlank()) return "item";
        return name.replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", "_")
                .trim();
    }
}
