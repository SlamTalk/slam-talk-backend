package sync.slamtalk.team.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.team.dto.FromTeamFormDTO;
import sync.slamtalk.team.dto.ToTeamFormDTO;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.team.repository.TeamMatchingRepository;
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
    private final TeamMatchingRepository teamMatchingRepository;

    @Operation(
            summary = "팀 매칭 등록",
            description = "팀 매칭을 위해 글을 생성하는 api 입니다.",
            tags = {"팀 매칭"}
    )
    @PostMapping("/register")
    public ResponseEntity registerTeamMatchingPage(@Valid @RequestBody FromTeamFormDTO fromTeamFormDTO){
        log.debug("fromTeamFormDTO : {}", fromTeamFormDTO);

        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다. User객체로 대체할것
        long userId = 1;

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
    @GetMapping("/{teamMatchingId}")
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
    public ApiResponse updateTeamMatchingPage(@PathVariable("teamMatchingId") long teamMatchingId, @RequestBody FromTeamFormDTO fromTeamFormDTO){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        int userId = 1;

        // * 해당 게시글 등록 폼에 입력된 작성자 ID와 접속자 ID가 일치하는지 확인한다.

        teamMatchingService.updateTeamMatching(teamMatchingId, fromTeamFormDTO);
        return ApiResponse.ok();
    }

    @Operation(
            summary = "팀 매칭 삭제",
            description = "팀 매칭 글을 삭제하는 api 입니다.",
            tags = {"팀 매칭"}
    )
    @DeleteMapping("/{teamMatchingId}")
    public ApiResponse deleteTeamMatchingPage(@PathVariable("teamMatchingId") long teamMatchingId){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        int userId = 1;

        TeamMatching teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow();

        ToTeamFormDTO dto;
        // * 해당 게시글 등록 폼에 입력된 작성자 ID와 접속자 ID가 일치하는지 확인한다.
        if(teamMatchingEntity.isCorrespondTo(userId)){
            teamMatchingService.deleteTeamMatching(teamMatchingId, teamMatchingEntity);
        }else{
            throw new IllegalArgumentException("해당 글을 삭제할 권한이 없습니다.");
        }

        return ApiResponse.ok();
    }

    @GetMapping
    public ApiResponse getTeamMatchingList(@RequestParam(name="cursor", required = false) Optional<String> cursor, @RequestParam(name="limit", required = false) Optional<Integer> limit){
        List<ToTeamFormDTO> dtoList;
        String cursorTime = cursor.orElse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        int limitNumber = 10;
        if(limit.isPresent()){
            limitNumber = limit.get();
        }
        dtoList = teamMatchingService.getTeamMatchingList(limitNumber, cursorTime);
        return ApiResponse.ok(dtoList);
    }
}
