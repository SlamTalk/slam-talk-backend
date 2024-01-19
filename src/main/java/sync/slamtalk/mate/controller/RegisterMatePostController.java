package sync.slamtalk.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.dto.MateFormDTO;
import sync.slamtalk.mate.service.MatePostService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterMatePostController {

    private final MatePostService MatePostService;

    @Operation(
            summary = "메이트 찾기 글 등록",
            description = "메이트 찾기 글쓰기 버튼을 누르고 글을 쓰는 폼을 작성하여 제출할때 접근하는 api 입니다. POST 요청 시 JSON 형식은 다음의 API 명세서를 참고해주세요. https://www.notion.so/6407bc994ee04e009bf22513119eb18a",
            tags = {"메이트 찾기"}
    )
    @PostMapping("/api/mate/register")
    public ApiResponse registerMatePost(@RequestBody MateFormDTO mateFormDTO){

        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        int userId = 1;
        String userNickname = "테스트";

        // * MateFormDTO를 MatePost로 변환한다.
        MatePost matePost = mateFormDTO.toEntity(userId, userNickname);

        // * MatePost를 저장한다.
        MatePostService.registerMatePost(matePost);

        return ApiResponse.ok();
    }

    @GetMapping("/test")
    public ApiResponse<MateFormDTO> test(){
        return ApiResponse.ok();
    }
}
