package sync.slamtalk.mate.mapper;

import com.querydsl.core.Tuple;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import sync.slamtalk.mate.dto.MatePostDTO;
import sync.slamtalk.mate.dto.MatePostToDto;
import sync.slamtalk.mate.dto.PositionListDTO;
import sync.slamtalk.mate.dto.UnrefinedMatePostDTO;
import sync.slamtalk.mate.entity.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Component
public class EntityToDtoMapper {

    public List<PositionListDTO> toPositionListDto(MatePost matePost){
        List<PositionListDTO> positionList = new ArrayList<>();

        if(matePost.getMaxParticipantsCenters() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.CENTER.getPosition(), matePost.getMaxParticipantsCenters(), matePost.getCurrentParticipantsCenters());
            positionList.add(positionListDTO);
        }
        if(matePost.getMaxParticipantsForwards() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.FORWARD.getPosition(), matePost.getMaxParticipantsForwards(), matePost.getCurrentParticipantsForwards());
            positionList.add(positionListDTO);
        }
        if(matePost.getMaxParticipantsGuards() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.GUARD.getPosition(), matePost.getMaxParticipantsGuards(), matePost.getCurrentParticipantsGuards());
            positionList.add(positionListDTO);
        }
        if(matePost.getMaxParticipantsOthers() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.UNSPECIFIED.getPosition(), matePost.getMaxParticipantsOthers(), matePost.getCurrentParticipantsOthers());
            positionList.add(positionListDTO);
        }

        return positionList;
    }

    public List<String> toSkillLevelTypeList(RecruitedSkillLevelType skillLevel){
        List<String> skillLevelTypeList = new ArrayList<>();

        if(skillLevel == RecruitedSkillLevelType.BEGINNER) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
        }
        if(skillLevel == RecruitedSkillLevelType.OVER_BEGINNER) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }
        if(skillLevel == RecruitedSkillLevelType.UNDER_LOW) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
        }
        if(skillLevel == RecruitedSkillLevelType.OVER_LOW) {
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }
        if(skillLevel == RecruitedSkillLevelType.UNDER_MIDDLE) {
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
        }
        if(skillLevel == RecruitedSkillLevelType.OVER_MIDDLE) {
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }
        if(skillLevel == RecruitedSkillLevelType.UNDER_HIGH) {
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
        }
        if(skillLevel == RecruitedSkillLevelType.HIGH) {
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }

        return skillLevelTypeList;
    }

    public SkillLevelList fromRecruitSkillLevel(RecruitedSkillLevelType skillLevel){
        SkillLevelList skillLevelList = new SkillLevelList();
        switch(skillLevel) {
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


    public MatePostDTO toMatePostDto(MatePost matePost){
        MatePostDTO matePostDTO = new MatePostDTO();
        matePostDTO.setWriterId(matePost.getWriterId());
        matePostDTO.setWriterNickname(matePost.getWriterNickname());
        matePostDTO.setMatePostId(matePost.getMatePostId());
        matePostDTO.setScheduledDate(matePost.getScheduledDate());
        matePostDTO.setStartTime(matePost.getStartTime());
        matePostDTO.setEndTime(matePost.getEndTime());
        matePostDTO.setTitle(matePost.getTitle());
        matePostDTO.setContent(matePost.getContent());
        matePostDTO.setRecruitmentStatus(matePost.getRecruitmentStatus());
        matePostDTO.setLocationDetail(matePost.getLocationDetail());
        matePostDTO.setParticipants(matePost.getParticipants());
        matePostDTO.setPositionList(toPositionListDto(matePost));
        matePostDTO.setSkillList(matePost.toSkillLevelTypeList());
        matePostDTO.setPositionList(toPositionListDto(matePost));
        matePostDTO.setCreatedAt(matePost.getCreatedAt());
        return matePostDTO;
    }

    public MatePostToDto FromUnrefinedToMatePostDto(UnrefinedMatePostDTO dto){
        List<PositionListDTO> positionList = new ArrayList<>();

        if(dto.getMaxParticipantsCenters() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.CENTER.getPosition(), dto.getMaxParticipantsCenters(), dto.getCurrentParticipantsCenters());
            positionList.add(positionListDTO);
        }
        if(dto.getMaxParticipantsForwards() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.FORWARD.getPosition(), dto.getMaxParticipantsForwards(), dto.getCurrentParticipantsForwards());
            positionList.add(positionListDTO);
        }
        if(dto.getMaxParticipantsGuards() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.GUARD.getPosition(), dto.getMaxParticipantsGuards(), dto.getCurrentParticipantsGuards());
            positionList.add(positionListDTO);
        }
        if(dto.getMaxParticipantsOthers() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.UNSPECIFIED.getPosition(), dto.getMaxParticipantsOthers(), dto.getCurrentParticipantsOthers());
            positionList.add(positionListDTO);
        }


        MatePostToDto resultDto = new MatePostToDto();
        resultDto.setSkillLevelList(toSkillLevelTypeList(dto.getSkillLevel()));
        resultDto.setWriterId(dto.getWriterId());
        resultDto.setWriterNickname(dto.getWriterNickname());
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
}
