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
    @NonNull
    private List<String> skillList = new ArrayList<>();
    @NonNull
    @Enumerated(EnumType.STRING)
    private RecruitmentStatusType recruitmentStatus;
    @NonNull
    private String locationDetail;
    @NonNull
    private List<Participant> participants = new ArrayList<>();
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;


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

    @QueryProjection
    public MatePostDTO(long matePostId, long writerId, String writerNickname, LocalDate scheduledDate, LocalTime startTime, LocalTime endTime, String title, String content, int currentParticipantsCenters, int maxParticipantsCenters, int currentParticipantsForwards, int maxParticipantsForwards, int currentParticipantsGuards, int maxParticipantsGuards, int currentParticipantsOthers, int maxParticipantsOthers, boolean skillLevelBeginner, boolean skillLevelLow, boolean skillLevelMiddle, boolean skillLevelHigh, RecruitmentStatusType recruitmentStatus, String locationDetail, List<Participant> participants, LocalDateTime createdAt) {
        this.matePostId = matePostId;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.scheduledDate = scheduledDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.content = content;
        if (maxParticipantsCenters > 0) {
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.CENTER.getPosition(), maxParticipantsCenters, currentParticipantsCenters);
            this.positionList.add(positionListDTO);
        }
        if (maxParticipantsForwards > 0) {
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.FORWARD.getPosition(), maxParticipantsForwards, currentParticipantsForwards);
            this.positionList.add(positionListDTO);
        }
        if (maxParticipantsGuards > 0) {
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.GUARD.getPosition(), maxParticipantsGuards, currentParticipantsGuards);
            this.positionList.add(positionListDTO);
        }
        if (maxParticipantsOthers > 0) {
            PositionListDTO positionListDTO = new PositionListDTO(PositionType.UNSPECIFIED.getPosition(), maxParticipantsOthers, currentParticipantsOthers);
            this.positionList.add(positionListDTO);
        }
        if (skillLevelBeginner) {
            this.skillList.add(SkillLevelType.BEGINNER.getLevel());
        }
        if (skillLevelLow) {
            this.skillList.add(SkillLevelType.LOW.getLevel());
        }
        if (skillLevelMiddle) {
            this.skillList.add(SkillLevelType.MIDDLE.getLevel());
        }
        if (skillLevelHigh) {
            this.skillList.add(SkillLevelType.HIGH.getLevel());
        }
        this.recruitmentStatus = recruitmentStatus;
        this.locationDetail = locationDetail;
        this.participants = participants;
        this.createdAt = createdAt;
    }
}
