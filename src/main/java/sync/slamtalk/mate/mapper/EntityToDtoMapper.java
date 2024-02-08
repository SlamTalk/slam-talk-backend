package sync.slamtalk.mate.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import sync.slamtalk.mate.dto.MatePostDTO;
import sync.slamtalk.mate.dto.PositionListDTO;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.PositionType;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.SkillLevelType;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Component
public class EntityToDtoMapper {

    public static List<PositionListDTO> toPositionListDto(MatePost matePost){
        List<PositionListDTO> positionList = new ArrayList<>();

        if(matePost.getMaxParticipantsCenters() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.CENTER, matePost.getMaxParticipantsCenters(), matePost.getCurrentParticipantsCenters());
            positionList.add(positionListDTO);
        }
        if(matePost.getMaxParticipantsForwards() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.FORWARD, matePost.getMaxParticipantsForwards(), matePost.getCurrentParticipantsForwards());
            positionList.add(positionListDTO);
        }
        if(matePost.getMaxParticipantsGuards() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.GUARD, matePost.getMaxParticipantsGuards(), matePost.getCurrentParticipantsGuards());
            positionList.add(positionListDTO);
        }
        if(matePost.getMaxParticipantsOthers() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.UNSPECIFIED, matePost.getMaxParticipantsOthers(), matePost.getCurrentParticipantsOthers());
            positionList.add(positionListDTO);
        }

        return positionList;
    }

    public static List<String> toSkillLevelTypeList(RecruitedSkillLevelType postSkillType){
        List<String> skillLevelTypeList = new ArrayList<>();

        if(postSkillType == RecruitedSkillLevelType.BEGINNER){
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
        }else if(postSkillType == RecruitedSkillLevelType.OVER_BEGINNER) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }else if(postSkillType == RecruitedSkillLevelType.UNDER_LOW) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
        }else if(postSkillType == RecruitedSkillLevelType.OVER_LOW) {
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }else if(postSkillType == RecruitedSkillLevelType.UNDER_MIDDLE) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
        }else if(postSkillType == RecruitedSkillLevelType.OVER_MIDDLE) {
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }else if(postSkillType == RecruitedSkillLevelType.UNDER_HIGH) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }else if(postSkillType == RecruitedSkillLevelType.HIGH) {
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }

        return skillLevelTypeList;
    }

    public static void toPositionListDTO(MatePost matePost, MatePostDTO matePostDTO){
        if(matePost.getMaxParticipantsCenters() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.CENTER, matePost.getMaxParticipantsCenters(), matePost.getCurrentParticipantsCenters());
            matePostDTO.getPositionList().add(positionListDTO);
        }
        if(matePost.getMaxParticipantsForwards() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.FORWARD, matePost.getMaxParticipantsForwards(), matePost.getCurrentParticipantsForwards());
            matePostDTO.getPositionList().add(positionListDTO);
        }
        if(matePost.getMaxParticipantsGuards() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.GUARD, matePost.getMaxParticipantsGuards(), matePost.getCurrentParticipantsGuards());
            matePostDTO.getPositionList().add(positionListDTO);
        }
        if(matePost.getMaxParticipantsOthers() > 0){
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.UNSPECIFIED, matePost.getMaxParticipantsOthers(), matePost.getCurrentParticipantsOthers());
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
        matePostDTO.setSkillList(toSkillLevelTypeList(matePost.getSkillLevel()));
        matePostDTO.setPositionList(toPositionListDto(matePost));
        matePostDTO.setCreatedAt(matePost.getCreatedAt());
        return matePostDTO;
    }
}
