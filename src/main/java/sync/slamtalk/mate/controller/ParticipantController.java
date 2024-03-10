package sync.slamtalk.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.common.ErrorResponseCode;
import sync.slamtalk.mate.dto.response.ParticipantDto;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.service.ParticipantService;

@RestController
@RequestMapping("/api/mate")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @Operation(
            summary = "메이트 찾기 글에 참여하기",
            description = "글 아이디와 참여자 정보를 요청하면 글에 참여하는 api 입니다.",
            tags = {"메이트 찾기 / 참여자 목록"}
    )
    @PostMapping("/{matePostId}/participants/register")
    public ApiResponse<ParticipantDto> addParticipant(@PathVariable("matePostId") long matePostId, @RequestBody ParticipantDto fromParticipantDto,
                                                      @AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(participantService.addParticipant(matePostId, userId, fromParticipantDto));
    }

    @Operation(
            summary = "메이트 찾기 글에 참여자 상태 변경(수락, 거절, 취소)",
            description = "글 아이디와 참여자 아이디, 변경할 상태를 요청하면 글에 참여자 상태를 변경하는 api 입니다.",
            tags = {"메이트 찾기 / 참여자 목록"}
    )
    @PatchMapping("/{matePostId}/participants/{participantTableId}")
    public ApiResponse<ParticipantDto> updateParticipant(@PathVariable("matePostId") long matePostId, @PathVariable("participantTableId") long participantTableId,
                                                         @RequestParam("applyStatus") ApplyStatusType applyStatus, @AuthenticationPrincipal Long userId) {

        ParticipantDto dto = null;
        switch (applyStatus) {
            case ACCEPTED:
                dto = participantService.acceptParticipant(matePostId, participantTableId, userId);
                break;
            case REJECTED:
                dto = participantService.rejectParticipant(matePostId, participantTableId, userId);
                break;
            case CANCELED:
                participantService.cancelParticipant(matePostId, participantTableId, userId);
                break;
            default:
                throw new BaseException(ErrorResponseCode.UNCATEGORIZED);
        }

        return ApiResponse.ok(dto);
    }

//    @Operation(
//            summary = "메이트 찾기 글에 참여자 목록 조회",
//            description = "글 아이디를 요청하면 해당 글에 참여한 참여자 목록을 조회하는 api 입니다.",
//            tags = {"메이트 찾기 / 참여자 목록"}
//    )
//    @GetMapping("/{matePostId}/participants")
//    public ApiResponse<List<ParticipantDto>> getParticipants(@PathVariable("matePostId") long matePostId) {
//        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
//        long userId = 1;
//
//        // * 해당 글에 지원자의 신청 절차를 진행한다.
//        List<ParticipantDto> dto = participantService.getParticipants(matePostId);
//
//        return ApiResponse.ok(dto);
//    }

}
