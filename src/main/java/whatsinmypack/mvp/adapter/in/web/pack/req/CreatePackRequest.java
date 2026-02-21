package whatsinmypack.mvp.adapter.in.web.pack.req;

import java.util.List;

public record CreatePackRequest(
        List<Long> items,
        String title,
        String contextCategory, // Long으로 받을까 그냥..?
        String review
) {
}
