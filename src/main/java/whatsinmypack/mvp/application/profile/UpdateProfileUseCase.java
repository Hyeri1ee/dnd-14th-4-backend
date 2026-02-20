package whatsinmypack.mvp.application.profile;

import whatsinmypack.mvp.domain.user.entity.User;

public interface UpdateProfileUseCase {

    User updateProfile(UpdateProfileCommand command);
}
