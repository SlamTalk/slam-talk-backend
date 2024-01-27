package sync.slamtalk.mate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.entity.SkillLevelType;
import sync.slamtalk.mate.mapper.MatePostEntityToDtoMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MateFormDTO {
    private long matePostId; // 글 아이디
    private long writerId; // 작성자 아이디

    private String title; // 제목
    private String content; // 본문
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startScheduledTime; // 예정된 시작 시간
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endScheduledTime; // 예정된 종료 시간
    private String locationDetail; // 상세 시합 장소
    @Enumerated(EnumType.STRING)
    private RecruitedSkillLevelType skillLevel; // 원하는 스킬 레벨 - BEGINNER, OVER_BEGINNER, UNDER_LOW, OVER_LOW, UNDER_MIDDLE, OVER_MIDDLE, UNDER_HIGH, HIGH
    private List<String> skillLevelList; // 원하는 스킬 레벨 목록 (프론트로 응답 시에만 사용)

    private Integer maxParticipantsCenters; // 모집 포지션 센터 최대 인원 수
    private Integer currentParticipantsCenters; // 모집 포지션 센터 현재 인원 수

    private Integer maxParticipantsGuards; // 모집 포지션 가드 최대 인원 수
    private Integer currentParticipantsGuards; // 모집 포지션 가드 현재 인원 수

    private Integer maxParticipantsForwards; // 모집 포지션 포워드 최대 인원 수
    private Integer currentParticipantsForwards; // 모집 포지션 포워드 현재 인원 수

    private Integer maxParticipantsOthers; // 모집 포지션 무관 최대 인원 수
    private Integer currentParticipantsOthers; // 모집 포지션 무관 현재 인원 수

    private List<PositionListDTO> positionList = new ArrayList<>(); // 모집 포지션 목록 (프론트로 응답 시에만 사용)

    private List<MatePostApplicantDTO> participants = new ArrayList<>(); // 참여자 목록

    @JsonIgnore
    public MatePost toEntity(long userId) { // * writerId를 User 객체로 대체할 것!
            return MatePost.builder()
                    .writerId(userId)
                    .title(title)
                    .startScheduledTime(startScheduledTime)
                    .endScheduledTime(endScheduledTime)
                    .locationDetail(locationDetail)
                    .content(content)
                    .maxParticipantsCenters(maxParticipantsCenters)
                    .currentParticipantsCenters(0)
                    .maxParticipantsGuards(maxParticipantsGuards)
                    .currentParticipantsGuards(0)
                    .maxParticipantsForwards(maxParticipantsForwards)
                    .currentParticipantsForwards(0)
                    .maxParticipantsOthers(maxParticipantsOthers)
                    .currentParticipantsOthers(0)
                    .skillLevel(skillLevel)
                    .recruitmentStatus(RecruitmentStatusType.RECRUITING)
                    .participants(new ArrayList<>())
                    .build();
    }

}
