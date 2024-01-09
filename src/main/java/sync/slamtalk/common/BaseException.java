package sync.slamtalk.common;


/*
   기능별 커스텀 에러 응답을 준다.
*/
public class BaseException extends RuntimeException{
    private ResponseCode responseCode = ResponseCode.UNCATEGORIZED;

    public BaseException(ResponseCode responseCode){
        super("error");
        this.responseCode = responseCode;
    }

    public ResponseCode getErrorCode(){
        return responseCode;
    }
}
