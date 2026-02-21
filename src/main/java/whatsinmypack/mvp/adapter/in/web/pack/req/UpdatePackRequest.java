package whatsinmypack.mvp.adapter.in.web.pack.req;

import java.util.List;

public record UpdatePackRequest(
        String introduction,
        List<Long> addItems,
        List<Long> removeItems
) {
}
