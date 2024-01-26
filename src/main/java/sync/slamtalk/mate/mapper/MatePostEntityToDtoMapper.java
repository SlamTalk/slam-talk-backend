package sync.slamtalk.mate.mapper;

import lombok.NoArgsConstructor;
import sync.slamtalk.mate.dto.MatePostListDTO;
import sync.slamtalk.mate.dto.PositionListDTO;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.PositionType;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class MatePostEntityToDtoMapper {

    public List<PositionListDTO> toPositionListDto(MatePost matePost){
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

    public List<String> toSkillLevelTypeList(MatePost matePost){
        List<String> skillLevelTypeList = new ArrayList<>();

        if(matePost.getSkillLevel() == RecruitedSkillLevelType.BEGINNER){
            skillLevelTypeList.add("입문");
        }else if(matePost.getSkillLevel() == RecruitedSkillLevelType.OVER_BEGINNER) {
            skillLevelTypeList.add("입문");
            skillLevelTypeList.add("초보");
            skillLevelTypeList.add("중수");
            skillLevelTypeList.add("고수");
        }else if(matePost.getSkillLevel() == RecruitedSkillLevelType.UNDER_LOW) {
            skillLevelTypeList.add("입문");
            skillLevelTypeList.add("초보");
        }else if(matePost.getSkillLevel() == RecruitedSkillLevelType.OVER_LOW) {
            skillLevelTypeList.add("초보");
            skillLevelTypeList.add("중수");
            skillLevelTypeList.add("고수");
        }else if(matePost.getSkillLevel() == RecruitedSkillLevelType.UNDER_MIDDLE) {
            skillLevelTypeList.add("입문");
            skillLevelTypeList.add("초보");
            skillLevelTypeList.add("중수");
        }else if(matePost.getSkillLevel() == RecruitedSkillLevelType.OVER_MIDDLE) {
            skillLevelTypeList.add("중수");
            skillLevelTypeList.add("고수");
        }else if(matePost.getSkillLevel() == RecruitedSkillLevelType.UNDER_HIGH) {
            skillLevelTypeList.add("입문");
            skillLevelTypeList.add("초보");
            skillLevelTypeList.add("중수");
            skillLevelTypeList.add("고수");
        }else if(matePost.getSkillLevel() == RecruitedSkillLevelType.HIGH) {
            skillLevelTypeList.add("고수");
        }

        return skillLevelTypeList;
    }

    public MatePostListDTO toMatePostListDto(MatePost matePost){
        MatePostListDTO matePostListDTO = new MatePostListDTO();
        matePostListDTO.setMatePostId(matePost.getMatePostId());
        matePostListDTO.setWriterId(matePost.getWriterId());
       // matePostListDTO.setWriterNickname(matePost.getWriterNickname());
        matePostListDTO.setTitle(matePost.getTitle());
        matePostListDTO.setContent(matePost.getContent());
        matePostListDTO.setStartScheduledTime(matePost.getStartScheduledTime());
        matePostListDTO.setEndScheduledTime(matePost.getEndScheduledTime());
        matePostListDTO.setLocationDetail(matePost.getLocationDetail());
        matePostListDTO.setPositionList(toPositionListDto(matePost));
        matePostListDTO.setSkillList(toSkillLevelTypeList(matePost));
        matePostListDTO.setParticipants(matePost.getParticipants());

        return matePostListDTO;
    }
}
