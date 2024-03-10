package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.dto.MateSearchCondition;
import sync.slamtalk.mate.dto.PositionListDto;
import sync.slamtalk.mate.dto.UnrefinedMatePostDto;
import sync.slamtalk.mate.dto.request.MatePostReq;
import sync.slamtalk.mate.dto.response.*;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.mate.repository.QueryRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import static sync.slamtalk.mate.error.MateErrorResponseCode.*;
import static sync.slamtalk.user.error.UserErrorResponseCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatePostService {

    private final MatePostRepository matePostRepository;
    private final ParticipantService participantService;
    private final UserRepository userRepository;
    private final EntityToDtoMapper entityToDtoMapper;
    private final QueryRepository queryRepository;

    private static final Comparator<MatePostToDto> COMPARE_BY_SCHEDULED_DATE = (o1, o2) -> {
        if (o1.getScheduledDate().isEqual(o2.getScheduledDate())) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
        return o1.getScheduledDate().compareTo(o2.getScheduledDate());
    };

    /**
     * Objective : 메이트찾기 게시글을 등록한다.
     * Flow :
     * 1. 매개변수로 입력 받은 userId로 userRepository를 조회하여 해당 User 객체를 가져온다.(없을 경우 예외 처리)
     * 2. MateFormDTO를 MatePost로 변환한다.
     * 3. MatePost를 저장한다.
     * 4. 저장된 게시글의 아이디를 반환한다.
     */
    public long registerMatePost(MatePostReq matePostReq, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        MatePost matePost = matePostReq.toEntity();
        matePost.connectParent(user);
        MatePost result = matePostRepository.save(matePost);
        return result.getId(); // * 저장된 게시글의 아이디를 반환한다.
    }

    /**
     * Objective : matePostId로 원하는 메이트찾기 글을 조회한다.
     * Flow :
     * 1. 게시글 ID를 이용해서 저장된 게시글을 불러온다.(없을 경우 예외 처리)
     * 2. 게시글이 삭제된 게시글인지 확인한다.(삭제된 게시글일 경우 예외 처리)
     * 3. 게시글의 작성자 정보를 불러온다.(작성자(User) Id와 닉네임, 없을 경우 예외 처리)
     * 4. 해당 글의 참여자 목록을 불러와서 Dto로 변환한다.
     * 5. 게시글의 정보를 Dto로 변환하여 반환한다.
     */
    @Transactional(readOnly = true)
    public MatePostRes getMatePost(long matePostId) {
        MatePost post = findMatePost(matePostId);

        if (post.getIsDeleted()) {
            throw new BaseException(MATE_POST_ALREADY_DELETED);
        }

        return getMatePostRes(matePostId, post);
    }

    private MatePostRes getMatePostRes(long matePostId, MatePost post) {
        User writer = post.getWriter();

        List<ParticipantDto> participantsToArrayList = participantService.getParticipants(matePostId);
        List<String> skillList = post.toSkillLevelTypeList();
        List<PositionListDto> positionList = entityToDtoMapper.toPositionListDto(post);

        return MatePostRes.builder()
                .matePostId(post.getId())
                .writerId(writer.getId())
                .writerNickname(writer.getNickname())
                .writerImageUrl(writer.getImageUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .scheduledDate(post.getScheduledDate())
                .startTime(post.getStartTime())
                .endTime(post.getEndTime())
                .locationDetail(post.getLocation() + " " + post.getLocationDetail())
                .skillLevel(post.getSkillLevel())
                .skillLevelList(skillList)
                .recruitmentStatus(post.getRecruitmentStatus())
                .positionList(positionList)
                .participants(participantsToArrayList)
                .createdAt(post.getCreatedAt())
                .build();
    }

    /**
     * Objective : 메이트찾기 글을 삭제한다.
     * Flow :
     * 1. 매개변수로 입력 받은 글 ID를 이용하여 해당 글을 찾는다. (글이 존재하지 않을 경우 예외 처리)
     * 2. 삭제(soft delete)된 글인지 확인한다. (삭제된 글일 경우 예외 처리)
     * 3. 글 작성자가 맞는지 확인한다. (글 작성자가 아닐 경우 예외 처리)
     * 4. 해당 게시글을 soft delete 한다.
     * 4-1. 해당 게시글에 속한 참여자 목록도 soft delete 한다.
     * 4-2. 해당 게시글에 속한 참여자를 순회하며 CANCELED 상태로 변경한다.
     * Note :
     * 채팅방 관련 기능은 추후에 구현할 수도 있다. (e.g 채팅방 삭제 등)
     */
    public void deleteMatePost(long matePostId, long userId) {
        MatePost matePost = findMatePost(matePostId);

        if (matePost.getIsDeleted()) {
            throw new BaseException(MATE_POST_ALREADY_DELETED);
        }

        if (!matePost.isCorrespondToUser(userId)) {
            throw new BaseException(USER_NOT_AUTHORIZED);
        }
        matePost.softDeleteMatePost();
    }

    /**
     * Objective : 메이트찾기 글을 수정한다.
     * Flow :
     * 1. 매개변수로 입력 받은 글 ID를 이용하여 해당 글을 찾는다. (글이 존재하지 않을 경우 예외 처리)
     * 2. 삭제(soft delete)된 글인지 확인한다. (삭제된 글일 경우 예외 처리)
     * 3. 글 작성자가 맞는지 확인한다. (글 작성자가 아닐 경우 예외 처리)
     * 4. 수정 가능한 항목 : 제목, 내용, 예정된 시간, 상세 시합 장소, 스킬 레벨, 모집 포지션 별 최대 인원 수
     * Note :
     * 지금은 dto로 넘어오는 존재하는 필드와 값만 수정하거나 업데이트 하도록 구현되어 있다.
     */
    public void updateMatePost(long matePostId, MatePostReq matePostReq, long userId) {
        MatePost matePost = findMatePost(matePostId);

        if (matePost.getIsDeleted()) {
            throw new BaseException(MATE_POST_ALREADY_DELETED);
        }

        if (!matePost.isCorrespondToUser(userId)) {
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if (matePost.getRecruitmentStatus() == RecruitmentStatusType.COMPLETED) {
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }

        String[] splitedLocationString = matePostReq.getLocationDetail().split(" ", 2);
        String location = splitedLocationString[0];
        String locationDetail = splitedLocationString.length > 1 ? splitedLocationString[1] : "";
        LocalDate scheduledDate = matePostReq.getScheduledDate();
        LocalTime startTime = matePostReq.getStartTime();
        LocalTime endTime = matePostReq.getEndTime();
        SkillLevelList skillList = entityToDtoMapper.fromRecruitSkillLevel(matePostReq.getSkillLevel());

        matePost.updateContent(matePostReq.getContent());
        matePost.updateTitle(matePostReq.getTitle());
        matePost.updateLocation(location);
        matePost.updateLocationDetail(locationDetail);
        matePost.updateScheduledDate(scheduledDate);
        matePost.updateStartTime(startTime);
        matePost.updateEndTime(endTime);
        matePost.configureSkillLevel(skillList);
    }

    /**
     * Objective : 커서 페이징 방식으로 메이트찾기 글 목록을 조회한다.
     * Flow :
     * 1. 커서를 이용하여 글 목록을 조회한다.
     * 2. 조회된 글 목록을 DTO로 변환하여 반환한다.
     */
    @Transactional(readOnly = true)
    public MatePostListDto getMatePostsByCursor(MateSearchCondition condition) {
        log.debug("condition: {}", condition);
        List<UnrefinedMatePostDto> listedMatePosts = queryRepository.findMatePostList(condition);

        log.debug("listedMatePosts: {}", listedMatePosts);
        List<MatePostToDto> result = listedMatePosts.stream()
                .map(entityToDtoMapper::fromUnrefinedToMatePostDto)
                .map(this::setParticipants)
                .toList();

        MatePostListDto response = new MatePostListDto();
        response.setMatePostList(result);

        if (!listedMatePosts.isEmpty()) {
            response.setNextCursor(listedMatePosts.get(listedMatePosts.size() - 1).getCreatedAt().toString());
        }

        return response;
    }

    private MatePostToDto setParticipants(MatePostToDto dto) {
        dto.setParticipants(queryRepository.findParticipantByMatePostId(dto.getMatePostId()));
        return dto;
    }

    /**
     * Objective : 해당 메이트찾기 글의 모집을 완료한다.
     * Flow :
     * 1. 매개변수로 입력 받은 글 ID를 이용하여 해당 글을 찾는다. (글이 존재하지 않을 경우 예외 처리)
     * 2. 글 작성자가 맞는지 확인한다. (글 작성자가 아닐 경우 예외 처리)
     * 3. 글의 모집 상태가 모집 중인지 확인한다. (모집 중이 아닐 경우 예외 처리)
     * 3-1. 모집 중이라면 참여자 목록을 불러온다.
     * 3-2. 참여자 목록이 비어있다면 예외 처리한다.
     * 3-3. 참여자 목록을 순회하며 수락되지 않은 참여자들을 데이터베이스에서 삭제한다. (hard delete)
     * 4. 글의 모집 상태를 완료로 변경한다.
     */
    public List<ParticipantDto> completeRecruitment(long matePostId, long userId) {
        MatePost matePost = findMatePost(matePostId);
        List<Participant> participants = matePost.getParticipants();

        if (!matePost.isCorrespondToUser(userId)) {
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if (matePost.getRecruitmentStatus() != RecruitmentStatusType.RECRUITING) {
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }

        boolean isNotAccepted = participants.stream()
                .noneMatch(participant -> participant.getApplyStatus() == ApplyStatusType.ACCEPTED);

        if (participants.isEmpty() || isNotAccepted) {
            throw new BaseException(NO_ACCEPTED_PARTICIPANT);
        }

        participants.stream()
                .filter(participant -> participant.getApplyStatus() != ApplyStatusType.ACCEPTED)
                .forEach(Participant::softDeleteParticipant);

        matePost.updateRecruitmentStatus(RecruitmentStatusType.COMPLETED);
        return participantService.getParticipants(matePost.getId());
    }

    /**
     * 내가 쓴 글 또는 내가 참여한 모든 Mate List를 반환하는 메서드
     *
     * @param userId : 유저 아이디
     * @return MyMateListRes : 내가 쓴 글/ 참여한 글 반환
     */
    public MyMateListRes getMyMateList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        List<MatePostToDto> authoredPost = findAuthoredPosts(user);

        List<MatePost> allByApplications = matePostRepository.findAllByApplicationId(userId);

        List<MatePostToDto> participatedPost = allByApplications.stream()
                .map(entityToDtoMapper::fromMatePostToMatePostDto)
                .map(matePostToDto -> {
                    matePostToDto.setParticipants(
                            matePostToDto.getParticipants().stream()
                                    .filter(participant -> participant.getParticipantId().equals(userId))
                                    .toList()
                    );
                    return matePostToDto;
                })
                .sorted(COMPARE_BY_SCHEDULED_DATE)
                .toList();

        return new MyMateListRes(authoredPost, participatedPost);
    }

    private List<MatePostToDto> findAuthoredPosts(User writer) {
        return matePostRepository.findAllByWriterAndIsDeletedFalse(writer)
                .stream()
                .map(entityToDtoMapper::fromMatePostToMatePostDto)
                .sorted(COMPARE_BY_SCHEDULED_DATE)
                .toList();
    }

    private MatePost findMatePost(Long matePostId) {
        return matePostRepository.findById(matePostId).orElseThrow(() -> new BaseException(MATE_POST_NOT_FOUND));
    }

}
