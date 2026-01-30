package whatsinmypack.mvp.domain.common;

public class NotFoundEntityException extends RuntimeException {
    private final Class<?> entity;

    public NotFoundEntityException(Class<?> entity, String message) {
        super(message);
        this.entity = entity;
    }
}
