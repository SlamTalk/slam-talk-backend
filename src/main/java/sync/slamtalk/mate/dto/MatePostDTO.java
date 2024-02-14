package sync.slamtalk.mate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import sync.slamtalk.mate.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MatePostDTO {
    @NonNull
    private long matePostId;
    @NonNull
    private long writerId;
    @NonNull
    private String writerNickname;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    @NonNull
    private String title;
    @NonNull
    private String content;
    @NonNull
    private List<PositionListDTO> positionList = new ArrayList<>();

    private List<String> skillList = new ArrayList<>();

    private SkillLevelList skillLevelList;
    @NonNull
    @Enumerated(EnumType.STRING)
    private RecruitmentStatusType recruitmentStatus;
    @NonNull
    private String locationDetail;

    private List<Participant> participants = new ArrayList<>();
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    private List<FromParticipantDto> fromParticipants = new ArrayList<>();


    public MatePostDTO(long matePostId, long writerId, String writerNickname, LocalDate scheduledDate, LocalTime startTime, LocalTime endTime, String title, String content, List<PositionListDTO> positionList, List<String> skillList, RecruitmentStatusType recruitmentStatus, String locationDetail, List<Participant> participants, LocalDateTime createdAt) {
        this.matePostId = matePostId;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.scheduledDate = scheduledDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.content = content;
        this.positionList = positionList;
        this.skillList = skillList;
        this.recruitmentStatus = recruitmentStatus;
        this.locationDetail = locationDetail;
        this.participants = participants;
        this.createdAt = createdAt;
    }

}
