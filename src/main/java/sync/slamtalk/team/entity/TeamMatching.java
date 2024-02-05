package sync.slamtalk.team.entity;

import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.mapper.MatePostEntityToDtoMapper;
import sync.slamtalk.team.dto.FromTeamFormDTO;
import sync.slamtalk.team.dto.ToApplicantDto;
import sync.slamtalk.team.dto.ToTeamFormDTO;
import sync.slamtalk.user.entity.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static sync.slamtalk.team.error.TeamErrorResponseCode.ALEADY_DECLARED_OPPONENT;
import static sync.slamtalk.team.error.TeamErrorResponseCode.PROHIBITED_TO_APPLY_TO_YOUR_POST;

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
    private Long teamMatchingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_id")
    private User opponent;

    private String teamName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String locationDetail;

    @Column(nullable = false)
    private Integer numberOfMembers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecruitedSkillLevelType skillLevel;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private RecruitmentStatusType recruitmentStatus;

    @OneToMany(mappedBy = "teamMatching", cascade = CascadeType.ALL)
    private List<TeamApplicant> teamApplicants = new ArrayList<>();

//    public void connectUser(long writerId){ // * writerId를 User 객체로 대체할 것!
//        this.writerId = writerId;
//    }

    public void declareOpponent(User opponent){
        this.opponent = opponent;
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
                ", writerId=" + writer.getId() +
                ", opponentId=" + opponent.getId() +
                ", teamName='" + teamName + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", locationDetail='" + locationDetail + '\'' +
                ", numberOfMembers=" + numberOfMembers +
                ", skillLevel=" + skillLevel +
                ", scheduledDate=" + scheduledDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public void updateTeamMatching(FromTeamFormDTO fromTeamFormDTO){
        this.title = fromTeamFormDTO.getTitle();
        this.content = fromTeamFormDTO.getContent();
        this.locationDetail = fromTeamFormDTO.getLocationDetail();
        this.skillLevel = fromTeamFormDTO.getSkillLevel();
        this.startTime = fromTeamFormDTO.getStartTime();
        this.endTime = fromTeamFormDTO.getEndTime();
        this.scheduledDate = fromTeamFormDTO.getScheduledDate();
        this.teamName = fromTeamFormDTO.getTeamName();
        this.numberOfMembers = fromTeamFormDTO.getNumberOfMembers();
    }

    public void createTeamMatching(FromTeamFormDTO fromTeamFormDTO, User user){ // * writerId를 User 객체로 대체할 것!
        this.title = fromTeamFormDTO.getTitle();
        this.content = fromTeamFormDTO.getContent();
        this.locationDetail = fromTeamFormDTO.getLocationDetail();
        this.skillLevel = fromTeamFormDTO.getSkillLevel();
        this.startTime = fromTeamFormDTO.getStartTime();
        this.endTime = fromTeamFormDTO.getEndTime();
        this.scheduledDate = fromTeamFormDTO.getScheduledDate();
        this.teamName = fromTeamFormDTO.getTeamName();
        this.numberOfMembers = fromTeamFormDTO.getNumberOfMembers();
        this.recruitmentStatus = RecruitmentStatusType.RECRUITING;
        this.connectParentUser(user);
    }

    /*
    * TeamMatching 객체를 ToTeamFormDTO로 변환하여 반환하는 메소드 입니다.
    * TeamMatching 객체의 teamApplicants 리스트를 ToApplicantDto로 변환하여 순환참조를 방지합니다.
     */
    public ToTeamFormDTO toTeamFormDto(ToTeamFormDTO dto){
        MatePostEntityToDtoMapper mapper = new MatePostEntityToDtoMapper();
        dto.setTeamMatchingId(this.teamMatchingId);
        dto.setTitle(this.title);
        dto.setContent(this.content);
        dto.setWriterId(this.writer.getId());
        dto.setNickname(this.writer.getNickname());
        dto.setLocationDetail(this.locationDetail);
        dto.setSkillLevel(mapper.toSkillLevelTypeList(this.skillLevel));
        dto.setScheduledDate(this.scheduledDate);
        dto.setStartTime(this.startTime);
        dto.setEndTime(this.endTime);
        dto.setTeamName(this.teamName);
        dto.setNumberOfMembers(this.numberOfMembers);
        dto.setCreatedAt(this.getCreatedAt());
        dto.setRecruitmentStatusType(this.recruitmentStatus);
        dto.setTeamApplicantsDto(this.makeApplicantDto());
        return dto;
    }

    @Override
    public void delete() {
        super.delete();
    }


    // 글의 작성자 ID와 현재 로그인한 사용자 ID가 일치하는지 확인
    public boolean isCorrespondTo(long loginId){
        return this.writer.getId() == loginId;
    }

    public void setRecruitmentStatus(RecruitmentStatusType recruitmentStatus){
        //todo : 같은 모집 상태로 변경 시 예외 처리

        this.recruitmentStatus = recruitmentStatus;
    }

    public void connectOpponent(User opponent){
        if(opponent.equals(this.writer)){
            throw new BaseException(PROHIBITED_TO_APPLY_TO_YOUR_POST);
        }
        if(this.opponent != null){
            throw new BaseException(ALEADY_DECLARED_OPPONENT);
        }
        this.opponent = opponent;
    }

    public void connectParentUser(User user){ // * writerId를 User 객체로 대체할 것!
        this.writer = user;
        //  this.writer.getTeamMatchings().add(this);
        // * 연관관계 편의 메서드
        // todo: User 객체에 있는 teamMatchingList에 현재 객체를 추가한다.
    }

    // * 리스트 컬렉션에 저장된 TeamApplicant 객체를 ToApplicantDto로 변환하여 리스트로 반환하는 기능을 수행합니다.
    public List<ToApplicantDto> makeApplicantDto(){
        List<TeamApplicant> teamApplicants = getTeamApplicants();
        List<ToApplicantDto> dto = teamApplicants.stream().map(TeamApplicant::makeDto).collect(Collectors.toList());
        return dto;
    }
}
