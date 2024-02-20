package sync.slamtalk.mate.dto.garbage;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sync.slamtalk.mate.dto.PositionListDto;
import sync.slamtalk.mate.dto.response.ParticipantDto;
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
    @NonNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @NonNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    @NonNull
    @Length(min = 4, max = 30)
    private String title;
    @NonNull
    @Length(min = 4, max = 500)
    private String content;
    @NonNull
    private List<PositionListDto> positionList = new ArrayList<>();

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
    private List<ParticipantDto> fromParticipants = new ArrayList<>();


    public MatePostDTO(long matePostId, long writerId, String writerNickname, LocalDate scheduledDate, LocalTime startTime, LocalTime endTime, String title, String content, List<PositionListDto> positionList, List<String> skillList, RecruitmentStatusType recruitmentStatus, String locationDetail, List<Participant> participants, LocalDateTime createdAt) {
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
