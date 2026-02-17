package whatsinmypack.mvp.domain.item.port;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStoragePort {

    /**
     * @param itemId 생성 시에는 null (경로에 "new" 사용)
     */
    List<String> store(List<MultipartFile> files, Long userId, Long itemId, String itemName);
}
