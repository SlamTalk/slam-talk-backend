package sync.slamtalk.team.service;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.team.dto.FromApplicantDto;
import sync.slamtalk.team.dto.FromTeamFormDTO;
import sync.slamtalk.team.dto.ToApplicantDto;
import sync.slamtalk.team.dto.ToTeamFormDTO;
import sync.slamtalk.team.entity.TeamApplicant;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.team.repository.TeamApplicantRepository;
import sync.slamtalk.team.repository.TeamMatchingRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static sync.slamtalk.team.error.TeamErrorResponseCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamMatchingService {
    private final TeamMatchingRepository teamMatchingRepository;
    private final TeamApplicantRepository teamApplicantRepository;

    /*
    * 팀 매칭 글을 등록하는 메소드 입니다.
     */
    public long registerTeamMatching(FromTeamFormDTO dto, long userId){
        TeamMatching teamMatchingEntity = new TeamMatching();
        teamMatchingEntity.createTeamMatching(dto, userId);
        TeamMatching resultTeamMatchingEntity = teamMatchingRepository.save(teamMatchingEntity);
        return resultTeamMatchingEntity.getTeamMatchingId();
    }

    /*
    * 팀 매칭 글을 조회하는 메소드 입니다.
    * 인자로 받은 id를 가진 팀 매칭 글이 없을 경우 BaseException을 발생시킵니다.
    * 팀 매칭 글이 삭제되었을 경우 BaseException을 발생시킵니다.
     */
    public ToTeamFormDTO getTeamMatching(long teamMatchingId){
        TeamMatching teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        if(teamMatchingEntity.getIsDeleted()){
            throw new BaseException(TEAM_POST_ALREADY_DELETED);
        }
        ToTeamFormDTO dto = new ToTeamFormDTO();
        teamMatchingEntity.toTeamFormDto(dto);
        return dto;
    }

    /*
    * 팀 매칭 글을 수정하는 메소드 입니다.
     */
    public ApiResponse updateTeamMatching(long teamMatchingId, FromTeamFormDTO fromTeamFormDTO){
        TeamMatching teamMatchingEntity = teamMatchingRepository.findById(teamMatchingId).orElseThrow();
        teamMatchingEntity.updateTeamMatching(fromTeamFormDTO);
        return ApiResponse.ok();
    }

    /*
    * 팀 매칭 글을 삭제하는 메소드 입니다.
     */
    public ApiResponse deleteTeamMatching(long teamMatchingId, TeamMatching teamMatchingEntity){
        teamMatchingEntity.delete();
        return ApiResponse.ok();
    }

    /*
    * 팀 매칭 목록 조회를 위한 메소드 입니다. 최신 등록일자 순으로 조회합니다.
    * 커서 페이징 방식으로 구현 하였고 다음의 인자를 받습니다.
    * limit : 한번에 가져올 목록의 개수
    * stringCursor : 커서로 사용할 날짜시간 문자열 입니다. 이 값은 LocalDateTime.parse를 통해 LocalDateTime으로 변환됩니다.
    * log.debug를 통해 쿼리가 실행된 시간을 확인할 수 있습니다.
    * ToTeamFormDTO 타입의 리스트를 반환합니다.
     */
    public List<ToTeamFormDTO> getTeamMatchingList(int limit, String stringCursor){
        PageRequest request = PageRequest.of(0, limit);
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

    public ToApplicantDto applyTeamMatching(long teamMatchingId, long userId, FromApplicantDto fromApplicantDto){
        TeamMatching entity = teamMatchingRepository.findById(teamMatchingId).orElseThrow(()-> new BaseException(TEAM_POST_NOT_FOUND));
        if(entity.getIsDeleted()){
            throw new BaseException(TEAM_POST_ALREADY_DELETED);
        }
        // * 신청자의 닉네임을 가져온다.
        String userNickname = "test";

        // * 채팅방을 생성해서 채팅방 id를 가져온다.
        long createdChatroomId = 1; // todo : 채팅방 생성 로직 완료시 해당 메서드를 통한 채팅방 id 가져오기

//        // * 글 작성자와 접속자가 같은지 확인한다.
//        if(entity.getWriterId() == userId){
//            throw new BaseException(PROHIBITED_TO_APPLY_TO_YOUR_POST);
//        }
        // * 모집 상태가 모집 중인지 확인한다.
        if(entity.getRecruitmentStatus() != RecruitmentStatusType.RECRUITING){
            throw new BaseException(TEAM_POST_IS_NOT_RECRUITING);
        }
//        // * 이미 지원한 작성자인지 확인한다.
//        entity.getTeamApplicants().forEach(applicant -> {
//            if(applicant.getApplicantId() == userId){
//                throw new BaseException(ALREADY_APPLIED_TO_THIS_POST);
//            }
//        });

        TeamApplicant applicant = TeamApplicant.builder()
                .applicantId(userId)
                .applicantNickname(userNickname)
                .applyStatus(ApplyStatusType.WAITING)
                .chatroomId(createdChatroomId)
                .teamName(fromApplicantDto.getTeamName())
                .skillLevel(fromApplicantDto.getSkillLevel())
                .build();
        applicant.connectTeamMatching(entity);
        TeamApplicant result = teamApplicantRepository.save(applicant);

        return result.makeDto();
    }
}
