package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.dto.PositionListDto;
import sync.slamtalk.mate.dto.response.ParticipantDto;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.Participant;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.mate.repository.ParticipantRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

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
    2. 접속한 유저의 ID가 글 작성자의 ID와 일치할 때
    3. 해당 글이 삭제되었을 때
    4. 해당 글의 모집 상태가 모집 중이 아닐 때
    5. 해당 글의 작성자가 아닐 때
    6. 해당 글에 이미 참여자로 등록되어 있을 때(거절 당하거나 취소한 사람도 포함)
     */
    public ParticipantDto addParticipant(long matePostId, long participantId, ParticipantDto fromParticipantDto) {
        MatePost matePost = findMatePost(matePostId);

        User user = userRepository.findById(participantId).orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        String participantNickname = user.getNickname();

        if (matePost.isCorrespondToUser(participantId)) {
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if (matePost.getParticipants().stream().anyMatch(participant -> participant.getParticipantId().equals(participantId))) {
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if (matePost.getRecruitmentStatus() != RecruitmentStatusType.RECRUITING) {
            throw new BaseException(NOT_ALLOWED_TO_PARTICIPATE);
        }

        Participant participant = new Participant(participantId, participantNickname, fromParticipantDto.getPosition(),
                fromParticipantDto.getSkillLevel(), matePost);

        Participant resultParticipant = participantRepository.save(participant);
        return new ParticipantDto(resultParticipant);
    }

    // 해당 글의 참여자 목록을 불러온다.
    // 참여자 목록은 참여자의 ID, 닉네임, 포지션, 실력, 신청 상태를 담고 있다.
    @Transactional(readOnly = true)
    public List<ParticipantDto> getParticipants(long matePostId) {
        MatePost matePost = findMatePost(matePostId);

        return matePost.getParticipants()
                .stream()
                .filter(participant -> !participant.getIsDeleted())
                .map(ParticipantDto::new)
                .toList();
    }


    // 모집 글 게시자가 참여자를 수락했을 때
    public ParticipantDto acceptParticipant(long matePostId, long participantTableId, long hostId) {
        MatePost matePost = findMatePost(matePostId);
        Participant participant = findParticipant(participantTableId);

        if (!matePost.isCorrespondToUser(hostId)) { // 접근자가 게시글 작성자가 아닐 때
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if (matePost.getRecruitmentStatus() != RecruitmentStatusType.RECRUITING) {
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }

        if (participant.getApplyStatus() != ApplyStatusType.WAITING) {
            throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
        }

        List<PositionListDto> allowedPosition = entityToDtoMapper.toPositionListDto(matePost);
        List<String> allowedSkillLevel = matePost.toSkillLevelTypeList();

        if (!participant.checkCapabilities(allowedPosition, allowedSkillLevel)) { // 참여자의 포지션(그리고 참여 가능한 인원 수)와 실력이 모집글의 요구사항과 일치할 때
            throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
        }

        participant.updateApplyStatus(ApplyStatusType.ACCEPTED);
        matePost.increasePositionNumbers(participant.getPosition());
        return new ParticipantDto(participant);
    }


    public void cancelParticipant(long matePostId, long participantTableId, long writerId) {
        Participant participant = findParticipant(participantTableId);
        MatePost matePost = findMatePost(matePostId);

        if (!participant.isCorrespondTo(writerId)) {
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if (matePost.getRecruitmentStatus() != RecruitmentStatusType.RECRUITING) {
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }

        if (participant.getApplyStatus() != ApplyStatusType.WAITING) {
            throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
        }

        participant.disconnectParent();
        participantRepository.delete(participant);
    }

    // 모집 글 게시자가 참여자를 거절했을 때
    // 접속자와 ID와 모집 글 작성자의 ID가 일치하는지 확인한다.
    // 승낙 전 참여자를 거절하기 때문에 포지션별 인원의 변동은 없다.
    // 현재 참여자 목록에서 완전히 삭제 하지 않았지만 재신청이 가능하다면 참여자 목록에서 삭제(hard delete)하는 것이 맞다고 생각한다.
    public ParticipantDto rejectParticipant(long matePostId, long participantTableId, long hostId) {
        MatePost matePost = findMatePost(matePostId);
        Participant participant = findParticipant(participantTableId);

        if (!matePost.isCorrespondToUser(hostId)) {
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if (matePost.getRecruitmentStatus() != RecruitmentStatusType.RECRUITING) {
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }

        if (participant.getApplyStatus() != ApplyStatusType.WAITING) {
            throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS); // 해당 참여자는 상태를 변경할 수 없습니다. (수락된 참여자, 취소한 참여자,거절된 참여자 포함)
        }

        participant.updateApplyStatus(ApplyStatusType.REJECTED);
        return new ParticipantDto(participant);
    }

    private MatePost findMatePost(Long matePostId) {
        return matePostRepository.findById(matePostId).orElseThrow(() -> new BaseException(MATE_POST_NOT_FOUND));
    }

    private Participant findParticipant(Long participantTableId) {
        return participantRepository.findById(participantTableId).orElseThrow(() -> new BaseException(PARTICIPANT_NOT_FOUND));
    }
}