package sync.slamtalk.mate.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import sync.slamtalk.mate.dto.MatePostDTO;
import sync.slamtalk.mate.dto.PositionListDTO;
import sync.slamtalk.mate.entity.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Component
public class EntityToDtoMapper {

    public static List<PositionListDTO> toPositionListDto(MatePost matePost){
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

    public static List<String> toSkillLevelTypeList(Post post){
        List<String> skillLevelTypeList = new ArrayList<>();

        if(post.isSkillLevelBeginner()) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
        }

        if(post.isSkillLevelLow()) {
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
        }

        if(post.isSkillLevelMiddle()) {
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
        }

        if(post.isSkillLevelHigh()) {
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }


        return skillLevelTypeList;
    }
    public static SkillLevelList fromRecruitSkillLevel(RecruitedSkillLevelType skillLevel){
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
    public static void toPositionListDTO(MatePost matePost, MatePostDTO matePostDTO){
        if(matePost.getMaxParticipantsCenters() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.CENTER.getPosition(), matePost.getMaxParticipantsCenters(), matePost.getCurrentParticipantsCenters());
            matePostDTO.getPositionList().add(positionListDTO);
        }
        if(matePost.getMaxParticipantsForwards() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.FORWARD.getPosition(), matePost.getMaxParticipantsForwards(), matePost.getCurrentParticipantsForwards());
            matePostDTO.getPositionList().add(positionListDTO);
        }
        if(matePost.getMaxParticipantsGuards() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.GUARD.getPosition(), matePost.getMaxParticipantsGuards(), matePost.getCurrentParticipantsGuards());
            matePostDTO.getPositionList().add(positionListDTO);
        }
        if(matePost.getMaxParticipantsOthers() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.UNSPECIFIED.getPosition(), matePost.getMaxParticipantsOthers(), matePost.getCurrentParticipantsOthers());
            matePostDTO.getPositionList().add(positionListDTO);
        }
    }
    public static MatePostDTO toMatePostDto(MatePost matePost){
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
        toPositionListDTO(matePost, matePostDTO);
        matePostDTO.setSkillList(toSkillLevelTypeList(matePost));
        matePostDTO.setPositionList(toPositionListDto(matePost));
        matePostDTO.setCreatedAt(matePost.getCreatedAt());
        return matePostDTO;
    }
}
