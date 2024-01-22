package sync.slamtalk.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.mate.dto.MateFormDTO;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.service.MatePostService;

@Slf4j
@RestController
@RequestMapping("/api/mate")
@RequiredArgsConstructor
public class MatepostController {

    private final MatePostService matePostService;

    @Operation(
            summary = "메이트 찾기 글 등록",
            description = "메이트 찾기 글쓰기 버튼을 누르고 글을 쓰는 폼을 작성하여 제출할때 접근하는 api 입니다. POST 요청 시 JSON 형식은 다음의 API 명세서를 참고해주세요. https://www.notion.so/6407bc994ee04e009bf22513119eb18a",
            tags = {"메이트 찾기"}
    )
    @PostMapping("/register")
    public ApiResponse registerMatePost(@RequestBody MateFormDTO mateFormDTO){

        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        int userId = 1;

        // * 해당 게시글 등록 폼에 입력된 작성자 ID와 접속자 ID가 일치하는지 확인한다.


        // * MateFormDTO를 MatePost로 변환한다.
        MatePost matePost = mateFormDTO.toEntity(userId);

        // * MatePost를 저장한다.
        matePostService.registerMatePost(matePost);

        return ApiResponse.ok();
    }

    @Operation(
            summary = "메이트 찾기 글 조회",
            description = "글 아이디를 요청하면 글을 조회하는 api 입니다.",
            tags = {"메이트 찾기"}
    )
    @GetMapping("/{matePostId}")
    public ApiResponse<MateFormDTO> getMatePost(long matePostId){
        MateFormDTO dto = matePostService.getMatePost(matePostId);
        return ApiResponse.ok(dto);
    }

    @Operation(
            summary = "메이트 찾기 글 수정",
            description = "글 아이디와 수정이 필요한 내용을 JSON 형식으로 요청하면 글을 수정하는 api 입니다.",
            tags = {"메이트 찾기"}
    )
    @PatchMapping("/{matePostId}")
    public ApiResponse<MateFormDTO> updateMatePost(@PathVariable long matePostId, @RequestBody MateFormDTO mateFormDTO){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        int userId = 1;

        // * 접근한 유저 아이디와 수정하려는 글의 작성자 아이디가 일치하는지 확인한다.
        // * 일치하지 않으면 에러 메세지를 보낸다.

        // * MatePost를 저장한다.
        matePostService.updateMatePost(matePostId, mateFormDTO);

        return ApiResponse.ok();
    }

    @Operation(
            summary = "메이트 찾기 글 삭제",
            description = "글 아이디를 요청하면 글을 삭제하는 api 입니다.(soft delete)",
            tags = {"메이트 찾기"}
    )
    @DeleteMapping("/{matePostId}")
    public ApiResponse<MateFormDTO> deleteMatePost(@PathVariable long matePostId){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        int userId = 1;

        // * 접근한 유저 아이디와 수정하려는 글의 작성자 아이디가 일치하는지 확인한다.
        // * 일치하지 않으면 에러 메시지를 반환한다.

        // * 일치한다면 해당 글을 soft delete 한다.
        matePostService.deleteMatePost(matePostId);
        return ApiResponse.ok();
    }
    //
//    @Operation(
//            summary = "메이트 찾기 글에 참여하기",
//            description = "글 아이디와 참여자 정보를 요청하면 글에 참여하는 api 입니다.",
//            tags = {"메이트 찾기"}
//    )
//    @PostMapping("/{matePostId}/participant")
//    public ApiResponse<MateFormDTO> addParticipant(long matePostId, MateFormDTO mateFormDTO){
//        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
//        int userId = 1;
//        String userNickname = "테스트";
//
//        // * MateFormDTO를 MatePost로 변환한다.
//        MatePost matePost = mateFormDTO.toEntity(userId, userNickname);
//
//        // * MatePost에 참여자를 추가한다.
//        MatePostService.addParticipant(matePostId, matePost.getParticipants().get(0));
//
//        return ApiResponse.ok();
//    }
    @GetMapping("/test")
    public ApiResponse<MateFormDTO> test(){
        return ApiResponse.ok();
    }
}
