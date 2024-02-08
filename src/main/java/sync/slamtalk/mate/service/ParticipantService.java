package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.dto.MatePostApplicantDTO;
import sync.slamtalk.mate.dto.PositionListDTO;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.error.MateErrorResponseCode;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.mate.repository.ParticipantRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.util.ArrayList;
import java.util.List;

import static sync.slamtalk.mate.error.MateErrorResponseCode.*;
import static sync.slamtalk.user.error.UserErrorResponseCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ParticipantService {

    private final MatePostRepository matePostRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final EntityToDtoMapper entityToDtoMapper;

    /*
    해당 글의 참여자 목록에 신청자를 등록한다.
    다음의 예외 상황을 가정하고 처리한다.
    1. 해당 글이 존재하지 않을 때
    2. 접속한 유저의 ID가 존재하지 않을 때
    3. 해당 글이 삭제되었을 때
    4. 해당 글의 모집 상태가 모집 중이 아닐 때
    5. 해당 글의 작성자가 아닐 때
    6. 해당 글에 이미 참여자로 등록되어 있을 때(거절 당하거나 취소한 사람도 포함)
     */
    public MatePostApplicantDTO addParticipant(long matePostId, long participantId, MatePostApplicantDTO matePostApplicantDTO){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MateErrorResponseCode.MATE_POST_NOT_FOUND));

        User user = userRepository.findById(participantId).orElseThrow(()->new BaseException(NOT_FOUND_USER));
        String participantNickname = user.getNickname();

        if(!post.isCorrespondToUser(participantId)){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(post.getParticipants().stream().anyMatch(participant -> participant.getParticipantId().equals(participantId))){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(post.getRecruitmentStatus().equals(RecruitmentStatusType.RECRUITING)){
            Participant participant = new Participant(participantId, participantNickname, matePostApplicantDTO.getPosition(),
                    matePostApplicantDTO.getSkillLevel());
            participant.connectParent(post);
            Participant resultParticipant = participantRepository.save(participant);

            MatePostApplicantDTO resultParticipantDTO = new MatePostApplicantDTO(resultParticipant.getParticipantTableId(), resultParticipant.getApplyStatus());
            return resultParticipantDTO;
        } else {
            throw new BaseException(NOT_ALLOWED_TO_PARTICIPATE);
        }

    }

    // 해당 글의 참여자 목록을 불러온다.
    // 참여자 목록은 참여자의 ID, 닉네임, 포지션, 실력, 신청 상태를 담고 있다.
    public List<MatePostApplicantDTO> getParticipants(long matePostId){
        MatePost matePost = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        List<Participant> participants = matePost.getParticipants();
        ArrayList<MatePostApplicantDTO> matePostApplicantDTOs = new ArrayList<>();
        for(Participant participant : participants){ // 참여자 목록에서 취소하였거나 거절 당한 참여자도 포함한다. * 수정 가능
//            if(participant.getApplyStatus() == ApplyStatusType.REJECTED || participant.getApplyStatus() == ApplyStatusType.CANCEL){
//                continue;
//            }
            if(participant.getIsDeleted()){
                continue;
            }
            MatePostApplicantDTO matePostApplicantDTO = new MatePostApplicantDTO(participant.getParticipantTableId(), participant.getParticipantId(), participant.getParticipantNickname(), participant.getPosition(), participant.getSkillLevel(), participant.getApplyStatus());
            matePostApplicantDTOs.add(matePostApplicantDTO);
            log.debug("참여자 목록 : {}", matePostApplicantDTO);
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
        MatePost matePost = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MateErrorResponseCode.MATE_POST_NOT_FOUND));

        if(matePost.isCorrespondToUser(hostId) == false){ // 접근자가 게시글 작성자가 아닐 때
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(matePost.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            Participant participant = participantRepository.findById(participantTableId).orElseThrow(()->new BaseException(PARTICIPANT_NOT_FOUND));

            if(participant.getApplyStatus() == ApplyStatusType.WAITING){
                List<PositionListDTO> allowedPosition = entityToDtoMapper.toPositionListDto(matePost);
                List<String> allowedSkillLevel= entityToDtoMapper.toSkillLevelTypeList(matePost.getSkillLevel());
                if(participant.checkCapabilities(allowedPosition, allowedSkillLevel)) { // 참여자의 포지션(그리고 참여 가능한 인원 수)와 실력이 모집글의 요구사항과 일치할 때
                    participant.updateApplyStatus(ApplyStatusType.ACCEPTED);
                    matePost.increasePositionNumbers(participant.getPosition());
                }else{
                    throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
                }
            }else{
                throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS); // 대기 중인(WAITING) 참여자가 아닐 때 상태를 변경할 수 없습니다.
            }
        }else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }

        return ApiResponse.ok();
    }




    // 참여자가 취소를 선택했을 때
    // 접속자 ID와 해당 신청자 ID가 일치하는지 확인한다.
    // 참여자 목록의 해당 정보를 CANCEL로 변경한다.(연관관계는 그대로, softDelete는 false)
    // 참여자가 취소를 선택했을 때 참여자 목록에서 삭제하는 것이 아니라 취소 상태로 변경하는 이유는 완전히 삭제되지 않는다면 연결된 정보는 남겨놔야 할거 같아서
    // 취소를 눌러도 포지션별 인원의 변동은 없다.
    // todo : 현재 참여자 목록에서 완전히 삭제 하지 않았지만 재신청이 가능하다면 참여자 목록에서 삭제(hard delete)하는 것이 맞다고 생각한다.
    public ApiResponse cancelParticipant(long matePostId, long participantTableId, long writerId){
        Participant participant = participantRepository.findById(participantTableId).orElseThrow(()->new BaseException(PARTICIPANT_NOT_FOUND));

        MatePost matePost = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        if(participant.isCorrespondTo(writerId) == false){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(matePost.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            if(participant.getApplyStatus() == ApplyStatusType.WAITING){
                participant.updateApplyStatus(ApplyStatusType.CANCELED);
            }else if(participant.getApplyStatus() == ApplyStatusType.ACCEPTED){ // 이미 수락된 참여자가 취소할 경우 해당 포지션의 모집 인원 수를 감소 시킵니다.
                matePost.reducePositionNumbers(participant.getPosition());
                participant.updateApplyStatus(ApplyStatusType.CANCELED);
            }else{
                throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
            }
            return ApiResponse.ok();
        }else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }
    }

    // 모집 글 게시자가 참여자를 거절했을 때
    // 접속자와 ID와 모집 글 작성자의 ID가 일치하는지 확인한다.
    // 승낙 전 참여자를 거절하기 때문에 포지션별 인원의 변동은 없다.
    // 현재 참여자 목록에서 완전히 삭제 하지 않았지만 재신청이 가능하다면 참여자 목록에서 삭제(hard delete)하는 것이 맞다고 생각한다.
    public ApiResponse rejectParticipant(long matePostId, long participantTableId, long hostId){
        MatePost matePost = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        Participant participant = participantRepository.findById(participantTableId).orElseThrow(()->new BaseException(PARTICIPANT_NOT_FOUND));

        if(matePost.isCorrespondToUser(hostId) == false){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }
        if(matePost.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            if(participant.getApplyStatus() == ApplyStatusType.WAITING) {  // 모집글 작성자는 대기 상태인 참여자만 거절할 수 있다.
                participant.updateApplyStatus(ApplyStatusType.REJECTED);
            }else {
                    throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS); // 해당 참여자는 상태를 변경할 수 없습니다. (수락된 참여자, 취소한 참여자,거절된 참여자 포함)
                }
            } else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }
        return ApiResponse.ok();

    }
}


/*
    * 추가 고려사항 : 참여자 목록에 지원할 수 있는 인원 수를 제한할지 여부
 */