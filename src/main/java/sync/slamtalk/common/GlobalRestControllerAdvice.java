package sync.slamtalk.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 모든 RestController 에서 발생하는 예외를 처리하기 위한 클래스
 */
@Slf4j
@RestControllerAdvice(basePackages = "sync.slamtalk")
// "sync.slamtalk" 패키지 내의 컨트롤러에서 발생하는 예외 처리

public class GlobalRestControllerAdvice {

    // 응답 상태가 INTERNAL_SERVER_ERROR && Exception 호출 시
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleBaseException(Exception e){
        e.printStackTrace();
        log.error("Exception catched in RestControllerAdvice : {}", e.getMessage());
        return ApiResponse.fail(e.getMessage());
    }

    // 응답 상태가 BAD_REQUEST && BaseException 호출 시
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseException.class)
    public ApiResponse<String> handleBaseException(BaseException e){
        log.debug("Exception catched in RestControllerAdvice : {}",e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    // 응답 상태가 BAD_REQUEST && MethodArgumentNotValidException 호출 시
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.debug("Exception cathced in RestControllerAdvice:{}",e.getMessage());
        return ApiResponse.fail(e.getMessage());
    }

    // enum 타입 클라이언트가 잘못 요청 했을 경우 발생하는 exception
    // HttpMessageNotReadableException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        log.debug("Exception cathced in RestControllerAdvice:{}",e.getMessage());
        return ApiResponse.fail(e.getMessage());
    }

}
