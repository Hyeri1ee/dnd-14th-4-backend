package whatsinmypack.mvp.adapter.in.web.pack.req;

import jakarta.validation.constraints.Size;
import java.util.List;

public record CreatePackRequest(
        List<Long> items,
        @Size(max = 10, message = "팩 제목은 10자 이내로 입력해주세요.")
        String title,
        String contextCategory, // Long으로 받을까 그냥..?
        String review
) {
}
