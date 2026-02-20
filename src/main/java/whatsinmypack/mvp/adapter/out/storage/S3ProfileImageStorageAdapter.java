package whatsinmypack.mvp.adapter.out.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import whatsinmypack.mvp.domain.user.port.StoreProfileImagePort;

import java.io.IOException;

@Component
public class S3ProfileImageStorageAdapter implements StoreProfileImagePort {

    private final S3Client s3Client;
    private final String bucket;
    private final String keyPrefix;
    private final String baseUrl;

    public S3ProfileImageStorageAdapter(
            S3Client s3Client,
            @Value("${app.s3.bucket}") String bucket,
            @Value("${app.s3.key-prefix}") String keyPrefix,
            @Value("${app.s3.base-url}") String baseUrl
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.keyPrefix = keyPrefix.endsWith("/") ? keyPrefix : keyPrefix + "/";
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    @Override
    public String store(MultipartFile file, Long userId) {
        String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
                : ".jpg";
        String filename = System.currentTimeMillis() + ext;
        String key = keyPrefix + "profile/" + userId + "/" + filename;

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
            throw new IllegalStateException("프로필 이미지 저장 실패: " + file.getOriginalFilename(), e);
        }
        return baseUrl + "profile/" + userId + "/" + filename;
    }
}
