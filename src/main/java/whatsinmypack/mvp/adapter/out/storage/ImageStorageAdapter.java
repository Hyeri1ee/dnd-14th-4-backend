package whatsinmypack.mvp.adapter.out.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ImageStorageAdapter {

    private static final int MAX_IMAGES = 5;
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private final Path uploadPath;
    private final String baseUrl;

    public ImageStorageAdapter(
            @Value("${app.upload.path}") String uploadPath,
            @Value("${app.upload.base-url}") String baseUrl
    ) {
        this.uploadPath = Paths.get(uploadPath).toAbsolutePath().normalize();
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        initUploadDir();
    }

    private void initUploadDir() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 디렉토리를 생성할 수 없습니다: " + uploadPath, e);
        }
    }

    /**
     * multipart 이미지 목록을 저장하고 저장된 경로(URL) 목록을 반환합니다.
     * 빈 파일은 무시합니다. 최대 5개까지 저장합니다.
     */
    public List<String> store(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        if (files.size() > MAX_IMAGES) {
            throw new IllegalArgumentException("리뷰 이미지는 최대 " + MAX_IMAGES + "개까지 등록할 수 있습니다.");
        }
        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            validateContentType(file.getContentType());
            String storedPath = storeOne(file);
            paths.add(storedPath);
        }
        return paths;
    }

    private void validateContentType(String contentType) {
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "허용되지 않는 이미지 형식입니다. 허용: " + ALLOWED_CONTENT_TYPES);
        }
    }

    private String storeOne(MultipartFile file) {
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        Path target = uploadPath.resolve(filename);
        try {
            Files.copy(file.getInputStream(), target);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장에 실패했습니다: " + file.getOriginalFilename(), e);
        }
        return baseUrl + filename;
    }

    private static String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
