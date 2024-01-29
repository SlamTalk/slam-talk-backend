package sync.slamtalk.team;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import sync.slamtalk.team.dto.FromTeamFormDTO;
import sync.slamtalk.team.dto.ToTeamFormDTO;
import sync.slamtalk.team.entity.TeamMatchings;

@Mapper
@Component
public interface TeamMapper {
    TeamMapper INSTANCE = Mappers.getMapper(TeamMapper.class);

    @Mapping(target = "recruitmentStatus", constant = "RECRUITING")
    TeamMatchings createToTeamMatching(FromTeamFormDTO dto);

    void updateToTeamMatching(FromTeamFormDTO dto, @MappingTarget TeamMatchings teamMatching);


    @Mapping(source = "createdAt", target = "createdTime")
    ToTeamFormDTO toTeamFormDTO(TeamMatchings teamMatching);
}
