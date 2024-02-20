package sync.slamtalk.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.mate.dto.response.ParticipantDto;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.service.ParticipantService;

import java.util.List;

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
                                                      @AuthenticationPrincipal Long id){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        long userId = id;

        // * 해당 글에 지원자의 신청 절차를 진행한다.
        ParticipantDto dto = participantService.addParticipant(matePostId, userId, fromParticipantDto);

        return ApiResponse.ok(dto);
    }

    @Operation(
            summary = "메이트 찾기 글에 참여자 상태 변경(수락, 거절, 취소)",
            description = "글 아이디와 참여자 아이디, 변경할 상태를 요청하면 글에 참여자 상태를 변경하는 api 입니다.",
            tags = {"메이트 찾기 / 참여자 목록"}
    )
    @PatchMapping("/{matePostId}/participants/{participantTableId}")
    public ApiResponse updateParticipant(@PathVariable("matePostId") long matePostId, @PathVariable("participantTableId") long participantTableId,
                                         @RequestParam("applyStatus") ApplyStatusType applyStatus, @AuthenticationPrincipal Long id){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        long userId = id;
        ParticipantDto dto;
        if(applyStatus == ApplyStatusType.ACCEPTED){
            dto =  participantService.acceptParticipant(matePostId, participantTableId, userId);
            return ApiResponse.ok(dto);
        }else if(applyStatus == ApplyStatusType.REJECTED){
            dto = participantService.rejectParticipant(matePostId, participantTableId, userId);
            return ApiResponse.ok(dto);
        }else if(applyStatus == ApplyStatusType.CANCELED){
            participantService.cancelParticipant(matePostId, participantTableId, userId);
            return ApiResponse.ok("취소되었습니다.");
        }else{
            return ApiResponse.fail("잘못된 요청입니다.");
        }
    }

    @Operation(
            summary = "메이트 찾기 글에 참여자 목록 조회",
            description = "글 아이디를 요청하면 해당 글에 참여한 참여자 목록을 조회하는 api 입니다.",
            tags = {"메이트 찾기 / 참여자 목록"}
    )
    @GetMapping("/{matePostId}/participants")
    // todo : 프론트엔드에서 호출 할 경우가 없다면 삭제한다.
    public ApiResponse<List<ParticipantDto>> getParticipants(@PathVariable("matePostId") long matePostId){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        long userId = 1;

        // * 해당 글에 지원자의 신청 절차를 진행한다.
        List<ParticipantDto> dto = participantService.getParticipants(matePostId);

        return ApiResponse.ok(dto);
    }

}
