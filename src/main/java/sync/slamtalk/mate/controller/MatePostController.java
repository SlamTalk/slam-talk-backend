package sync.slamtalk.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.mate.dto.MateFormDTO;
import sync.slamtalk.mate.dto.MatePostDTO;
import sync.slamtalk.mate.dto.MatePostListDTO;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.service.MatePostService;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/mate")
@RequiredArgsConstructor
public class MatePostController {

    private final MatePostService matePostService;

    @Operation(
            summary = "메이트 찾기 글 등록",
            description = "메이트 찾기 글쓰기 버튼을 누르고 글을 쓰는 폼을 작성하여 제출할때 접근하는 api 입니다. POST 요청 시 JSON 형식은 다음의 API 명세서를 참고해주세요. https://www.notion.so/6407bc994ee04e009bf22513119eb18a",
            tags = {"메이트 찾기"}
    )
    @PostMapping("/register")
    public ResponseEntity registerMatePost(@RequestBody MateFormDTO mateFormDTO, @AuthenticationPrincipal Long id){

        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        long userId = id;

        // MateFormDTO를 MatePost로 변환한다.
        long matePostId = matePostService.registerMatePost(mateFormDTO, userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/mate/" + matePostId));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @Operation(
            summary = "메이트 찾기 글 조회",
            description = "글 아이디를 요청하면 글을 조회하는 api 입니다.",
            tags = {"메이트 찾기"}
    )
    @GetMapping("/{matePostId}")
    public ApiResponse<MateFormDTO> getMatePost(@PathVariable("matePostId") long matePostId){
        MateFormDTO dto = matePostService.getMatePost(matePostId);
        return ApiResponse.ok(dto);
    }

    @Operation(
            summary = "메이트 찾기 글 수정",
            description = "글 아이디와 수정이 필요한 내용을 JSON 형식으로 요청하면 글을 수정하는 api 입니다.",
            tags = {"메이트 찾기"}
    )
    @PatchMapping("/{matePostId}")
    public ApiResponse<MateFormDTO> updateMatePost(@PathVariable("matePostId") long matePostId, @RequestBody MateFormDTO mateFormDTO,
                                                   @AuthenticationPrincipal Long id){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        long userId = id;

        // * MatePost를 저장한다.
        matePostService.updateMatePost(matePostId, mateFormDTO, userId);

        return ApiResponse.ok();
    }

    @Operation(
            summary = "메이트 찾기 글 삭제",
            description = "글 아이디를 요청하면 글을 삭제하는 api 입니다.(soft delete)",
            tags = {"메이트 찾기"}
    )
    @DeleteMapping("/{matePostId}")
    public ApiResponse<MateFormDTO> deleteMatePost(@PathVariable("matePostId") long matePostId, @AuthenticationPrincipal Long id){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        long userId = id;

        // * 일치한다면 해당 글을 soft delete 한다.
        matePostService.deleteMatePost(matePostId, userId);
        return ApiResponse.ok();
    }

    @Operation(
            summary = "메이트 찾기 글 목록 조회",
            description = "메이트 찾기 글 목록을 조회하는 api 입니다. cursor(모집글 등록일)를 이용하여 최근 등록일 순으로 커서 페이징을 구현합니다. 제공되는 기본 페이지 수는 10 입니다. \n" +
                    "cursor가 없을 경우 현재 시간을 기준으로 최근 등록일 순으로 10개의 글을 반환합니다. \n" +
                    "cursor가 있을 경우 해당 시간을 기준으로 최근 등록일 순으로 10개의 글을 반환합니다. \n" +
                    "cursor는 yyyy-MM-dd HH:mm 형식으로 요청해야 합니다. \n" +
                    "cursor는 반환되는 글 중 가장 마지막 글의 등록일을 기준으로 합니다.",
            tags = {"메이트 찾기"}
    )
    @GetMapping
    public ApiResponse<MatePostListDTO> getMatePostList(@RequestParam(name = "cursor", required = false) Optional<String> cursor) {
        String effectiveCursor = cursor.orElse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        List<MatePostDTO> listedMatePostDTO = matePostService.getMatePostsByCurser(effectiveCursor, 10);

        MatePostListDTO response = new MatePostListDTO();
        response.setMatePostList(listedMatePostDTO);
        if (!listedMatePostDTO.isEmpty()) {
            String nextCursor = listedMatePostDTO.get(listedMatePostDTO.size() - 1).getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            response.setNextCursor(nextCursor);
        }

        return ApiResponse.ok(response);
    }
}
