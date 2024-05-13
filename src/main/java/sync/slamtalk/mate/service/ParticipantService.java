package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.dto.response.ParticipantDto;
import sync.slamtalk.mate.dto.PositionListDto;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.error.MateErrorResponseCode;
import sync.slamtalk.mate.event.MateSupportAcceptanceEvent;
import sync.slamtalk.mate.event.MateSupportEvent;
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
    private final ApplicationEventPublisher eventPublisher;

    /*
    해당 글의 참여자 목록에 신청자를 등록한다.
    다음의 예외 상황을 가정하고 처리한다.
    1. 해당 글이 존재하지 않을 때
    2. 접속한 유저의 ID가 글 작성자의 ID와 일치할 때
    3. 해당 글이 삭제되었을 때
    4. 해당 글의 모집 상태가 모집 중이 아닐 때
    5. 해당 글의 작성자가 아닐 때
    6. 해당 글에 이미 참여자로 등록되어 있을 때(거절 당하거나 취소한 사람도 포함)
     */
    public ParticipantDto addParticipant(long matePostId, long participantId, ParticipantDto fromParticipantDto){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MateErrorResponseCode.MATE_POST_NOT_FOUND));

        User user = userRepository.findById(participantId).orElseThrow(()->new BaseException(NOT_FOUND_USER));
        String participantNickname = user.getNickname();

        if(post.isCorrespondToUser(participantId)){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(post.getParticipants().stream().anyMatch(participant -> participant.getParticipantId().equals(participantId))){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        // 모집중이 아닐 경우 신청을 못함
        if(!post.getRecruitmentStatus().equals(RecruitmentStatusType.RECRUITING)) {
            throw new BaseException(NOT_ALLOWED_TO_PARTICIPATE);
        }

        Participant participant = new Participant(participantId, participantNickname, fromParticipantDto.getPosition(),
                    fromParticipantDto.getSkillLevel(), post);

        Participant resultParticipant = participantRepository.save(participant);

        ParticipantDto resultdto = new ParticipantDto(resultParticipant);

        // 이벤트 발생
        eventPublisher.publishEvent(new MateSupportEvent(post, user.getNickname(), post.getWriterId()));

        return resultdto;

    }

    // 해당 글의 참여자 목록을 불러온다.
    // 참여자 목록은 참여자의 ID, 닉네임, 포지션, 실력, 신청 상태를 담고 있다.
    public List<ParticipantDto> getParticipants(long matePostId){
        MatePost matePost = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        List<Participant> participants = matePost.getParticipants();
        ArrayList<ParticipantDto> resultDtoList = new ArrayList<>();
        for(Participant participant : participants){
            if(participant.getIsDeleted()){
                continue;
            }
            ParticipantDto resultDto = new ParticipantDto(participant);
            resultDtoList.add(resultDto);
        }
        return resultDtoList;
    }


    // 모집 글 게시자가 참여자를 수락했을 때
    public ParticipantDto acceptParticipant(long matePostId, long participantTableId, long hostId){
        MatePost matePost = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MateErrorResponseCode.MATE_POST_NOT_FOUND));

        if(matePost.isCorrespondToUser(hostId) == false){ // 접근자가 게시글 작성자가 아닐 때
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(matePost.getRecruitmentStatus() != RecruitmentStatusType.RECRUITING) {
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }
        Participant participant = participantRepository.findById(participantTableId).orElseThrow(()->new BaseException(PARTICIPANT_NOT_FOUND));

        if(participant.getApplyStatus() != ApplyStatusType.WAITING){
            throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS); // 대기 중인(WAITING) 참여자가 아닐 때 상태를 변경할 수 없습니다.
        }

        List<PositionListDto> allowedPosition = entityToDtoMapper.toPositionListDto(matePost);
        List<String> allowedSkillLevel= matePost.toSkillLevelTypeList();

        if(!participant.checkCapabilities(allowedPosition, allowedSkillLevel)) { // 참여자의 포지션(그리고 참여 가능한 인원 수)와 실력이 모집글의 요구사항과 일치하지 않을 때
            throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
        }

        participant.updateApplyStatus(ApplyStatusType.ACCEPTED);
        matePost.increasePositionNumbers(participant.getPosition());

        //알림 발생
        eventPublisher.publishEvent(new MateSupportAcceptanceEvent(matePost, participant.getParticipantId()));
        return new ParticipantDto(participant);

    }


    public void cancelParticipant(long matePostId, long participantTableId, long writerId){
        Participant participant = participantRepository.findById(participantTableId).orElseThrow(()->new BaseException(PARTICIPANT_NOT_FOUND));

        MatePost matePost = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        if(participant.isCorrespondTo(writerId) == false){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(matePost.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            if(participant.getApplyStatus() == ApplyStatusType.WAITING){
                participant.disconnectParent();
                participantRepository.delete(participant);
            }else{
                throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
            }
        }else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }
    }

    // 모집 글 게시자가 참여자를 거절했을 때
    // 접속자와 ID와 모집 글 작성자의 ID가 일치하는지 확인한다.
    // 승낙 전 참여자를 거절하기 때문에 포지션별 인원의 변동은 없다.
    // 현재 참여자 목록에서 완전히 삭제 하지 않았지만 재신청이 가능하다면 참여자 목록에서 삭제(hard delete)하는 것이 맞다고 생각한다.
    public ParticipantDto rejectParticipant(long matePostId, long participantTableId, long hostId){
        MatePost matePost = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        Participant participant = participantRepository.findById(participantTableId).orElseThrow(()->new BaseException(PARTICIPANT_NOT_FOUND));

        if(matePost.isCorrespondToUser(hostId) == false){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }
        if(matePost.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            if(participant.getApplyStatus() == ApplyStatusType.WAITING) {  // 모집글 작성자는 대기 상태인 참여자만 거절할 수 있다.
                participant.updateApplyStatus(ApplyStatusType.REJECTED);
                return new ParticipantDto(participant);
            }else {
                    throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS); // 해당 참여자는 상태를 변경할 수 없습니다. (수락된 참여자, 취소한 참여자,거절된 참여자 포함)
                }
            } else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }
    }
}


/*
    * 추가 고려사항 : 참여자 목록에 지원할 수 있는 인원 수를 제한할지 여부
 */