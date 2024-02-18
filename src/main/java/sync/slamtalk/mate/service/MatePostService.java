package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.dto.*;
import sync.slamtalk.mate.dto.request.MatePostReq;
import sync.slamtalk.mate.dto.response.*;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.mate.repository.ParticipantRepository;
import sync.slamtalk.mate.repository.QueryRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static sync.slamtalk.mate.error.MateErrorResponseCode.*;
import static sync.slamtalk.user.error.UserErrorResponseCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatePostService {

    private final MatePostRepository matePostRepository;
    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final EntityToDtoMapper entityToDtoMapper;
    private final QueryRepository queryRepository;

    private static final int FIRST_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * Objective : 메이트찾기 게시글을 등록한다.
     * Flow :
     * 1. 매개변수로 입력 받은 userId로 userRepository를 조회하여 해당 User 객체를 가져온다.(없을 경우 예외 처리)
     * 2. MateFormDTO를 MatePost로 변환한다.
     * 3. MatePost를 저장한다.
     * 4. 저장된 게시글의 아이디를 반환한다.
     */
    public long registerMatePost(MatePostReq matePostReq, long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new BaseException(NOT_FOUND_USER));
        MatePost matePost = matePostReq.toEntity();
        matePost.connectParent(user);
        MatePost result = matePostRepository.save(matePost);
        return result.getMatePostId(); // * 저장된 게시글의 아이디를 반환한다.
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
    public MatePostRes getMatePost(long matePostId){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        if(post.getIsDeleted()){
            throw new BaseException(MATE_POST_ALREADY_DELETED);
        }

        MatePostRes matePostRes = getMatePostRes(matePostId, post);

        return matePostRes;
    }

    private MatePostRes getMatePostRes(long matePostId, MatePost post) {
        User writer = post.getWriter();
        Long writerId = writer.getId();
        String writerNickname = writer.getNickname();
        String writerImageUrl = writer.getImageUrl();

        List<ParticipantDto> participantsToArrayList = participantService.getParticipants(matePostId);
        List<String> skillList = post.toSkillLevelTypeList();
        List<PositionListDto> positionList = entityToDtoMapper.toPositionListDto(post);

        MatePostRes matePostRes = MatePostRes.builder()
                .matePostId(post.getMatePostId())
                .writerId(writerId)
                .writerNickname(writerNickname)
                .writerImageUrl(writerImageUrl)
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
                .build();
        return matePostRes;
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
    public boolean deleteMatePost(long matePostId, long userId){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));
        if(post.getIsDeleted()){
            throw new BaseException(MATE_POST_ALREADY_DELETED);
        }
        if(!(post.isCorrespondToUser(userId))){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }
        post.softDeleteMatePost();
        return true;
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
    public boolean updateMatePost(long matePostId, MatePostReq matePostReq, long userId){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        if(post.getIsDeleted()){
            throw new BaseException(MATE_POST_ALREADY_DELETED);
        }
        if(post.isCorrespondToUser(userId) == false) {
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        EntityToDtoMapper entityToDtoMapper = new EntityToDtoMapper();

        String[] splitedLocationString = matePostReq.getLocationDetail().split(" ", 2);
        String location = splitedLocationString[0];
        String locationDetail = splitedLocationString[1].length() > 0 ? splitedLocationString[1] : "";
        LocalDate scheduledDate = matePostReq.getScheduledDate();
        LocalTime startTime = matePostReq.getStartTime();
        LocalTime endTime = matePostReq.getEndTime();
        SkillLevelList skillList = entityToDtoMapper.fromRecruitSkillLevel(matePostReq.getSkillLevel());

        Integer maxParticipantsCenters = matePostReq.getMaxParticipantsCenters();
        Integer maxParticipantsGuards = matePostReq.getMaxParticipantsGuards();
        Integer maxParticipantsForwards = matePostReq.getMaxParticipantsForwards();
        Integer maxParticipantsOthers = matePostReq.getMaxParticipantsOthers();

        if(maxParticipantsCenters != null){
            if(post.getCurrentParticipantsCenters() > maxParticipantsCenters){
                throw new BaseException(EXCEED_OR_UNDER_LIMITED_NUMBER);
            }
            post.updateMaxParticipantsCenters(maxParticipantsCenters);
        }

        if(maxParticipantsGuards != null){
            if(post.getCurrentParticipantsGuards() > maxParticipantsGuards){
                throw new BaseException(EXCEED_OR_UNDER_LIMITED_NUMBER);
            }
            post.updateMaxParticipantsGuards(maxParticipantsGuards);
        }

        if(maxParticipantsForwards != null){
            if(post.getCurrentParticipantsForwards() > maxParticipantsForwards){
                throw new BaseException(EXCEED_OR_UNDER_LIMITED_NUMBER);
            }
            post.updateMaxParticipantsForwards(maxParticipantsForwards);
        }

        if(maxParticipantsOthers != null){
            if(post.getCurrentParticipantsOthers() > maxParticipantsOthers){
                throw new BaseException(EXCEED_OR_UNDER_LIMITED_NUMBER);
            }
            post.updateMaxParticipantsOthers(maxParticipantsOthers);
        }

            post.updateContent(matePostReq.getContent());
            post.updateTitle(matePostReq.getTitle());
            post.updateLocation(location);
            post.updateLocationDetail(locationDetail);
            post.updateScheduledDate(scheduledDate);
            post.updateStartTime(startTime);
            post.updateEndTime(endTime);
            post.configureSkillLevel(skillList);

        return true;
    }

    /**
    Objective : 커서 페이징 방식으로 메이트찾기 글 목록을 조회한다.
    Flow :
        1. 커서를 이용하여 글 목록을 조회한다.
        2. 조회된 글 목록을 DTO로 변환하여 반환한다.
     */
    @Transactional(readOnly = true)
    public MatePostListDto getMatePostsByCurser(MateSearchCondition condition){
        log.debug("condition: {}", condition);
        List<UnrefinedMatePostDto> listedMatePosts = queryRepository.findMatePostList(condition);

        log.debug("listedMatePosts: {}", listedMatePosts);
        List<MatePostToDto> refinedDto = listedMatePosts.stream().map(dto -> new EntityToDtoMapper().fromUnrefinedToMatePostDto(dto)).collect(Collectors.toList());
        List<MatePostToDto> result = refinedDto.stream().map(dto -> {
                    List<ParticipantDto> refined = queryRepository.findParticipantByMatePostId(dto.getMatePostId());
                     dto.setParticipants(refined);
                        return dto;
                }
        ).toList();
        MatePostListDto response = new MatePostListDto();
        response.setMatePostList(result);
        if(listedMatePosts.isEmpty() == false) {
            response.setNextCursor(listedMatePosts.get(listedMatePosts.size() - 1).getCreatedAt().toString());
        }
        return response;
    }

    /**
    Objective : 해당 메이트찾기 글의 모집을 완료한다.
    Flow :
        1. 매개변수로 입력 받은 글 ID를 이용하여 해당 글을 찾는다. (글이 존재하지 않을 경우 예외 처리)
        2. 글 작성자가 맞는지 확인한다. (글 작성자가 아닐 경우 예외 처리)
        3. 글의 모집 상태가 모집 중인지 확인한다. (모집 중이 아닐 경우 예외 처리)
        3-1. 모집 중이라면 참여자 목록을 불러온다.
        3-2. 참여자 목록이 비어있다면 예외 처리한다.
        3-3. 참여자 목록을 순회하며 수락되지 않은 참여자들을 데이터베이스에서 삭제한다. (hard delete)
        4. 글의 모집 상태를 완료로 변경한다.
     */
    public MatePostRes completeRecruitment(long matePostId, long userId) {
        MatePost post = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        if(!post.isCorrespondToUser(userId)){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(post.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            List<Participant> participants = post.getParticipants();
            if(participants.isEmpty()){
                throw new BaseException(NO_ACCEPTED_PARTICIPANT);
            }

            List<Long> usersId = new ArrayList<>();
            for(Participant participant : participants){
                if(participant.getApplyStatus() == ApplyStatusType.ACCEPTED){
                    usersId.add(participant.getParticipantId());
                }else{
                    participant.disconnectParent();
                    participantRepository.delete(participant); // * 수락되지 않은 참여자들은 데이터베이스에서 삭제한다.(hard delete)
                }
            }

            post.updateRecruitmentStatus(RecruitmentStatusType.COMPLETED);
            return getMatePostRes(matePostId, post);
        }else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }

    }

    /**
     * 내가 쓴 글 또는 내가 참여한 모든 Mate List를 반환하는 메서드
     *
     * @param userId : 유저 아이디
     * @return MyMateListRes : 내가 쓴 글/ 참여한 글 반환
     * */
    public MyMateListRes getMyMateList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        List<MatePost> allByWriter = matePostRepository.findAllByWriter(user);
        List<MatePost> allByApplications = matePostRepository.findAllByApplicationId(userId);

        List<MatePostToDto> authoredPost = new ArrayList<>(allByWriter.stream()
                .map(matePost -> entityToDtoMapper.FromMatePostToMatePostDto(matePost))
                .toList());

        List<MatePostToDto> participatedPost = new ArrayList<>(allByApplications.stream()
                .map(matePost -> entityToDtoMapper.FromMatePostToMatePostDto(matePost))
                .map(matePostToDto -> {
                    matePostToDto.setParticipants(
                            matePostToDto.getParticipants().stream()
                                    .filter(participant -> participant.getParticipantId().equals(userId))
                                    .toList()
                    );
                    return matePostToDto;
                })
                .toList());

        for (List<MatePostToDto> toTeamFormDTOS : Arrays.asList(authoredPost, participatedPost)) {
            Collections.sort(toTeamFormDTOS, (o1, o2) -> {
                if (o1.getScheduledDate().isEqual(o2.getScheduledDate())) {
                    return o1.getStartTime().compareTo(o2.getStartTime());
                }
                return o1.getScheduledDate().compareTo(o2.getScheduledDate());
            });
        }

        return new MyMateListRes(authoredPost, participatedPost);
    }
}
