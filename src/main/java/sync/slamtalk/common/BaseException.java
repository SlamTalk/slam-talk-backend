package sync.slamtalk.common;


/*
   기능별 커스텀 에러 응답을 준다.
*/
public class BaseException extends RuntimeException{
    private ErrorResponseCode errorResponseCode = ErrorResponseCode.UNCATEGORIZED;

    public BaseException(ErrorResponseCode errorResponseCode){
        super("error");
        this.errorResponseCode = errorResponseCode;
    }

    public ErrorResponseCode getErrorCode(){
        return errorResponseCode;
    }
}
