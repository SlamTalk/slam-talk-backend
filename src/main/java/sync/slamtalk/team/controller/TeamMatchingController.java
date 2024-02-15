package sync.slamtalk.team.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.team.dto.*;
import sync.slamtalk.team.service.TeamMatchingService;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("api/match")
@RequiredArgsConstructor
public class TeamMatchingController {

    private final TeamMatchingService teamMatchingService;

    @Operation(
            summary = "팀 매칭 등록",
            description = "팀 매칭을 위해 글을 생성하는 api 입니다.",
            tags = {"팀 매칭"}
    )
    @PostMapping("/register")
    public ResponseEntity registerTeamMatchingPage(@Valid @RequestBody FromTeamFormDTO fromTeamFormDTO, @AuthenticationPrincipal Long id){
        log.debug("fromTeamFormDTO : {}", fromTeamFormDTO);

        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다. User객체로 대체할것
        long userId = id;

        long matePostId = teamMatchingService.registerTeamMatching(fromTeamFormDTO, userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/match/read/" + matePostId));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @Operation(
            summary = "팀 매칭 조회",
            description = "팀 매칭 글을 조회하는 api 입니다.",
            tags = {"팀 매칭", "게스트"}
    )
    @GetMapping("/read/{teamMatchingId}")
    public ApiResponse<ToTeamFormDTO> getTeamMatchingPage(@PathVariable("teamMatchingId") long teamMatchingId){

        ToTeamFormDTO dto = teamMatchingService.getTeamMatching(teamMatchingId);

        return ApiResponse.ok(dto);
    }

    @Operation(
            summary = "팀 매칭 수정",
            description = "팀 매칭 글을 수정하는 api 입니다.",
            tags = {"팀 매칭"}
    )
    @PatchMapping("/{teamMatchingId}")
    public ApiResponse updateTeamMatchingPage(@PathVariable("teamMatchingId") long teamMatchingId, @RequestBody FromTeamFormDTO fromTeamFormDTO,
                                              @AuthenticationPrincipal Long id){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        Long userId = id;

        teamMatchingService.updateTeamMatching(teamMatchingId, fromTeamFormDTO,userId);

        return ApiResponse.ok();
    }

    @Operation(
            summary = "팀 매칭 삭제",
            description = "팀 매칭 글을 삭제하는 api 입니다.",
            tags = {"팀 매칭"}
    )
    @DeleteMapping("/{teamMatchingId}")
    public ApiResponse deleteTeamMatchingPage(@PathVariable("teamMatchingId") long teamMatchingId, @AuthenticationPrincipal Long id){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        Long userId = id;

        teamMatchingService.deleteTeamMatching(teamMatchingId, userId);

        return ApiResponse.ok();
    }

    @Operation(
            summary = "팀 매칭 리스트 조회",
            description = "커서 페이징 방식으로 팀 매칭 글 리스트를 조회하는 api 입니다. \n" +
                    "다음 글 목록을 불러오려면 이전 요청 응답 모델에 넣었던 cursor값을 쿼리파라미터의 cursor에 적어주세요. (yyyy-MM-dd HH:mm:ss.SSS)\n"
                    + "limit은 한번 조회할 때 가져올 수 있는 최대 글 개수이며, 기본값은 10개입니다.",
            tags = {"팀 매칭","게스트"}
    )
    @GetMapping("/list")
    public ApiResponse getTeamMatchingList(@RequestParam(name="cursor", required = false) Optional<String> cursor, @RequestParam(name="limit", required = false) Optional<Integer> limit){
        List<ToTeamFormDTO> dtoList;
        String cursorTime = cursor.orElse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        int limitNumber = 10;
        if(limit.isPresent()){
            limitNumber = limit.get();
        }
        dtoList = teamMatchingService.getTeamMatchingList(limitNumber, cursorTime);
        ToTeamMatchingListDto resultDto = new ToTeamMatchingListDto();
        if(dtoList.size() == 0){
            return ApiResponse.ok(resultDto);
        }else if(dtoList.size() < limitNumber) {
            resultDto.setCursor(null);
        }else{
            resultDto.setCursor(dtoList.get(dtoList.size() - 1).getCreatedAt());
        }
        resultDto.setTeamMatchingList(dtoList);
        return ApiResponse.ok(resultDto);
    }

    @Operation(
            summary = "팀 매칭 신청",
            description = "팀 매칭 글에 신청하는 api 입니다.",
            tags = {"팀 매칭 / 신청자 목록"}
    )
    @PostMapping("/{teamMatchingId}/apply")
    public ApiResponse applyTeamMatching(@PathVariable("teamMatchingId") Long teamMatchingId, @RequestBody FromApplicantDto fromApplicantDto,
                                         @AuthenticationPrincipal Long id){
        Long chatroomId = teamMatchingService.applyTeamMatching(teamMatchingId, fromApplicantDto, id);

        return ApiResponse.ok(chatroomId);
    }

    @Operation(
            summary = "팀 매칭 신청자 관련 동작 api",
            description =
                    "거절하기(REJECTED) : 해당 신청자를 거절합니다. \n" +
                    "취소하기(CANCELED) : 해당 신청자의 신청을 취소합니다. \n" +
                    "수락하기(ACCEPTED) : 해당 신청자를 수락합니다. 수락된 신청자는 취소하기를 통해 CANCELED 상태로 변경할 수 있습니다. \n",
            tags = {"팀 매칭 / 신청자 목록"}
    )
    @PatchMapping("/{teamMatchingId}/apply/{teamApplicantId}")
    public ApiResponse updateTeamMatching(@PathVariable("teamMatchingId") Long teamMatchingId, @PathVariable("teamApplicantId") Long teamApplicantId,
                                          @RequestParam("applyStatus") ApplyStatusType applyStatus, @AuthenticationPrincipal Long id){


        if(applyStatus == ApplyStatusType.ACCEPTED){
            teamMatchingService.acceptApplicant(teamMatchingId, teamApplicantId, id);
        }else if(applyStatus == ApplyStatusType.REJECTED){
            teamMatchingService.rejectApplicant(teamMatchingId, teamApplicantId, id);
        }else if(applyStatus == ApplyStatusType.CANCELED){
            teamMatchingService.cancelApplicant(teamMatchingId, teamApplicantId, id);
        }else{
            return ApiResponse.fail("잘못된 요청입니다.");
        }
        return ApiResponse.ok();
    }

    @Operation(
            summary = "팀 매칭 모집 완료",
            description = "팀 매칭 글의 모집을 완료하는 api 입니다.",
            tags = {"팀 매칭 / 신청자 목록"}
    )
    @PatchMapping("/{teamMatchingId}/complete")
    public ApiResponse completeTeamMatching(@PathVariable("teamMatchingId") Long teamMatchingId, @AuthenticationPrincipal Long id){
        teamMatchingService.completeTeamMatching(teamMatchingId, id);
        return ApiResponse.ok();
    }



}
