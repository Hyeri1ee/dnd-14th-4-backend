package whatsinmypack.mvp.domain.item.entity;

public enum UsePeriod {

    BELOW_ONE_YEAR("1년 이하"),
    ABOVE_ONE_YEAR("1년 이상"),
    ABOVE_THREE_YEAR("3년 이상"),
    ABOVE_FIVE_YEAR("5년 이상");

    private final String kor;

    UsePeriod(String kor){
        this.kor = kor;
    }

    public String kor(){
        return kor;
    }
    /*
    System.out.println(UsePeriod.BELOW_ONE_YEAR.name());      //BELOW_ONE_YEAR
    System.out.println(UsePeriod.BELOW_ONE_YEAR.kor());     //1년 이하
     */

    //map으로 enum내 값 찾을 때 o(1)으로 설정하는 메소드의 트레이드오프
        //enum 개수가 5개 미만인 경우, map을 만드는 것 > 메모리 사용
}
