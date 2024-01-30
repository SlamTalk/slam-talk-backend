package sync.slamtalk.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.team.dto.FromTeamFormDTO;
import sync.slamtalk.team.dto.ToTeamFormDTO;
import sync.slamtalk.team.entity.TeamMatchings;
import sync.slamtalk.team.repository.TeamMatchingRepository;

import static sync.slamtalk.team.error.TeamErrorResponseCode.TEAM_POST_ALREADY_DELETED;
import static sync.slamtalk.team.error.TeamErrorResponseCode.TEAM_POST_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamMatchingService {
    private final TeamMatchingRepository teamMatchingRepository;

    public long registerTeamMatching(FromTeamFormDTO dto, long userId){
        TeamMatchings teamMatchingEntity = new TeamMatchings();
        teamMatchingEntity.createTeamMatching(dto, userId);
        TeamMatchings resultTeamMatchingEntity = teamMatchingRepository.save(teamMatchingEntity);
        return resultTeamMatchingEntity.getTeamMatchingId();
    }

    public ToTeamFormDTO getTeamMatching(long teamMatchingId){
        TeamMatchings teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        if(teamMatchingEntity.getIsDeleted()){
            throw new BaseException(TEAM_POST_ALREADY_DELETED);
        }
        ToTeamFormDTO dto = new ToTeamFormDTO();
        teamMatchingEntity.toTeamFormDto(dto);
        return dto;
    }

    public ApiResponse updateTeamMatching(long teamMatchingId, FromTeamFormDTO fromTeamFormDTO){
        TeamMatchings teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow();
        teamMatchingEntity.updateTeamMatching(fromTeamFormDTO);
        return ApiResponse.ok();
    }

    public ApiResponse deleteTeamMatching(long teamMatchingId, TeamMatchings teamMatchingEntity){
        teamMatchingEntity.delete();
        return ApiResponse.ok();
    }
}
