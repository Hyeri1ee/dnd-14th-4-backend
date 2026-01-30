package whatsinmypack.mvp.domain.user.exception;

import lombok.Getter;

@Getter
public class DuplicateNicknameException extends RuntimeException {
    private final String duplicatedNickname;
    private final String message;

    public DuplicateNicknameException(String duplicatedNickname, String message) {
        super(message);
        this.duplicatedNickname = duplicatedNickname;
        this.message = message;
    }
}
