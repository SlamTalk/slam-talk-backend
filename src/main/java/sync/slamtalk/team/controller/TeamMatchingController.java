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
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.team.dto.*;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.team.repository.TeamMatchingRepository;
import sync.slamtalk.team.service.TeamMatchingService;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static sync.slamtalk.mate.error.MateErrorResponseCode.USER_NOT_AUTHORIZED;

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
        headers.setLocation(URI.create("/api/match/" + matePostId));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @Operation(
            summary = "팀 매칭 조회",
            description = "팀 매칭 글을 조회하는 api 입니다.",
            tags = {"팀 매칭"}
    )
    @GetMapping("/{teamMatchingId}/post")
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
            description = "커서 페이징 방식으로 팀 매칭 글 리스트를 조회하는 api 입니다. " +
                    "다음 글 목록을 불러오려면 이전 요청 응답 모델에 넣었던 cursor값을 쿼리파라미터의 cursor에 적어주세요. (yyyy-MM-dd HH:mm:ss.SSS)"
                    + "limit은 한번 조회할 때 가져올 수 있는 최대 글 개수이며, 기본값은 10개입니다.",
            tags = {"팀 매칭"}
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
            tags = {"팀 매칭 / 신청자 목록",}
    )
    @PostMapping("/{teamMatchingId}/apply")
    public ApiResponse applyTeamMatching(@PathVariable("teamMatchingId") Long teamMatchingId, @RequestBody FromApplicantDto fromApplicantDto,
                                         @AuthenticationPrincipal Long id){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        Long userId = id;

        ToApplicantDto dto = teamMatchingService.applyTeamMatching(teamMatchingId, fromApplicantDto, userId);

        return ApiResponse.ok(dto);
    }





}
