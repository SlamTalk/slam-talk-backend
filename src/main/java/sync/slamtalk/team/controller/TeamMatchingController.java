package sync.slamtalk.team.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.team.TeamMapper;
import sync.slamtalk.team.dto.FromTeamFormDTO;
import sync.slamtalk.team.dto.ToTeamFormDTO;
import sync.slamtalk.team.entity.TeamMatchings;
import sync.slamtalk.team.repository.TeamMatchingRepository;
import sync.slamtalk.team.service.TeamMatchingService;

import java.net.URI;

@RestController
@RequestMapping("api/match")
@RequiredArgsConstructor
public class TeamMatchingController {

    private final TeamMatchingService teamMatchingService;
    private final TeamMatchingRepository teamMatchingRepository;
    private final TeamMapper teamMapper;

    @PostMapping("/register")
    public ResponseEntity registerTeamMatchingPage(@Valid @RequestBody FromTeamFormDTO fromTeamFormDTO){

        TeamMatchings teamMatchingEntity = teamMapper.createToTeamMatching(fromTeamFormDTO);
        long matePostId = teamMatchingService.registerTeamMatching(teamMatchingEntity);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/match/" + matePostId));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("/{teamMatchingId}")
    public ApiResponse<ToTeamFormDTO> getTeamMatchingPage(@PathVariable("teamMatchingId") long teamMatchingId){
        ToTeamFormDTO dto = teamMatchingService.getTeamMatching(teamMatchingId);
        return ApiResponse.ok(dto);
    }

    @PatchMapping("/{teamMatchingId}")
    public ApiResponse<ToTeamFormDTO> updateTeamMatchingPage(@PathVariable("teamMatchingId") long teamMatchingId, @RequestBody FromTeamFormDTO fromTeamFormDTO){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        int userId = 1;

        // * 해당 게시글 등록 폼에 입력된 작성자 ID와 접속자 ID가 일치하는지 확인한다.

        ToTeamFormDTO dto = teamMatchingService.updateTeamMatching(teamMatchingId, fromTeamFormDTO);
        return ApiResponse.ok(dto);
    }

    @DeleteMapping("/{teamMatchingId}")
    public ApiResponse deleteTeamMatchingPage(@PathVariable("teamMatchingId") long teamMatchingId){
        // * 토큰을 이용하여 유저 아이디를 포함한 유저 정보를 가져온다.
        int userId = 1;

        TeamMatchings teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow();

        ToTeamFormDTO dto;
        // * 해당 게시글 등록 폼에 입력된 작성자 ID와 접속자 ID가 일치하는지 확인한다.
        if(teamMatchingEntity.isCorrespondTo(userId)){
            teamMatchingService.deleteTeamMatching(teamMatchingId, teamMatchingEntity);
        }else{
            throw new IllegalArgumentException("해당 글을 삭제할 권한이 없습니다.");
        }

        return ApiResponse.ok();
    }
}
