package sync.slamtalk.mate.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import sync.slamtalk.mate.dto.PositionListDto;
import sync.slamtalk.mate.dto.UnrefinedMatePostDto;
import sync.slamtalk.mate.dto.response.MatePostToDto;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.team.dto.ToTeamFormDTO;
import sync.slamtalk.team.dto.UnrefinedTeamMatchingDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Component
public class EntityToDtoMapper {

    public List<PositionListDto> toPositionListDto(MatePost matePost) {
        List<PositionListDto> positionList = new ArrayList<>();

        if (matePost.getMaxParticipantsCenters() > 0 || matePost.getCurrentParticipantsCenters() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.CENTER.getPosition(), matePost.getMaxParticipantsCenters(), matePost.getCurrentParticipantsCenters());
            positionList.add(positionListDTO);
        }
        if (matePost.getMaxParticipantsForwards() > 0 || matePost.getCurrentParticipantsForwards() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.FORWARD.getPosition(), matePost.getMaxParticipantsForwards(), matePost.getCurrentParticipantsForwards());
            positionList.add(positionListDTO);
        }
        if (matePost.getMaxParticipantsGuards() > 0 || matePost.getCurrentParticipantsGuards() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.GUARD.getPosition(), matePost.getMaxParticipantsGuards(), matePost.getCurrentParticipantsGuards());
            positionList.add(positionListDTO);
        }
        if (matePost.getMaxParticipantsOthers() > 0 || matePost.getCurrentParticipantsOthers() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.UNSPECIFIED.getPosition(), matePost.getMaxParticipantsOthers(), matePost.getCurrentParticipantsOthers());
            positionList.add(positionListDTO);
        }

        return positionList;
    }

    public List<String> toSkillLevelTypeList(RecruitedSkillLevelType skillLevel) {
        List<String> skillLevelTypeList = new ArrayList<>();

        if (skillLevel == RecruitedSkillLevelType.BEGINNER) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
        }
        if (skillLevel == RecruitedSkillLevelType.OVER_BEGINNER) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }
        if (skillLevel == RecruitedSkillLevelType.UNDER_LOW) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
        }
        if (skillLevel == RecruitedSkillLevelType.OVER_LOW) {
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }
        if (skillLevel == RecruitedSkillLevelType.UNDER_MIDDLE) {
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
        }
        if (skillLevel == RecruitedSkillLevelType.OVER_MIDDLE) {
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }
        if (skillLevel == RecruitedSkillLevelType.UNDER_HIGH) {
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
        }
        if (skillLevel == RecruitedSkillLevelType.HIGH) {
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }

        return skillLevelTypeList;
    }

    public SkillLevelList fromRecruitSkillLevel(RecruitedSkillLevelType skillLevel) {
        SkillLevelList skillLevelList = new SkillLevelList();
        switch (skillLevel) {
            case HIGH:
                skillLevelList.setSkillLevelHigh(true);
                break;
            case UNDER_HIGH:
                skillLevelList.setSkillLevelHigh(true);
                skillLevelList.setSkillLevelMiddle(true);
                skillLevelList.setSkillLevelLow(true);
                skillLevelList.setSkillLevelBeginner(true);
                break;
            case OVER_MIDDLE:
                skillLevelList.setSkillLevelHigh(true);
                skillLevelList.setSkillLevelMiddle(true);
                break;
            case UNDER_MIDDLE:
                skillLevelList.setSkillLevelMiddle(true);
                skillLevelList.setSkillLevelLow(true);
                skillLevelList.setSkillLevelBeginner(true);
                break;
            case OVER_LOW:
                skillLevelList.setSkillLevelHigh(true);
                skillLevelList.setSkillLevelMiddle(true);
                skillLevelList.setSkillLevelLow(true);
                break;
            case UNDER_LOW:
                skillLevelList.setSkillLevelLow(true);
                skillLevelList.setSkillLevelBeginner(true);
                break;
            case OVER_BEGINNER:
                skillLevelList.setSkillLevelHigh(true);
                skillLevelList.setSkillLevelMiddle(true);
                skillLevelList.setSkillLevelLow(true);
                skillLevelList.setSkillLevelBeginner(true);
                break;
            case BEGINNER:
                skillLevelList.setSkillLevelBeginner(true);
                break;
        }
        return skillLevelList;
    }


    public MatePostToDto fromUnrefinedToMatePostDto(UnrefinedMatePostDto dto) {
        List<PositionListDto> positionList = new ArrayList<>();

        if (dto.getMaxParticipantsCenters() > 0 || dto.getCurrentParticipantsCenters() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.CENTER.getPosition(), dto.getMaxParticipantsCenters(), dto.getCurrentParticipantsCenters());
            positionList.add(positionListDTO);
        }
        if (dto.getMaxParticipantsForwards() > 0 || dto.getCurrentParticipantsForwards() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.FORWARD.getPosition(), dto.getMaxParticipantsForwards(), dto.getCurrentParticipantsForwards());
            positionList.add(positionListDTO);
        }
        if (dto.getMaxParticipantsGuards() > 0 || dto.getCurrentParticipantsGuards() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.GUARD.getPosition(), dto.getMaxParticipantsGuards(), dto.getCurrentParticipantsGuards());
            positionList.add(positionListDTO);
        }
        if (dto.getMaxParticipantsOthers() > 0 || dto.getCurrentParticipantsOthers() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.UNSPECIFIED.getPosition(), dto.getMaxParticipantsOthers(), dto.getCurrentParticipantsOthers());
            positionList.add(positionListDTO);
        }


        MatePostToDto resultDto = new MatePostToDto();
        resultDto.setSkillLevelList(toSkillLevelTypeList(dto.getSkillLevel()));
        resultDto.setWriterId(dto.getWriterId());
        resultDto.setWriterNickname(dto.getWriterNickname());
        resultDto.setWriterImageUrl(dto.getImageUrl());
        resultDto.setMatePostId(dto.getMatePostId());
        resultDto.setScheduledDate(dto.getScheduledDate());
        resultDto.setStartTime(dto.getStartTime());
        resultDto.setEndTime(dto.getEndTime());
        resultDto.setTitle(dto.getTitle());
        resultDto.setContent(dto.getContent());
        resultDto.setSkillLevel(dto.getSkillLevel());
        resultDto.setRecruitmentStatus(dto.getRecruitmentStatus());
        resultDto.setLocationDetail(dto.getLocation() + " " + dto.getLocationDetail());
        resultDto.setPositionList(positionList);
        resultDto.setCreatedAt(dto.getCreatedAt());

        return resultDto;
    }

    /**
     * MatePost 에서 MatePostToDto로 변환하는 Mapper 매서드
     *
     * @param matePost : entity
     * @return MatePostToDto : response dto
     */
    public MatePostToDto FromMatePostToMatePostDto(MatePost matePost) {
        List<PositionListDto> positionList = new ArrayList<>();

        if (matePost.getMaxParticipantsCenters() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.CENTER.getPosition(), matePost.getMaxParticipantsCenters(), matePost.getCurrentParticipantsCenters());
            positionList.add(positionListDTO);
        }
        if (matePost.getMaxParticipantsForwards() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.FORWARD.getPosition(), matePost.getMaxParticipantsForwards(), matePost.getCurrentParticipantsForwards());
            positionList.add(positionListDTO);
        }
        if (matePost.getMaxParticipantsGuards() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.GUARD.getPosition(), matePost.getMaxParticipantsGuards(), matePost.getCurrentParticipantsGuards());
            positionList.add(positionListDTO);
        }
        if (matePost.getMaxParticipantsOthers() > 0) {
            PositionListDto positionListDTO = new PositionListDto(PositionType.UNSPECIFIED.getPosition(), matePost.getMaxParticipantsOthers(), matePost.getCurrentParticipantsOthers());
            positionList.add(positionListDTO);
        }

        MatePostToDto resultDto = new MatePostToDto();
        resultDto.setSkillLevelList(toSkillLevelTypeList(matePost.getSkillLevel()));
        resultDto.setWriterId(matePost.getWriterId());
        resultDto.setWriterNickname(matePost.getWriterNickname());
        resultDto.setMatePostId(matePost.getId());
        resultDto.setScheduledDate(matePost.getScheduledDate());
        resultDto.setStartTime(matePost.getStartTime());
        resultDto.setEndTime(matePost.getEndTime());
        resultDto.setTitle(matePost.getTitle());
        resultDto.setContent(matePost.getContent());
        resultDto.setSkillLevel(matePost.getSkillLevel());
        resultDto.setRecruitmentStatus(matePost.getRecruitmentStatus());
        resultDto.setLocationDetail(matePost.getLocation() + " " + matePost.getLocationDetail());
        resultDto.setPositionList(positionList);
        resultDto.setCreatedAt(matePost.getCreatedAt());

        return resultDto;
    }

    public ToTeamFormDTO fromUnrefinedTeamMatchingToDto(UnrefinedTeamMatchingDto inputDto) {
        ToTeamFormDTO resultDto = new ToTeamFormDTO();
        resultDto.setTeamMatchingId(inputDto.getTeamMatchingId());
        resultDto.setTeamName(inputDto.getTeamName());
        resultDto.setTitle(inputDto.getTitle());
        resultDto.setContent(inputDto.getContent());
        resultDto.setWriterId(inputDto.getWriterId());
        resultDto.setWriterNickname(inputDto.getWriterNickname());
        resultDto.setWriterImageUrl(inputDto.getWriterImageUrl());
        resultDto.setScheduledDate(inputDto.getScheduledDate());
        resultDto.setStartTime(inputDto.getStartTime());
        resultDto.setEndTime(inputDto.getEndTime());
        resultDto.setLocationDetail(inputDto.getLocation() + " " + inputDto.getLocationDetail());
        resultDto.setNumberOfMembers(inputDto.getNumberOfMembers());
        resultDto.setSkillLevel(inputDto.getSkillLevel());
        resultDto.setSkillLevelList(toSkillLevelTypeList(inputDto.getSkillLevel()));
        resultDto.setCreatedAt(inputDto.getCreatedAt());
        resultDto.setRecruitmentStatusType(inputDto.getRecruitmentStatus());

        return resultDto;
    }

}
