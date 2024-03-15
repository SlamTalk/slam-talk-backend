package sync.slamtalk.mate.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.user.entity.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "matepost")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MatePost extends BaseEntity implements Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mate_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "writer_id")
    private User writer;

    @Column(nullable = false)
    private String location; // 시합 장소 (* 서울, 인천 등)

    private String locationDetail; // 상세 시합 장소

    @Column(nullable = false)
    private String title; // 글 제목

    @Column(nullable = false)
    private String content; // 글 내용

    private RecruitedSkillLevelType skillLevel;

    private boolean skillLevelHigh;

    private boolean skillLevelMiddle;

    private boolean skillLevelLow;

    private boolean skillLevelBeginner;

    @Column(nullable = false)
    private LocalDate scheduledDate; // 예정된 날짜

    @Column(nullable = false)
    private LocalTime startTime; // 예정된 시작 시간

    @Column(nullable = false)
    private LocalTime endTime; // 예정된 종료 시간

    private Long chatRoomId;

    @Column(nullable = false, name = "recruitment_status_type")
    @Enumerated(EnumType.STRING)
    private RecruitmentStatusType recruitmentStatus; // 모집 마감 여부 "RECRUITING", "COMPLETED", "CANCELED"

    @Column(nullable = false)
    private int maxParticipantsForwards; // 포워드 최대 참여 인원

    @Column(nullable = false)
    private int currentParticipantsForwards; // 포워드 현재 참여 인원

    @Column(nullable = false)
    private int maxParticipantsCenters; // 센터 최대 참여 인원

    @Column(nullable = false)
    private int currentParticipantsCenters; // 센터 현재 참여 인원

    @Column(nullable = false)
    private int maxParticipantsGuards; // 가드 최대 참여 인원

    @Column(nullable = false)
    private int currentParticipantsGuards; // 가드 현재 참여 인원

    @Column(nullable = false)
    private int maxParticipantsOthers; // 모집 포지션 무관 최대 참여 인원

    @Column(nullable = false)
    private int currentParticipantsOthers; // 모집 포지션 무관 현재 참여 인원

    @OneToMany(mappedBy = "matePost", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Participant> participants = new ArrayList<>(); // 참여자 목록


    /**
     * 메이트찾기 게시글 삭제
     * soft delete
     */
    public void softDeleteMatePost() {
        softDeleteParticipantAll();
        if (this.recruitmentStatus == RecruitmentStatusType.RECRUITING) {
            this.recruitmentStatus = RecruitmentStatusType.CANCELED;
        }
        this.delete();
    }

    /**
     * 메이트찾기 게시글에 속한 참여자 목록 삭제한다.(글 작성자는 참여자 목록에 속하지 않음)
     * soft delete
     */
    public void softDeleteParticipantAll() {
        participants.forEach(Participant::softDeleteParticipant);
    }

    public void updateRecruitmentStatus(RecruitmentStatusType recruitmentStatus) {
        if (this.recruitmentStatus == recruitmentStatus) {
            log.debug("변경할 모집 상태와 현재 모집 상태가 같습니다.");
        }
        this.recruitmentStatus = recruitmentStatus;
    }

    public boolean isCorrespondToUser(Long userId) {
        log.debug("글 작성자 ID : {}", this.writer.getId());
        log.debug("요청자 ID : {}", userId);
        log.debug("글 작성자 ID와 요청자 ID 일치 여부 : {}", this.writer.getId().equals(userId));
        return this.writer.getId().equals(userId);
    }

    public void connectParent(User user) {
        this.writer = user;
        user.getMatePosts().add(this);
    }

    public void configureSkillLevel(SkillLevelList list) {
        this.skillLevelBeginner = false;
        this.skillLevelLow = false;
        this.skillLevelMiddle = false;
        this.skillLevelHigh = false;

        if (list.isSkillLevelBeginner()) this.skillLevelBeginner = true;
        if (list.isSkillLevelLow()) this.skillLevelLow = true;
        if (list.isSkillLevelMiddle()) this.skillLevelMiddle = true;
        if (list.isSkillLevelHigh()) this.skillLevelHigh = true;
    }

    public Long getWriterId() {
        return this.writer.getId();
    }

    public String getWriterNickname() {
        return this.writer.getNickname();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void updateStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void updateEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void updateLocationDetail(String locationDetail) {
        this.locationDetail = locationDetail;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateCurrentParticipantsForwards(int currentParticipantsForwards) {
        this.currentParticipantsForwards = currentParticipantsForwards;
    }

    public void updateCurrentParticipantsCenters(int currentParticipantsCenters) {
        this.currentParticipantsCenters = currentParticipantsCenters;
    }

    public void updateCurrentParticipantsGuards(int currentParticipantsGuards) {
        this.currentParticipantsGuards = currentParticipantsGuards;
    }

    public void updateCurrentParticipantsOthers(int currentParticipantsOthers) {
        this.currentParticipantsOthers = currentParticipantsOthers;
    }

    public void increasePositionNumbers(PositionType position) {
        switch (position) {
            case CENTER:
                updateCurrentParticipantsCenters(getCurrentParticipantsCenters() + 1);
                break;
            case GUARD:
                updateCurrentParticipantsGuards(getCurrentParticipantsGuards() + 1);
                break;
            case FORWARD:
                updateCurrentParticipantsForwards(getCurrentParticipantsForwards() + 1);
                break;
            case UNSPECIFIED:
                updateCurrentParticipantsOthers(getCurrentParticipantsOthers() + 1);
                break;
        }
    }

    public List<String> toSkillLevelTypeList() {
        List<String> skillLevelTypeList = new ArrayList<>();

        if (this.isSkillLevelBeginner()) {
            skillLevelTypeList.add(SkillLevelType.BEGINNER.getLevel());
        }

        if (this.isSkillLevelLow()) {
            skillLevelTypeList.add(SkillLevelType.LOW.getLevel());
        }

        if (this.isSkillLevelMiddle()) {
            skillLevelTypeList.add(SkillLevelType.MIDDLE.getLevel());
        }

        if (this.isSkillLevelHigh()) {
            skillLevelTypeList.add(SkillLevelType.HIGH.getLevel());
        }

        return skillLevelTypeList;
    }

    @Override
    public String toString() {
        return "MatePost{" +
                "matePostId=" + id +
                ", locationDetail='" + locationDetail + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", scheduledDate=" + scheduledDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", chatRoomId=" + chatRoomId +
                ", recruitmentStatus=" + recruitmentStatus +
                ", maxParticipantsForwards=" + maxParticipantsForwards +
                ", currentParticipantsForwards=" + currentParticipantsForwards +
                ", maxParticipantsCenters=" + maxParticipantsCenters +
                ", currentParticipantsCenters=" + currentParticipantsCenters +
                ", maxParticipantsGuards=" + maxParticipantsGuards +
                ", currentParticipantsGuards=" + currentParticipantsGuards +
                ", maxParticipantsOthers=" + maxParticipantsOthers +
                ", currentParticipantsOthers=" + currentParticipantsOthers +
                '}';
    }
}
