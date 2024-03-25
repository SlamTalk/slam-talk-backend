package sync.slamtalk.mate.dto.garbage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import sync.slamtalk.mate.dto.PositionListDto;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.entity.SkillLevelList;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;




@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MateFormDTO {
    @JsonIgnore
    private final EntityToDtoMapper entityToDtoMapper = new EntityToDtoMapper();

    private long matePostId; // 글 아이디
    private long writerId; // 작성자 아이디
    private String writerNickname; // 작성자 닉네임
    private String writerImageUrl; // 작성자 이미지 URL

    @NotBlank
    private String title; // 제목
    @NotBlank
    private String content; // 본문

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate; // 예정된 날짜
    @NonNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime; // 예정된 시작 시간
    @NonNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime; // 예정된 종료 시간

    @NonNull
    @NotBlank
    private String locationDetail; // 상세 시합

    @Enumerated(EnumType.STRING)
    private RecruitmentStatusType recruitmentStatus; // 모집 상태 - RECRUITING, COMPLETED, CANCEL
    @NonNull
    @Enumerated(EnumType.STRING)
    private RecruitedSkillLevelType skillLevel; // 원하는 스킬 레벨 - BEGINNER, OVER_BEGINNER, UNDER_LOW, OVER_LOW, UNDER_MIDDLE, OVER_MIDDLE, UNDER_HIGH, HIGH
    private List<String> skillLevelList; // 원하는 스킬 레벨 목록 (프론트로 응답 시에만 사용)

    @NonNull
    @Length(min = 0, max = 5)
    private Integer maxParticipantsCenters; // 모집 포지션 센터 최대 인원 수
    private Integer currentParticipantsCenters; // 모집 포지션 센터 현재 인원 수

    @NonNull
    @Length(min = 0, max = 5)
    private Integer maxParticipantsGuards; // 모집 포지션 가드 최대 인원 수
    private Integer currentParticipantsGuards; // 모집 포지션 가드 현재 인원 수

    @NonNull
    @Length(min = 0, max = 5)
    private Integer maxParticipantsForwards; // 모집 포지션 포워드 최대 인원 수
    private Integer currentParticipantsForwards; // 모집 포지션 포워드 현재 인원 수

    @NonNull
    @Length(min = 0, max = 5)
    private Integer maxParticipantsOthers; // 모집 포지션 무관 최대 인원 수
    private Integer currentParticipantsOthers; // 모집 포지션 무관 현재 인원 수

    private List<PositionListDto> positionList = new ArrayList<>(); // 모집 포지션 목록 (프론트로 응답 시에만 사용)

    private List<MatePostApplicantDTO> participants = new ArrayList<>(); // 참여자 목록

    @JsonIgnore
    public MatePost toEntity() {
        SkillLevelList tempSkillList = entityToDtoMapper.fromRecruitSkillLevel(skillLevel);
        String[] temp = locationDetail.split(" ", 2);
        String location = temp[0];
        String locationDetail = temp.length > 1 ? temp[1] : "";
            MatePost resultMatePost = MatePost.builder()
                    .title(title)
                    .scheduledDate(scheduledDate)
                    .startTime(startTime)
                    .endTime(endTime)
                    .location(location)
                    .locationDetail(locationDetail)
                    .content(content)
                    .skillLevel(skillLevel)
                    .maxParticipantsCenters(maxParticipantsCenters)
                    .currentParticipantsCenters(0)
                    .maxParticipantsGuards(maxParticipantsGuards)
                    .currentParticipantsGuards(0)
                    .maxParticipantsForwards(maxParticipantsForwards)
                    .currentParticipantsForwards(0)
                    .maxParticipantsOthers(maxParticipantsOthers)
                    .currentParticipantsOthers(0)
                    .recruitmentStatus(RecruitmentStatusType.RECRUITING)
                    .participants(new ArrayList<>())
                    .build();
            resultMatePost.configureSkillLevel(tempSkillList);
            return resultMatePost;
    }

}
