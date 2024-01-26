package sync.slamtalk.mate.mapper;

import sync.slamtalk.mate.dto.PositionListDTO;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.PositionType;

import java.util.ArrayList;
import java.util.List;

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
}
