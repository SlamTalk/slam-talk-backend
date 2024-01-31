package sync.slamtalk.team.entity;

import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.team.dto.FromTeamFormDTO;
import sync.slamtalk.team.dto.ToTeamFormDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "teammatchinglist")
@Getter
@NamedEntityGraph(
        name = "TeamMatching.forEagerApplicants",
        attributeNodes = @NamedAttributeNode("teamApplicants")

)
public class TeamMatching extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long teamMatchingId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "writer_id")
    private long writerId; // * 작성자 User 객체로 변경해야 함

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "opponent_id")
    private long opponentId; // * 상대팀 User 객체로 변경해야 함

    private String teamName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String locationDetail;

    @Column(nullable = false)
    private int numberOfMembers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecruitedSkillLevelType skillLevel;

    @Column(nullable = false)
    private LocalDateTime startScheduledTime;

    @Column(nullable = false)
    private LocalDateTime endScheduledTime;

    @Enumerated(EnumType.STRING)
    private RecruitmentStatusType recruitmentStatus;

    @OneToMany(mappedBy = "teamMatching", cascade = CascadeType.ALL)
    private List<TeamApplicant> teamApplicants = new ArrayList<>();

//    public void connectUser(long writerId){ // * writerId를 User 객체로 대체할 것!
//        this.writerId = writerId;
//    }

    public void declareOpponent(long opponentId){
        this.opponentId = opponentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMatching that = (TeamMatching) o;
        return teamMatchingId == that.teamMatchingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamMatchingId);
    }

    @Override
    public String toString() {
        return "teammatching{" +
                "teamMatchingId=" + teamMatchingId +
                ", writerId=" + writerId +
                ", teamName='" + teamName + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", locationDetail='" + locationDetail + '\'' +
                ", numberOfMembers=" + numberOfMembers +
                ", skillLevel=" + skillLevel +
                ", startScheduledTime=" + startScheduledTime +
                ", endScheduledTime=" + endScheduledTime +
                '}';
    }

    public void updateTeamMatching(FromTeamFormDTO fromTeamFormDTO){
        this.title = fromTeamFormDTO.getTitle();
        this.content = fromTeamFormDTO.getContent();
        this.locationDetail = fromTeamFormDTO.getLocationDetail();
        this.skillLevel = fromTeamFormDTO.getSkillLevel();
        this.startScheduledTime = fromTeamFormDTO.getStartScheduledTime();
        this.endScheduledTime = fromTeamFormDTO.getEndScheduledTime();
        this.teamName = fromTeamFormDTO.getTeamName();
        this.numberOfMembers = fromTeamFormDTO.getNumberOfMembers();
    }

    public void createTeamMatching(FromTeamFormDTO fromTeamFormDTO, long writerId){ // * writerId를 User 객체로 대체할 것!
        this.title = fromTeamFormDTO.getTitle();
        this.content = fromTeamFormDTO.getContent();
        this.locationDetail = fromTeamFormDTO.getLocationDetail();
        this.skillLevel = fromTeamFormDTO.getSkillLevel();
        this.startScheduledTime = fromTeamFormDTO.getStartScheduledTime();
        this.endScheduledTime = fromTeamFormDTO.getEndScheduledTime();
        this.teamName = fromTeamFormDTO.getTeamName();
        this.numberOfMembers = fromTeamFormDTO.getNumberOfMembers();
        this.recruitmentStatus = RecruitmentStatusType.RECRUITING;
        this.connectParentUser(writerId);
    }

    public ToTeamFormDTO toTeamFormDto(ToTeamFormDTO dto){
        dto.setTitle(this.title);
        dto.setContent(this.content);
        dto.setWriterId(this.writerId); // * writerId를 User 객체로 대체할 것!
        dto.setLocationDetail(this.locationDetail);
        dto.setSkillLevel(this.skillLevel);
        dto.setStartScheduledTime(this.startScheduledTime);
        dto.setEndScheduledTime(this.endScheduledTime);
        dto.setTeamName(this.teamName);
        dto.setNumberOfMembers(this.numberOfMembers);
        dto.setCreatedAt(this.getCreatedAt());
        dto.setRecruitmentStatusType(this.recruitmentStatus);
        return dto;
    }

    @Override
    public void delete() {
        super.delete();
    }


    // 글의 작성자 ID와 현재 로그인한 사용자 ID가 일치하는지 확인
    public boolean isCorrespondTo(long loginId){
        return this.teamMatchingId == loginId;
    }

    public void setRecruitmentStatus(RecruitmentStatusType recruitmentStatus){
        //todo : 같은 모집 상태로 변경 시 예외 처리

        this.recruitmentStatus = recruitmentStatus;
    }

    public void connectOpponent(long opponentId){ // * opponentId를 User 객체로 대체할 것!
        if(opponentId == this.writerId){
            throw new IllegalArgumentException("상대팀은 작성자가 될 수 없습니다.");
        }
        this.opponentId = opponentId;
    }

    public void connectParentUser(long writerId){ // * writerId를 User 객체로 대체할 것!
        this.writerId = writerId;
        // * 연관관계 편의 메서드
        // todo: User 객체에 있는 teamMatchingList에 현재 객체를 추가한다.
    }
}
