package sync.slamtalk.common;

import static jakarta.servlet.http.HttpServletResponse.*;

public enum ErrorResponseCode implements ResponseCodeDetails {

    OK(SC_OK, 200,"Request Success"),
    INVALID_TOKEN(SC_BAD_REQUEST,4001,"Token Invalid"),

    // 로그인
    LOGIN_FAIL(SC_UNAUTHORIZED,4011,"Failed Login"),

    // 채팅
    CHAT_FAIL(SC_BAD_REQUEST,4021,"Failed chatting"),

    // 게시판
    BOARD_FAIL(SC_BAD_REQUEST,4031,"Failed Board"),

    // 지도
    MAP_FAIL(SC_BAD_REQUEST,4041,"Failed Map"),

    // 같이 하기
    TOGETHER_FAIL(SC_BAD_REQUEST,4051,"Failed Together"),

    // 팀 매칭
    TEAM_MATCHING_FAIL(SC_BAD_REQUEST,4061,"Failed matching"),


    // 서버에러
    UNCATEGORIZED(SC_INTERNAL_SERVER_ERROR,5000,"Uncategorized"),

    /* s3 bucket 에러*/

    // 이미지 찾을 수 없을 때
    S3_BUCKET_NOT_FOUND(SC_BAD_REQUEST,6000,"image is null"),
    // 이미지 업로드 실패
    S3_BUCKET_CANNOT_UPLOAD(SC_BAD_REQUEST,6001,"failed upload"),
    // 이미지 삭제 실패
    S3_BUCKET_CANNOT_DELETE(SC_BAD_REQUEST,6002,"failed delete"),
    // 지원 하지 않은 확장자 파일 업로드 시
    S3_BUCKET_INVALID_EXTENSION(SC_BAD_REQUEST,6003,"invalid extension"),
    // 파일 크기 제한 초과 시
    S3_BUCKET_EXCEEDED_CAPACITY(SC_BAD_REQUEST,6004,"capacity exceeded")


    ;

    private final int code;
    private int status;
    private final String message;


    ErrorResponseCode(int code, int status, String message){
        this.code = code;
        this.status= status;
        this.message = message;
    }

    public int getCode(){
        return code;
    }

    public int getStatus(){
        return status;
    }

    public String getMessage(){
        return message;
    }
}
