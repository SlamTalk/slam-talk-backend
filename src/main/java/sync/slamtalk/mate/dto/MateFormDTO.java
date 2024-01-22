package sync.slamtalk.mate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.Participant;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.entity.SkillLevelType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MateFormDTO {
    @NonNull
    private long userId; // 글 작성자 아이디 // * User 테이블과 매핑 필요
    private long matePostId; // 글 아이디

    private String title; // 제목
    private String content; // 본문
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime scheduledTime; // 예정된 시간
    private String locationDetail; // 상세 시합 장소
    private SkillLevelType skillLevel; // 원하는 스킬 레벨 "BEGINNER", "INTERMEDIATE", "MASTER", "IRRELEVANT"

    private int maxParticipantsCenters; // 모집 포지션 센터 최대 인원 수
    private int currentParticipantsCenters; // 모집 포지션 센터 현재 인원 수

    private int maxParticipantsGuards; // 모집 포지션 가드 최대 인원 수
    private int currentParticipantsGuards; // 모집 포지션 가드 현재 인원 수

    private int maxParticipantsForwards; // 모집 포지션 포워드 최대 인원 수
    private int currentParticipantsForwards; // 모집 포지션 포워드 현재 인원 수

    private int maxParticipantsOthers; // 모집 포지션 무관 최대 인원 수
    private int currentParticipantsOthers; // 모집 포지션 무관 현재 인원 수

    private List<Participant> participants = new ArrayList<>(); // 참여자 목록

    @JsonIgnore
    public MatePost toEntity(long writerId) {
            return MatePost.builder()
                    .writerId(writerId)
                    .title(title)
                    .scheduledTime(scheduledTime)
                    .locationDetail(locationDetail)
                    .content(content)
                    .maxParticipantsCenters(maxParticipantsCenters)
                    .maxParticipantsGuards(maxParticipantsGuards)
                    .maxParticipantsForwards(maxParticipantsForwards)
                    .maxParticipantsOthers(maxParticipantsOthers)
                    .skillLevel(skillLevel)
                    .softDelete(false)
                    .recruitmentStatus(RecruitmentStatusType.RECRUITING)
                    .build();
    }

}
