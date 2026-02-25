package whatsinmypack.mvp.adapter.out.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import whatsinmypack.mvp.domain.item.port.ImageStoragePort;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class S3ImageStorageAdapter implements ImageStoragePort {
    private static final String ITEM_ROOT_PREFIX = "localtest";

    private final S3Client s3Client;
    private final String bucket;
    private final String keyPrefix;
    private final String baseUrl;

    public S3ImageStorageAdapter(
            S3Client s3Client,
            @Value("${app.s3.bucket}") String bucket,
            @Value("${app.s3.key-prefix}") String keyPrefix,
            @Value("${app.s3.base-url}") String baseUrl
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.keyPrefix = normalizeItemKeyPrefix(keyPrefix);
        this.baseUrl = normalizeItemBaseUrl(baseUrl);
    }

    @Override
    public List<String> store(List<MultipartFile> files, Long userId, Long itemId, String itemName) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        String segmentItemId = itemId != null ? String.valueOf(itemId) : "new";
        String segmentItemName = sanitizePathSegment(itemName != null ? itemName : "item");
        return files.stream()
                .filter(f -> f != null && !f.isEmpty())
                .map(f -> storeOne(f, userId, segmentItemId, segmentItemName))
                .toList();
    }

    private String storeOne(MultipartFile file, Long userId, String segmentItemId, String segmentItemName) {
        String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
                : ".jpg";
        String filename = UUID.randomUUID() + ext;
        String key = keyPrefix + userId + "/" + segmentItemId + "/" + segmentItemName + "/" + filename;

        String contentType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
        try {
            byte[] bytes = file.getBytes();
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
        } catch (IOException e) {
            throw new IllegalStateException("S3 이미지 저장 실패: " + file.getOriginalFilename(), e);
        }
        return baseUrl + userId + "/" + segmentItemId + "/" + segmentItemName + "/" + filename;
    }

    private static String sanitizePathSegment(String name) {
        if (name == null || name.isBlank()) return "item";
        return name.replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", "_")
                .trim();
    }

    private static String normalizeItemKeyPrefix(String configuredKeyPrefix) {
        String normalized = configuredKeyPrefix == null ? "" : configuredKeyPrefix.trim();
        normalized = normalized.replaceAll("^/+", "").replaceAll("/+$", "");
        if (normalized.equals("upload")) {
            return ITEM_ROOT_PREFIX + "/";
        }
        if (normalized.startsWith("upload/")) {
            return ITEM_ROOT_PREFIX + normalized.substring("upload".length()) + "/";
        }
        if (normalized.isBlank()) {
            return ITEM_ROOT_PREFIX + "/";
        }
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }

    private static String normalizeItemBaseUrl(String configuredBaseUrl) {
        String normalized = configuredBaseUrl == null ? "" : configuredBaseUrl.trim();
        normalized = normalized.replaceAll("/+$", "");
        if (normalized.endsWith("/upload")) {
            return normalized.substring(0, normalized.length() - "/upload".length()) + "/" + ITEM_ROOT_PREFIX + "/";
        }
        return normalized.endsWith("/" + ITEM_ROOT_PREFIX)
                ? normalized + "/"
                : normalized + "/" + ITEM_ROOT_PREFIX + "/";
    }
}
