package sync.slamtalk.common;

public interface ResponseCode {

    /* 요청 처리 결과에 대한 코드값으로 HTTP Status 코드와는 별개로
     * 보다 상세한 정보를 나타내야 한다.
     */
    int getCode();

    /*
     * code 가 어떤 메세지를 의미하는지 나타내기 위함
     */
    String getMessage();
}
