package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.dto.MateFormDTO;
import sync.slamtalk.mate.dto.MatePostApplicantDTO;
import sync.slamtalk.mate.dto.MatePostDTO;
import sync.slamtalk.mate.dto.PositionListDTO;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.mapper.MatePostEntityToDtoMapper;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.mate.repository.ParticipantRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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

    /*
     * objective : 메이트찾기 게시글을 등록한다.
     * flow :
     * 1. 매개변수로 입력 받은 userId로 userRepository를 조회하여 해당 User 객체를 가져온다.
     * 2. MateFormDTO를 MatePost로 변환한다.
     * 3. MatePost를 저장한다.
     * 4. 저장된 게시글의 아이디를 반환한다.
     */
    public long registerMatePost(MateFormDTO mateFormDTO, long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new BaseException(NOT_FOUND_USER));
        MatePost matePost = mateFormDTO.toEntity(user);
        MatePost result = matePostRepository.save(matePost);
        return result.getMatePostId(); // * 저장된 게시글의 아이디를 반환한다.
    }

    /*
     * objective : matePostId로 원하는 메이트찾기 글을 조회한다.
     * flow :
     * 1. 게시글 ID를 이용해서 저장된 게시글을 불러온다.(없을 경우 예외 처리)
     * 2. 게시글이 삭제된 게시글인지 확인한다.(삭제된 게시글일 경우 예외 처리)
     * 3. 게시글의 작성자 정보를 불러온다.(작성자(User) Id와 닉네임, 없을 경우 예외 처리)
     * 4. 해당 글의 참여자 목록을 불러와서 Dto로 변환한다.
     * 5. 게시글의 정보를 Dto로 변환하여 반환한다.
     */
    public MateFormDTO getMatePost(long matePostId){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        if(post.getIsDeleted()){
            throw new BaseException(MATE_POST_ALREADY_DELETED);
        }

        User writer = post.getWriter();
        if(writer == null){
            throw new BaseException(NOT_FOUND_USER);
        }
        Long writerId = writer.getId();
        String writerNickname = writer.getNickname();

        List<MatePostApplicantDTO> participantsToArrayList = participantService.getParticipants(matePostId);
        MatePostEntityToDtoMapper mapper = new MatePostEntityToDtoMapper();
        List<String> skillList = mapper.toSkillLevelTypeList(post.getSkillLevel());
        List<PositionListDTO> positionList = mapper.toPositionListDto(post);

        MateFormDTO mateFormDTO = MateFormDTO.builder()
                .matePostId(post.getMatePostId())
                .writerId(writerId)
                .writerNickname(writerNickname)
                .title(post.getTitle())
                .content(post.getContent())
                .scheduledDate(post.getScheduledDate())
                .startTime(post.getStartTime())
                .endTime(post.getEndTime())
                .locationDetail(post.getLocationDetail())
                .skillLevelList(skillList)
                .recruitmentStatus(post.getRecruitmentStatus())
                .positionList(positionList)
                .participants(participantsToArrayList)
                .build();
        return mateFormDTO;
    }

    /*
     * objective : 메이트찾기 글을 삭제한다.
     * flow :
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

    /*
     * objective : 메이트찾기 글을 수정한다.
     * flow :
     * 1. 매개변수로 입력 받은 글 ID를 이용하여 해당 글을 찾는다. (글이 존재하지 않을 경우 예외 처리)
     * 2. 삭제(soft delete)된 글인지 확인한다. (삭제된 글일 경우 예외 처리)
     * 3. 글 작성자가 맞는지 확인한다. (글 작성자가 아닐 경우 예외 처리)
     * 4. 수정 가능한 항목 : 제목, 내용, 예정된 시간, 상세 시합 장소, 스킬 레벨, 모집 포지션 별 최대 인원 수
     * Note :
     * 지금은 dto로 넘어오는 존재하는 필드와 값만 수정하거나 업데이트 하도록 구현되어 있다.
     */
    public boolean updateMatePost(long matePostId, MateFormDTO mateFormDTO, long userId){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        if(post.getIsDeleted()){
            throw new BaseException(MATE_POST_ALREADY_DELETED);
        }
        if(!post.isCorrespondToUser(userId)) {
            throw new BaseException(USER_NOT_AUTHORIZED);
        }
        String content = mateFormDTO.getContent();
        String title = mateFormDTO.getTitle();
        String locationDetail = mateFormDTO.getLocationDetail();
        LocalDate scheduledDate = mateFormDTO.getScheduledDate();
        LocalTime startTime = mateFormDTO.getStartTime();
        LocalTime endTime = mateFormDTO.getEndTime();
        RecruitedSkillLevelType skillLevel = mateFormDTO.getSkillLevel();
        Integer maxParticipantsCenters = mateFormDTO.getMaxParticipantsCenters();
        Integer maxParticipantsGuards = mateFormDTO.getMaxParticipantsGuards();
        Integer maxParticipantsForwards = mateFormDTO.getMaxParticipantsForwards();
        Integer maxParticipantsOthers = mateFormDTO.getMaxParticipantsOthers();

        if(content != null && !content.equals("")){ // * 내용이 비어있지 않다면
            post.updateContent(content);
        }

        if(title != null && !title.equals("")){ // * 제목이 비어있지 않다면
            post.updateTitle(title);
        }

        if(locationDetail != null && !locationDetail.equals("")){ // * 상세 시합 장소가 비어있지 않다면
            post.updateLocationDetail(locationDetail);
        }


        if(scheduledDate != null){
            post.updateScheduledDate(scheduledDate);
        }
        if(startTime != null){
            post.updateStartTime(startTime);
        }
        if(endTime != null){
            post.updateEndTime(endTime);
        }

        if(skillLevel != null){
            post.updateSkillLevel(skillLevel);
        }

        if(maxParticipantsCenters != null){
            if(post.getCurrentParticipantsCenters() > maxParticipantsCenters){
                throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
            }
            post.updateMaxParticipantsCenters(maxParticipantsCenters);
        }

        if(maxParticipantsGuards != null){
            if(post.getCurrentParticipantsGuards() > maxParticipantsGuards){
                throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
            }
            post.updateMaxParticipantsGuards(maxParticipantsGuards);
        }

        if(maxParticipantsForwards != null){
            if(post.getCurrentParticipantsForwards() > maxParticipantsForwards){
                throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
            }
            post.updateMaxParticipantsForwards(maxParticipantsForwards);
        }

        if(maxParticipantsOthers != null){
            if(post.getCurrentParticipantsOthers() > maxParticipantsOthers){
                throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
            }
            post.updateMaxParticipantsOthers(maxParticipantsOthers);
        }

        return true;
    }

    /*
    objective : 커서 페이징 방식으로 메이트찾기 글 목록을 조회한다.
    flow :
        1. 커서를 이용하여 글 목록을 조회한다.
        2. 조회된 글 목록을 DTO로 변환하여 반환한다.
     */
    public List<MatePostDTO> getMatePostsByCurser(String cursorStr, int limit) {
        LocalDateTime cursor = LocalDateTime.parse(cursorStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        log.debug("cursor: {}", cursor);
        Pageable pageable = PageRequest.of(0, limit);
        List<MatePost> listedMatePosts = matePostRepository.findByCreatedAtLessThanAndIsDeletedNotOrderByCreatedAtDesc(cursor, true, pageable);
        log.debug("listedMatePosts: {}", listedMatePosts);
        List<MatePostDTO> response = listedMatePosts.stream().map(MatePostEntityToDtoMapper::toMatePostDto).collect(Collectors.toList());
        return response;
    }

    /*
    objective : 해당 메이트찾기 글의 모집을 완료한다.
    flow :
        1. 매개변수로 입력 받은 글 ID를 이용하여 해당 글을 찾는다. (글이 존재하지 않을 경우 예외 처리)
        2. 글 작성자가 맞는지 확인한다. (글 작성자가 아닐 경우 예외 처리)
        3. 글의 모집 상태가 모집 중인지 확인한다. (모집 중이 아닐 경우 예외 처리)
        3-1. 모집 중이라면 참여자 목록을 불러온다.
        3-2. 참여자 목록이 비어있다면 예외 처리한다.
        3-3. 참여자 목록이 비어있지 않다면 참여자 목록을 순회하며 수락된 참여자들에게 채팅방을 개설하고 채팅방 ID 정보를 포함한 알림을 보낸다. (필요 시 구현)
        3-4. 참여자 목록을 순회하며 수락되지 않은 참여자들을 데이터베이스에서 삭제한다. (hard delete)
        4. 글의 모집 상태를 완료로 변경한다.
     Note :
        채팅방 관련 기능은 추후에 구현할 수도 있다. (e.g 채팅방 개설, 채팅방 ID 정보를 포함한 알림 보내기 등)
     */
    public void completeRecruitment(long matePostId, long userId) {
        MatePost post = matePostRepository.findById(matePostId).orElseThrow(()->new BaseException(MATE_POST_NOT_FOUND));

        if(!post.isCorrespondToUser(userId)){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(post.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            List<Participant> participants = post.getParticipants();
            if(participants.isEmpty()){
                throw new BaseException(NO_ACCEPTED_PARTICIPANT);
            }

            for(Participant participant : participants){
                if(participant.getApplyStatus() == ApplyStatusType.ACCEPTED){
                    // todo? : 채팅방을 개설하고 승락된 참여자들에게 채팅방 ID 정보를 포함한 알림을 보낸다. (필요 시 구현)
                }else{
                    participant.disconnectParent();
                    participantRepository.delete(participant); // * 수락되지 않은 참여자들은 데이터베이스에서 삭제한다.(hard delete)
                }
            }
            post.updateRecruitmentStatus(RecruitmentStatusType.COMPLETED);
        }else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }

    }

    // * 글 작성자가 모집을 취소할 때
    // * 현재로썬 구현을 안 하는 방향으로 정했지만 추후에 구현할 수도 있음
    public void cancelRecruitment(long matePostId, long userId) {
    }
}
