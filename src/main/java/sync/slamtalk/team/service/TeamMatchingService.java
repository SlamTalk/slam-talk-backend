package sync.slamtalk.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.team.TeamMapper;
import sync.slamtalk.team.dto.FromTeamFormDTO;
import sync.slamtalk.team.dto.ToTeamFormDTO;
import sync.slamtalk.team.entity.TeamMatchings;
import sync.slamtalk.team.repository.TeamMatchingRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMatchingService {
    TeamMatchingRepository teamMatchingRepository;
    TeamMapper teamMapper;

    public long registerTeamMatching(TeamMatchings teamMatching){
        TeamMatchings teamMatchingEntity = teamMatchingRepository.save(teamMatching);
        return teamMatchingEntity.getTeamMatchingId();
    }

    public ToTeamFormDTO getTeamMatching(long teamMatchingId){
        TeamMatchings teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow();
        return teamMapper.toTeamFormDTO(teamMatchingEntity);
    }

    public ToTeamFormDTO updateTeamMatching(long teamMatchingId, FromTeamFormDTO fromTeamFormDTO){
        TeamMatchings teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow();
        teamMapper.updateToTeamMatching(fromTeamFormDTO, teamMatchingEntity);
        return teamMapper.toTeamFormDTO(teamMatchingEntity);
    }

    public void deleteTeamMatching(long teamMatchingId, TeamMatchings teamMatchingEntity){
        teamMatchingEntity.delete();
        teamMatchingRepository.deleteById(teamMatchingId);
    }
}
