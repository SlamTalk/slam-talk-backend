package sync.slamtalk.common;


/*
   기능별 커스텀 에러 응답을 준다.
*/
public class BaseException extends RuntimeException{
    private final ResponseCodeDetails responseCodeInterface;

    public BaseException(ResponseCodeDetails responseCodeInterface){
        super("error");
        this.responseCodeInterface = responseCodeInterface;
    }

    public ResponseCodeDetails getErrorCode(){
        return responseCodeInterface;
    }
}
