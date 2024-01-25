package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.dto.MatePostApplicantDTO;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.error.MateErrorResponseCode;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.mate.repository.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static sync.slamtalk.mate.error.MateErrorResponseCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {

    private final MatePostRepository matePostRepository;
    private final ParticipantRepository participantRepository;

    //
    //todo : post가 null이면 어떤 값을 반환할 지 결정해야 한다.
    public MatePostApplicantDTO addParticipant(long matePostId, long participantId, String participantNIckname, MatePostApplicantDTO matePostApplicantDTO){
        Optional<MatePost> post = matePostRepository.findById(matePostId);
        if(!post.isPresent()){
            throw new BaseException(MateErrorResponseCode.MATE_POST_NOT_FOUND);
        }
        Participant participant = new Participant(participantId,
                participantNIckname, matePostApplicantDTO.getPosition(),
                matePostApplicantDTO.getSkillLevel());
        participant.connectParent(post.get());
        Participant resultParticipant = participantRepository.save(participant);

        MatePostApplicantDTO resultParticipantDTO = new MatePostApplicantDTO(resultParticipant.getParticipantTableId(), resultParticipant.getApplyStatus());
        return resultParticipantDTO;
    }

    // 해당 글의 참여자 목록을 불러온다.
    // 참여자 목록은 참여자의 ID, 닉네임, 포지션, 실력, 신청 상태를 담고 있다.
    public List<MatePostApplicantDTO> getParticipants(long matePostId){
        Optional<MatePost> optionalMatePost = matePostRepository.findById(matePostId);
        if(!optionalMatePost.isPresent()){
            throw new BaseException(MateErrorResponseCode.MATE_POST_NOT_FOUND);
        }
        MatePost post = optionalMatePost.get();
        List<Participant> participants = post.getParticipants();
        ArrayList<MatePostApplicantDTO> matePostApplicantDTOs = new ArrayList<>();
        for(Participant participant : participants){ // 참여자 목록에서 취소하였거나 거절 당한 참여자도 포함한다. * 수정 가능
//            if(participant.getApplyStatus() == ApplyStatusType.REJECTED || participant.getApplyStatus() == ApplyStatusType.CANCEL){
//                continue;
//            }
            MatePostApplicantDTO matePostApplicantDTO = new MatePostApplicantDTO(participant.getParticipantTableId(), participant.getParticipantNickname(), participant.getPosition(), participant.getSkillLevel(), participant.getApplyStatus());
            matePostApplicantDTOs.add(matePostApplicantDTO);
        }
        return matePostApplicantDTOs;
    }

    /**
     *참여자의 신청 상태를 변경한다.
     *참여자의 신청 상태는 ACCEPTED, REJECTED, CANCEL, WAITING(신청 시 기본 상태)이 있다.
     *ACCEPTED : 모집 글 게시자가 참여자(AWAITING)를 수락했을 때
     *REJECTED : 모집 글 게시자가 참여자(AWAITING)를 거절했을 때
     *CANCEL : 참여자(AWAITING)가 취소를 선택했을 때
     */

    // 모집 글 게시자가 참여자를 수락했을 때
    public ApiResponse acceptParticipant(long matePostId, long participantTableId, long hostId){
        Optional<MatePost> OptionalMatePost = matePostRepository.findById(matePostId);
        if(!OptionalMatePost.isPresent()){
            throw new BaseException(MateErrorResponseCode.MATE_POST_NOT_FOUND);
        }
        MatePost matePost = OptionalMatePost.get();
        if(matePost.getWriterId() != hostId){ // 접근자가 게시글 작성자가 아닐 때
            return ApiResponse.fail(USER_NOT_AUTHORIZED);
        }else{
            Optional<Participant> optionalParticipant = participantRepository.findById(participantTableId);
            Participant participant;
            try{
                participant = optionalParticipant.get();
            }catch(Exception e){
                throw new BaseException(PARTICIPANT_NOT_FOUND);
            }

            if(participant.getApplyStatus() == ApplyStatusType.WAITING){
                participant.updateApplyStatus(ApplyStatusType.ACCEPTED);
                matePost.increasePositionNumbers(participant.getPosition());
            }else{
                return ApiResponse.fail(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS); // 대기 중인(WAITING) 참여자가 아닐 때 상태를 변경할 수 없습니다.
            }
        }
        return ApiResponse.ok();
    }




    // 참여자가 취소를 선택했을 때
    // 접속자 ID와 해당 신청자 ID가 일치하는지 확인한다.
    // 참여자 목록의 해당 정보를 CANCEL로 변경한다.(연관관계는 그대로, softDelete는 false)
    // 참여자가 취소를 선택했을 때 참여자 목록에서 삭제하는 것이 아니라 취소 상태로 변경하는 이유는 완전히 삭제되지 않는다면 연결된 정보는 남겨놔야 할거 같아서
    // 취소를 눌러도 포지션별 인원의 변동은 없다.
    // todo : userId가 String으로 바뀐다면 == 이 아닌 equals()를 사용해야 한다.
    // todo : 현재 참여자 목록에서 완전히 삭제 하지 않았지만 재신청이 가능하다면 참여자 목록에서 삭제(hard delete)하는 것이 맞다고 생각한다.
    public ApiResponse cancelParticipant(long matePostId, long participantTableId, long writerId){
        Optional<Participant> OptionalParticipant = participantRepository.findById(participantTableId);
        Participant participant;
        try{
            participant = OptionalParticipant.get();
        }catch(Exception e){
            throw new BaseException(PARTICIPANT_NOT_FOUND);
        }
        Optional<MatePost> optionalMatePost = matePostRepository.findById(matePostId);
        MatePost matePost;
        try{
            matePost = optionalMatePost.get();
        }catch(Exception e){
            throw new BaseException(MATE_POST_NOT_FOUND);
        }

        if(!(participant.getParticipantId() == writerId)){
            return ApiResponse.fail(USER_NOT_AUTHORIZED);
        } else{
            if(participant.getApplyStatus().equals(ApplyStatusType.WAITING)){
                participant.updateApplyStatus(ApplyStatusType.CANCEL);
            }else if(participant.getApplyStatus().equals(ApplyStatusType.ACCEPTED)){ // 이미 수락된 참여자가 취소할 경우 해당 포지션의 모집 인원 수를 감소 시킵니다.
                participant.updateApplyStatus(ApplyStatusType.CANCEL);
                matePost.reducePositionNumbers(participant.getPosition());
            }else{
                return ApiResponse.fail(PARTICIPANT_ALREADY_REJECTED);
            }
            return ApiResponse.ok();
        }
    }

    // 모집 글 게시자가 참여자를 거절했을 때
    // 접속자와 ID와 모집 글 작성자의 ID가 일치하는지 확인한다.
    // 승낙 전 참여자를 거절하기 때문에 포지션별 인원의 변동은 없다.
    // todo : 수락 전 대기 상태인 신청자를 모집 글의 포지션 별 현재 모집 인원 수에 반영해야 할지 논의해야 한다.
    // todo : 현재 참여자 목록에서 완전히 삭제 하지 않았지만 재신청이 가능하다면 참여자 목록에서 삭제(hard delete)하는 것이 맞다고 생각한다.
    public ApiResponse rejectParticipant(long matePostId, long participantTableId, long hostId){
        Optional<MatePost> optionalMatePost = matePostRepository.findById(matePostId);
        if(!optionalMatePost.isPresent()){
            throw new BaseException(MATE_POST_NOT_FOUND);
        }
        MatePost matePost = optionalMatePost.get();
        Optional<Participant> optionalParticipant = participantRepository.findById(participantTableId);
        Participant participant;
        try{
            participant = optionalParticipant.get();
        }catch(Exception e){
            throw new BaseException(PARTICIPANT_NOT_FOUND);
        }
        if(!(matePost.getWriterId() == hostId)){
            return ApiResponse.fail(USER_NOT_AUTHORIZED);
        } else {
            if(participant.getApplyStatus().equals(ApplyStatusType.WAITING)) {  // 모집글 작성자는 대기 상태인 참여자만 거절할 수 있다.
                participant.updateApplyStatus(ApplyStatusType.REJECTED);
            }else{
                return ApiResponse.fail(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS); // 해당 참여자는 상태를 변경할 수 없습니다.
            }
        }
        return ApiResponse.ok();

    }
}
