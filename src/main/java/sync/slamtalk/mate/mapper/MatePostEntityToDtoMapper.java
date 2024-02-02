package sync.slamtalk.mate.mapper;

import lombok.NoArgsConstructor;
import sync.slamtalk.mate.dto.MatePostDTO;
import sync.slamtalk.mate.dto.PositionListDTO;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.PositionType;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class MatePostEntityToDtoMapper {

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
            skillLevelTypeList.add("입문");
        }else if(postSkillType == RecruitedSkillLevelType.OVER_BEGINNER) {
            skillLevelTypeList.add("입문");
            skillLevelTypeList.add("초보");
            skillLevelTypeList.add("중수");
            skillLevelTypeList.add("고수");
        }else if(postSkillType == RecruitedSkillLevelType.UNDER_LOW) {
            skillLevelTypeList.add("입문");
            skillLevelTypeList.add("초보");
        }else if(postSkillType == RecruitedSkillLevelType.OVER_LOW) {
            skillLevelTypeList.add("초보");
            skillLevelTypeList.add("중수");
            skillLevelTypeList.add("고수");
        }else if(postSkillType == RecruitedSkillLevelType.UNDER_MIDDLE) {
            skillLevelTypeList.add("입문");
            skillLevelTypeList.add("초보");
            skillLevelTypeList.add("중수");
        }else if(postSkillType == RecruitedSkillLevelType.OVER_MIDDLE) {
            skillLevelTypeList.add("중수");
            skillLevelTypeList.add("고수");
        }else if(postSkillType == RecruitedSkillLevelType.UNDER_HIGH) {
            skillLevelTypeList.add("입문");
            skillLevelTypeList.add("초보");
            skillLevelTypeList.add("중수");
            skillLevelTypeList.add("고수");
        }else if(postSkillType == RecruitedSkillLevelType.HIGH) {
            skillLevelTypeList.add("고수");
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
        matePostDTO.setMatePostId(matePost.getMatePostId());
        matePostDTO.setStartScheduledTime(matePost.getStartScheduledTime());
        matePostDTO.setEndScheduledTime(matePost.getEndScheduledTime());
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
