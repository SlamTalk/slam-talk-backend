package sync.slamtalk.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.team.dto.FromTeamFormDTO;
import sync.slamtalk.team.dto.ToTeamFormDTO;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.team.repository.TeamMatchingRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static sync.slamtalk.team.error.TeamErrorResponseCode.TEAM_POST_ALREADY_DELETED;
import static sync.slamtalk.team.error.TeamErrorResponseCode.TEAM_POST_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamMatchingService {
    private final TeamMatchingRepository teamMatchingRepository;

    public long registerTeamMatching(FromTeamFormDTO dto, long userId){
        TeamMatching teamMatchingEntity = new TeamMatching();
        teamMatchingEntity.createTeamMatching(dto, userId);
        TeamMatching resultTeamMatchingEntity = teamMatchingRepository.save(teamMatchingEntity);
        return resultTeamMatchingEntity.getTeamMatchingId();
    }

    public ToTeamFormDTO getTeamMatching(long teamMatchingId){
        TeamMatching teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        if(teamMatchingEntity.getIsDeleted()){
            throw new BaseException(TEAM_POST_ALREADY_DELETED);
        }
        ToTeamFormDTO dto = new ToTeamFormDTO();
        teamMatchingEntity.toTeamFormDto(dto);
        return dto;
    }

    public ApiResponse updateTeamMatching(long teamMatchingId, FromTeamFormDTO fromTeamFormDTO){
        TeamMatching teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow();
        teamMatchingEntity.updateTeamMatching(fromTeamFormDTO);
        return ApiResponse.ok();
    }

    public ApiResponse deleteTeamMatching(long teamMatchingId, TeamMatching teamMatchingEntity){
        teamMatchingEntity.delete();
        return ApiResponse.ok();
    }

    public List<ToTeamFormDTO> getTeamMatchingList(int limit, String stringCursor){
        PageRequest request = PageRequest.of(0, limit);
        LocalDateTime cursor = LocalDateTime.parse(stringCursor, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        log.debug("[TeamMatchingService] cursor : {}, request : {}",cursor, request);
        long startTime = System.currentTimeMillis();
//        Page<Long> pages = teamMatchingRepository.findTeamMatchingIds(recruitStatus, cursorDateTime, request);
//        List<TeamMatching> result = teamMatchingRepository.findTeamMatchingsWithApplicants(pages.getContent());
        List<TeamMatching> result = teamMatchingRepository.findAllByCreatedAtBefore(cursor, request);
        log.debug("[TeamMatchingService] result : {}", result);
        long endTime = System.currentTimeMillis();
        log.debug("[TeamMatchingService] executed query time : {}", endTime - startTime);
        List<ToTeamFormDTO> dtoList = result.stream().map(teamMatchingEntity -> teamMatchingEntity.toTeamFormDto(new ToTeamFormDTO())).toList();
        return dtoList;
    }
}
