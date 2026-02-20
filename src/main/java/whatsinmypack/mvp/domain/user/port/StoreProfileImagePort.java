package whatsinmypack.mvp.domain.user.port;

import org.springframework.web.multipart.MultipartFile;

/**
 * 프로필 사진을 S3 upload/profile/{userId}/{timestamp}.ext 에 저장하고 URL 반환.
 */
public interface StoreProfileImagePort {

    String store(MultipartFile file, Long userId);
}
