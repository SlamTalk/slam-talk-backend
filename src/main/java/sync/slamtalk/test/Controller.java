package sync.slamtalk.test;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.common.ApiResponse;

/**
*   Swagger 사용 방법 및 테스트 기능
* */
@RestController("/api")
public class Controller {
    @GetMapping("/hello")
    @Operation( // Swagger 쓰는 이유!
            summary = "hello 응답 테스트 기능", // 기능 제목 입니다
            description = "이 기능은 hello 응답을 보내는 기능 입니다.", // 기능 설명
            tags = {"유저", "관리자"} // 유저인지 관리자기능인지 여부
    )
    // 주의 사항 ApiResponse Swagger 도 동일한 클래스가 존재하기 때문에 import 주의 해야합니다.
    public ApiResponse hello(String param){
        return ApiResponse.ok(param + "성공쓰");
    }
}
