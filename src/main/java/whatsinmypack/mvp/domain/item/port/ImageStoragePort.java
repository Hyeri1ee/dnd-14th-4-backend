package whatsinmypack.mvp.domain.item.port;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStoragePort {
    List<String> store(List<MultipartFile> files);
}
