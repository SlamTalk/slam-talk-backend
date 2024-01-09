package sync.slamtalk.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

/**
 * API 응답을 위한 클래스
 * @param <T> 응답 결과로 포함될 데이터의 타입
 */
@Getter
@JsonPropertyOrder({"success", "status", "message", "results"})
public class ApiResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T results;


    private ApiResponse(boolean success, String message, T results){
        this.success= success;
        this.message = message;
        this.results = results;
    }


    private ApiResponse(int status, String message){
        this.success = false;
        this.status = status;
        this.message = message;
        this.results = null;
    }


    // 성공 응답
    public static <T>ApiResponse<T> ok(T results, String message){
        return new ApiResponse<>(true,message,results);
    }

    // 성공 응답
    public static <T>ApiResponse<T> ok(T results){
        return new ApiResponse<>(true,"요청에 성공했습니다.",results);
    }


    // 성공 응답
    public static <T>ApiResponse<T> ok(){
        return new ApiResponse<>(true,"요청에 성공했습니다",null);
    }


    // 성공 응답
    public static <T>ApiResponse<T> fail(){
        return new ApiResponse<>(false,"요청에 실패했습니다",null);
    }


    // 실패 응답
    public static <T>ApiResponse<T> fail(ResponseCode responseCode){
        return new ApiResponse<>(responseCode.getStatus(), responseCode.getMessage());
    }


    // 실패 응답
    public static <T>ApiResponse<T> fail(String message){
        return new ApiResponse<>(false,message,null);
    }

}
