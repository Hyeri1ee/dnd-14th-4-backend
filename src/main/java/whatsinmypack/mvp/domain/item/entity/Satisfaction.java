package whatsinmypack.mvp.domain.item.entity;

public enum Satisfaction {
    GOOD("좋아요"),
    VERY_GOOD("매우 좋아요"),
    MUST_HAVE("인생템!");

    private final String kor;

    Satisfaction(String kor){
        this.kor = kor;
    }

    public String kor(){
        return kor;
    }

}
