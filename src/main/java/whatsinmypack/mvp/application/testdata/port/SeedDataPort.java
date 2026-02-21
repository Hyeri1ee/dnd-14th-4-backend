package whatsinmypack.mvp.application.testdata.port;

/**
 * 테스트용 시드 데이터 생성/삭제 (user 2명, item 5개). user_1@test.com, user_2@test.com.
 */
public interface SeedDataPort {

    void seed();

    void deleteSeed();
}
