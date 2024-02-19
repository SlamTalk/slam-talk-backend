package sync.slamtalk.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;
import sync.slamtalk.team.dto.FromApplicantDto;
import sync.slamtalk.team.dto.FromTeamFormDTO;
import sync.slamtalk.team.dto.ToTeamFormDTO;
import sync.slamtalk.team.dto.response.MyTeamMatchingListRes;
import sync.slamtalk.team.entity.TeamApplicant;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.team.repository.TeamApplicantRepository;
import sync.slamtalk.team.repository.TeamMatchingRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static sync.slamtalk.mate.error.MateErrorResponseCode.*;
import static sync.slamtalk.team.error.TeamErrorResponseCode.*;
import static sync.slamtalk.user.error.UserErrorResponseCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamMatchingService {
    private final TeamMatchingRepository teamMatchingRepository;
    private final TeamApplicantRepository teamApplicantRepository;
    private final UserRepository userRepository;
    private final EntityToDtoMapper entityToDtoMapper;

    private static final int FIRST_PAGE = 0;
    /**
     * Objective : 팀 매칭 글을 등록하는 메소드 입니다.
     * Flow :
     * 1. 접속한 유저 객체를 꺼내온다.(없을 경우 BaseException을 발생시킨다.)
     * 2. FromTeamFormDTO를 TeamMatching 객체로 변환하여 저장한다.
     */
    public long registerTeamMatching(FromTeamFormDTO dto, long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        log.debug("[TeamMatchingService] user : {}", user.getTeamMatchings());
        TeamMatching teamMatchingEntity = new TeamMatching();
        teamMatchingEntity.createTeamMatching(dto, user);
        TeamMatching resultTeamMatchingEntity = teamMatchingRepository.save(teamMatchingEntity);
        return resultTeamMatchingEntity.getTeamMatchingId();
    }

    /**
     * Objective : 팀 매칭 글을 조회하는 메소드 입니다.
     * Flow :
     * 1. teamMatchingId를 가진 글을 조회한다.(없을 경우 BaseException을 발생시킨다.)
     * 2. 조회된 글이 삭제되었을 경우 BaseException을 발생시킨다.
     * 3. 조회된 글을 ToTeamFormDTO로 변환하여 반환한다.
     * Note :
     *
     */
    @Transactional(readOnly = true)
    public ToTeamFormDTO getTeamMatching(long teamMatchingId){
        TeamMatching teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        if(teamMatchingEntity.getIsDeleted()){
            throw new BaseException(TEAM_POST_ALREADY_DELETED);
        }
        ToTeamFormDTO dto = new ToTeamFormDTO();
        teamMatchingEntity.toTeamFormDto(dto);
        return dto;
    }

    /**
     * Objective : 팀 매칭 글을 수정하는 메소드 입니다.
     * Flow :
     * 1. teamMatchingId를 가진 글을 조회한다.(없을 경우 BaseException을 발생시킨다.)
     * 2. 조회된 글이 삭제되었을 경우 BaseException을 발생시킨다.
     * 3. 글 작성자와 접속자가 같은지 확인한다.(다를 경우 BaseException을 발생시킨다.)
     * 4. 글을 수정한다.
     */
    public ApiResponse updateTeamMatching(Long teamMatchingId, FromTeamFormDTO fromTeamFormDTO, Long userId){
        TeamMatching teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        if(teamMatchingEntity.getIsDeleted()){
            throw new BaseException(TEAM_POST_ALREADY_DELETED);
        }
        if(teamMatchingEntity.getWriter().getId() != userId){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }
        teamMatchingEntity.updateTeamMatching(fromTeamFormDTO);
        return ApiResponse.ok();
    }

    /**
     * Objective : 팀 매칭 글을 삭제하는 메소드 입니다.
     * Flow :
     * 1. teamMatchingId를 가진 글을 조회한다.(없을 경우 BaseException을 발생시킨다.)
     * 2. 조회된 글이 이미 삭제되었을 경우 BaseException을 발생시킨다.
     * 3. 글 작성자와 접속자가 같은지 확인한다.(다를 경우 BaseException을 발생시킨다.)
     * 4. 글을 삭제한다.
     */
    public ApiResponse deleteTeamMatching(long teamMatchingId, Long userId){
        TeamMatching teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        if(teamMatchingEntity.getIsDeleted()){
            throw new BaseException(TEAM_POST_ALREADY_DELETED);
        }
        if(teamMatchingEntity.isCorrespondTo(userId) == false){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }else{
            teamMatchingEntity.delete();
        }
        return ApiResponse.ok();
    }

    /**
     * Objective : 팀 매칭 목록 조회를 위한 메소드 입니다. 최신 등록일자 순으로 조회합니다.
     * Flow:
     * 1. PageRequest를 생성합니다.
     * 2. stringCursor를 받아서 LocalDateTime 타입으로 변환합니다.
     * 3. LocalDateTime 타입의 cursor와 PageRequest 객체를 이용하여 쿼리를 실행합니다.
     * 4. 쿼리 실행 시간을 로그로 남깁니다.
     * 5. 조회된 결과를 ToTeamFormDTO 타입의 속성을 가진 리스트로 변환하여 반환합니다.
     * Note :
     * 커서 페이징 방식으로 구현 하였고 다음의 인자를 받습니다.
     * limit : 한번에 가져올 목록의 개수
     * stringCursor : 커서로 사용할 날짜시간 문자열 입니다. 이 값은 LocalDateTime.parse를 통해 LocalDateTime으로 변환됩니다.
     * log.debug를 통해 쿼리가 실행된 시간을 확인할 수 있습니다.
     * ToTeamFormDTO 타입의 리스트를 반환합니다.
     */
    @Transactional(readOnly = true)
    public List<ToTeamFormDTO> getTeamMatchingList(int limit, String stringCursor){
        PageRequest request = PageRequest.of(FIRST_PAGE, limit);
        LocalDateTime cursor = LocalDateTime.parse(stringCursor, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        log.debug("[TeamMatchingService] cursor : {}, request : {}",cursor, request);
        long startTime = System.currentTimeMillis();
        List<TeamMatching> result = teamMatchingRepository.findAllByCreatedAtBefore(cursor, request);
        log.debug("[TeamMatchingService] result : {}", result);
        long endTime = System.currentTimeMillis();
        log.debug("[TeamMatchingService] executed query time : {}", endTime - startTime);
        List<ToTeamFormDTO> dtoList = result.stream().map(teamMatchingEntity -> teamMatchingEntity.toTeamFormDto(new ToTeamFormDTO())).toList();
        return dtoList;
    }

    /**
     * Objective : 팀 매칭 글에 지원하는 메소드 입니다.
     * Flow :
     * 1. teamMatchingId를 가진 글을 조회한다.(없을 경우 BaseException을 발생시킨다.)
     * 2. 조회된 글이 삭제되었을 경우 BaseException을 발생시킨다.
     * 3. 글 작성자와 접속자가 같은지 확인한다.(다를 경우 BaseException을 발생시킨다.)
     * 4. 이미 지원한 작성자인지 확인한다.
     * 5. 모집 상태가 모집 중인지 확인한다.
     * 5-1. 모집 상태가 모집 중이 아닐 경우 BaseException을 발생시킨다.
     * 5-2. 요구 실력을 충족하지 못할 경우 BaseException을 발생시킨다.(임시 주석 처리)
     * 6. TeamApplicant 객체를 생성하여 연관관계를 맺고 저장한다.
     * Note :
     * 적합성 여부를 판단하는 로직을 주석 처리 함.
     */
    public Long applyTeamMatching(Long teamMatchingId, FromApplicantDto fromApplicantDto, Long id){
        long userId = id;

        TeamMatching entity = teamMatchingRepository.findById(teamMatchingId).orElseThrow(()-> new BaseException(TEAM_POST_NOT_FOUND));

        if(entity.isCorrespondTo(userId)){
            throw new BaseException(PROHIBITED_TO_APPLY_TO_YOUR_POST);
        }

        Long teamApplicantTableId = 0L;
        TeamApplicant existedApplicant = entity.getTeamApplicants().stream().filter(applicant ->
            applicant.getApplicantId().equals(userId)).findFirst().get();
       if(existedApplicant != null){
            if(existedApplicant.getApplyStatus() == ApplyStatusType.WAITING){
                throw new BaseException(ALREADY_APPLIED_TO_THIS_POST);
            }else{
                throw new BaseException(NOT_ALLOWED_REQUEST);
            }
        }

        if(entity.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){

            User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(NOT_FOUND_USER));
            String userNickname = user.getNickname();

//            if(entityToDtoMapper.toSkillLevelTypeList(entity.getSkillLevel()).contains(fromApplicantDto.getSkillLevel().getLevel()) == false){
//                throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
//            }

            TeamApplicant applicant = TeamApplicant.builder()
                    .applicantId(userId)
                    .applicantNickname(userNickname)
                    .applyStatus(ApplyStatusType.WAITING)
                    .teamName(fromApplicantDto.getTeamName())
                    .skillLevel(fromApplicantDto.getSkillLevel())
                    .build();

            applicant.connectTeamMatching(entity);
            TeamApplicant createdApplicant = teamApplicantRepository.save(applicant);
            teamApplicantTableId = createdApplicant.getTeamApplicantTableId();

        }else{
            throw new BaseException(TEAM_POST_IS_NOT_RECRUITING);
        }

        return teamApplicantTableId;
    }



    /**
     * Objective : 팀 매칭 글에 지원한 신청자를 거절하는 메소드 입니다.
     * Flow :
     * 1. teamMatchingId를 가진 글을 조회한다.(없을 경우 BaseException을 발생시킨다.)
     * 2. 글 작성자와 접속자가 같은지 확인한다.(다를 경우 BaseException을 발생시킨다.)
     * 3. 모집 상태가 모집 중인지 확인한다.
     * 3-1. 신청자의 신청 상태가 WAITING 인지 확인한다. 그렇다면 연관관계를 끊고 REJECTED로 변경한다. 그렇지 않다면 BaseException을 발생시킨다.
     * 4. 모집 상태가 모집 중이 아닐 경우 BaseException을 발생시킨다.
     * Note :
     * 이 메소드를 수행하려면 해당 글의 모집 상태가 RECRUITING이어야 합니다.
     * 그리고 신청자의 신청 상태가 WAITING 이어야 합니다.
     */
    public void rejectApplicant(long teamMatchingId, long teamApplicantId, long hostId){
        TeamMatching teamPost = teamMatchingRepository.findById(teamMatchingId).orElseThrow(()->new BaseException(TEAM_POST_NOT_FOUND));

        if(teamPost.isCorrespondTo(hostId) == false){ // 접근자가 게시글 작성자가 아닐 때
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(teamPost.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            TeamApplicant applicant = teamApplicantRepository.findById(teamApplicantId).orElseThrow(()->new BaseException(APPLICANT_NOT_FOUND));
            if(applicant.getApplyStatus() == ApplyStatusType.WAITING){
                applicant.updateApplyStatus(ApplyStatusType.REJECTED);
            }else{
                throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
            }
        }else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }
    }

    /**
     * Objective : 팀 매칭 글에 지원한 신청자를 취소하는 메소드 입니다.
     * Flow :
     * 1. teamMatchingId를 가진 글을 조회한다.(없을 경우 BaseException을 발생시킨다.)
     * 2. 해당 신청자의 TeamApplicant 객체를 가져온다.(없을 경우 BaseException을 발생시킨다.)
     * 3. 글 작성자와 접속자가 같은지 확인한다.(다를 경우 BaseException을 발생시킨다.)
     * 4. 모집 상태가 모집 중인지 확인한다.
     * 4-1. 신청자가 WAITING 상태라면 연관관계를 끊고 데이터베이스에서 삭제한다.(hard delete) 그렇지 않다면 BaseException을 발생시킨다.
     * 5. 모집 상태가 모집 중이 아닐 경우 BaseException을 발생시킨다.
     * Note :
     * 이 메소드를 수행하려면 해당 글의 모집 상태가 RECRUITING이어야 합니다.
     * 그리고 신청자의 신청 상태가 WAITING 이어야 합니다.
     */
    public void cancelApplicant(long teamMatchingId, long teamApplicantId, long writerId){
        TeamMatching teamPost = teamMatchingRepository.findById(teamMatchingId).orElseThrow(()->new BaseException(TEAM_POST_NOT_FOUND));

        TeamApplicant applicant = teamApplicantRepository.findById(teamApplicantId).orElseThrow(()->new BaseException(APPLICANT_NOT_FOUND));

        if(applicant.isCorrespondTo(writerId) == false){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(teamPost.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            if(applicant.getApplyStatus() == ApplyStatusType.WAITING){
                applicant.disconnectTeamMatching();
                teamApplicantRepository.delete(applicant);
            }else{
                throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
            }
        }else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }
    }

    /**
     * Objective : 팀 매칭 글에 지원한 신청자를 수락하는 메소드 입니다.
     * Flow :
     * 1. teamMatchingId를 가진 글을 조회한다.(없을 경우 BaseException을 발생시킨다.)
     * 2. 해당 신청자의 TeamApplicant 객체를 가져온다.(없을 경우 BaseException을 발생시킨다.)
     * 3. 글 작성자와 접속자가 같은지 확인한다.(다를 경우 BaseException을 발생시킨다.)
     * 4. 모집 상태가 모집 중인지 확인한다.
     * 4-1. 해당 글의 opponent가 이미 설정되어 있을 경우 BaseException을 발생시킨다.
     * 4-2. 4-2의 과정을 거친 후에는 해당 신청자를 opponent로 설정한다.
     * 4-3. 신청자의 신청 상태를 ACCEPTED로 변경한다.
     * 5. 모집 상태가 모집 중이 아닐 경우 BaseException을 발생시킨다.
     * Note :
     * 이 메소드를 수행하려면 해당 글의 모집 상태가 RECRUITING이어야 합니다.
     */
    public void acceptApplicant(long teamMatchingId,long teamApplicantId, long writerId){
        TeamMatching teamPost = teamMatchingRepository.findById(teamMatchingId).orElseThrow(()->new BaseException(TEAM_POST_NOT_FOUND));
        TeamApplicant teamApplicant = teamApplicantRepository.findById(teamApplicantId).orElseThrow(()->new BaseException(APPLICANT_NOT_FOUND));
        User applicantUser = userRepository.findById(teamApplicant.getApplicantId()).orElseThrow(()->new BaseException(NOT_FOUND_USER));
        if(teamPost.isCorrespondTo(writerId) == false){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }

        if(teamPost.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            if(teamApplicant.getApplyStatus() == ApplyStatusType.WAITING) {
                if (teamPost.getOpponent() != null) {
                    throw new BaseException(ALEADY_DECLARED_OPPONENT);
                }
                teamPost.declareOpponent(applicantUser);
                teamApplicant.updateApplyStatus(ApplyStatusType.ACCEPTED);
            }else{
                throw new BaseException(PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS);
            }
        }else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }
    }

    /**
     * Objective : 팀 매칭 글의 모집 상태를 완료로 변경하는 메소드 입니다.
     * Flow :
     * 1. teamMatchingId를 가진 글을 조회한다.(없을 경우 BaseException을 발생시킨다.)
     * 2. 글 작성자와 접속자가 같은지 확인한다.(다를 경우 BaseException을 발생시킨다.)
     * 3. 모집 상태가 모집 중인지 확인한다.
     * 3-1. 신청자의 지원 상태가 REJECTED, WAITING일 경우 연관관계를 끊고 데이터베이스에서 삭제한다.(hard delete)
     * 4. 모집 상태를 완료로 변경한다.
     * 5. 모집 상태가 모집 중이 아닐 경우 BaseException을 발생시킨다.
     */
    public void completeTeamMatching(long teamMatchingId, long writerId){
        TeamMatching teamPost = teamMatchingRepository.findById(teamMatchingId).orElseThrow(()->new BaseException(TEAM_POST_NOT_FOUND));
        if(teamPost.isCorrespondTo(writerId) == false){
            throw new BaseException(USER_NOT_AUTHORIZED);
        }
        if(teamPost.getRecruitmentStatus() == RecruitmentStatusType.RECRUITING){
            if(teamPost.getOpponent() == null){
                throw new BaseException(OPPONENT_NOT_DECLARED);
            }
            if(teamPost.getTeamApplicants().size() > 0){
                teamPost.getTeamApplicants().forEach(polledTeamApplicant -> {
                    if(polledTeamApplicant.getApplyStatus() != ApplyStatusType.ACCEPTED){
                        polledTeamApplicant.disconnectTeamMatching();
                        teamApplicantRepository.delete(polledTeamApplicant);
                    }
                });
            }else{
                throw new BaseException(NOT_ALLOWED_REQUEST);
            }
            teamPost.setRecruitmentStatus(RecruitmentStatusType.COMPLETED);
        }else{
            throw new BaseException(MATE_POST_ALREADY_CANCELED_OR_COMPLETED);
        }
    }

    /**
     * 내가 속한 모든 팀매칭 리스트 보기
     * @param userId 유저아이디
     * @return MyTeamMatchingListRes 나의 팀매칭 리스트
     * */
    public MyTeamMatchingListRes getMyTeamMatchingList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        List<TeamMatching> allByWriter = teamMatchingRepository.findAllByWriterAndIsDeletedFalse(user);
        List<TeamMatching> allByApplications = teamMatchingRepository.findAllByApplicationId(userId);

        List<ToTeamFormDTO> authoredPost = new ArrayList<>(allByWriter.stream()
                .map(teamMatchingEntity -> teamMatchingEntity.toTeamFormDto(new ToTeamFormDTO()))
                .toList());

        List<ToTeamFormDTO> participatedPost = new ArrayList<>(allByApplications.stream()
                .map(teamMatchingEntity -> teamMatchingEntity.toTeamFormDto(new ToTeamFormDTO()))
                .map(toTeamFormDTO -> {
                    toTeamFormDTO.setTeamApplicants(
                            toTeamFormDTO.getTeamApplicants().stream()
                                    .filter(toApplicantDto -> toApplicantDto.getApplicantId().equals(userId))
                                    .toList()
                    );
                    return toTeamFormDTO;
                })
                .toList());

        for (List<ToTeamFormDTO> toTeamFormDTOS : Arrays.asList(authoredPost, participatedPost)) {
            Collections.sort(toTeamFormDTOS, (o1, o2) -> {
                if (o1.getScheduledDate().isEqual(o2.getScheduledDate())) {
                    return o1.getStartTime().compareTo(o2.getStartTime());
                }
                return o1.getScheduledDate().compareTo(o2.getScheduledDate());
            });
        }

        return new MyTeamMatchingListRes(authoredPost, participatedPost);
    }
}
