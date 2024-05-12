package sync.slamtalk.team.entity;

import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;
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

import static sync.slamtalk.team.error.TeamErrorResponseCode.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "teammatchinglist")
@Getter
@Builder
@NamedEntityGraph(
        name = "TeamMatching.forEagerApplicants",
        attributeNodes = @NamedAttributeNode("teamApplicants")
)
public class TeamMatching extends BaseEntity implements Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamMatchingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_id")
    private User opponent;

    boolean skillLevelHigh = false;

    boolean skillLevelMiddle = false;

    boolean skillLevelLow = false;

    boolean skillLevelBeginner = false;

    private String teamName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    private String location;

    private String locationDetail;

    @Column(nullable = false)
    private String numberOfMembers;

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

    @OneToMany(mappedBy = "teamMatching", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TeamApplicant> teamApplicants = new ArrayList<>();

//    public void connectUser(long writerId){ // * writerId를 User 객체로 대체할 것!
//        this.writerId = writerId;
//    }

    private static final int MAX_APPLICANTS = 5;

    public void declareOpponent(User opponent){
        this.opponent = opponent;
        this.opponent.getOpponentTeamMatchings().add(this);
    }

    public void cancelOpponent(){
        this.opponent.getOpponentTeamMatchings().remove(this);
        this.opponent = null;
    }

    public void splitAndStoreLocation(String locationDetail){
        String[] splited = locationDetail.split(" ", 2);
        this.location = splited[0];
        this.locationDetail = splited.length > 1 ? splited[1] : "";
    }

    public String returnConcatenatedLocation(){
        return this.location + " " + this.locationDetail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMatching that = (TeamMatching) o;
        return teamMatchingId.equals(that.teamMatchingId);
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
                ", teamName='" + teamName + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", location='" + location + '\'' +
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
        this.skillLevel = fromTeamFormDTO.getSkillLevel();
        SkillLevelList skillList = new EntityToDtoMapper().fromRecruitSkillLevel(fromTeamFormDTO.getSkillLevel());
        configureSkillLevel(skillList);
        this.startTime = fromTeamFormDTO.getStartTime();
        this.endTime = fromTeamFormDTO.getEndTime();
        this.scheduledDate = fromTeamFormDTO.getScheduledDate();
        this.teamName = fromTeamFormDTO.getTeamName();
        this.numberOfMembers = fromTeamFormDTO.getNumberOfMembers();
        this.splitAndStoreLocation(fromTeamFormDTO.getLocationDetail());
    }

    public void createTeamMatching(FromTeamFormDTO fromTeamFormDTO, User user){
        this.title = fromTeamFormDTO.getTitle();
        this.content = fromTeamFormDTO.getContent();
        this.skillLevel = fromTeamFormDTO.getSkillLevel();
        SkillLevelList skillList = new EntityToDtoMapper().fromRecruitSkillLevel(fromTeamFormDTO.getSkillLevel());
        configureSkillLevel(skillList);
        this.startTime = fromTeamFormDTO.getStartTime();
        this.endTime = fromTeamFormDTO.getEndTime();
        this.scheduledDate = fromTeamFormDTO.getScheduledDate();
        this.teamName = fromTeamFormDTO.getTeamName();
        this.numberOfMembers = fromTeamFormDTO.getNumberOfMembers();
        this.recruitmentStatus = RecruitmentStatusType.RECRUITING;
        this.splitAndStoreLocation(fromTeamFormDTO.getLocationDetail());
        this.connectParentUser(user);
    }

    /*
    * TeamMatching 객체를 ToTeamFormDTO로 변환하여 반환하는 메소드 입니다.
    * TeamMatching 객체의 teamApplicants 리스트를 ToApplicantDto로 변환하여 순환참조를 방지합니다.
     */
    public ToTeamFormDTO toTeamFormDto(ToTeamFormDTO dto){
        EntityToDtoMapper mapper = new EntityToDtoMapper();
        dto.setTeamMatchingId(this.teamMatchingId);
        dto.setTitle(this.title);
        dto.setContent(this.content);
        dto.setWriterId(this.writer.getId());
        dto.setWriterNickname(this.writer.getNickname());
        dto.setWriterImageUrl(this.writer.getImageUrl());
        dto.setLocationDetail(this.returnConcatenatedLocation());
        dto.setSkillLevel(this.skillLevel);
        dto.setSkillLevelList(mapper.toSkillLevelTypeList(this.skillLevel));
        dto.setScheduledDate(this.scheduledDate);
        dto.setStartTime(this.startTime);
        dto.setEndTime(this.endTime);
        dto.setTeamName(this.teamName);
        dto.setNumberOfMembers(this.numberOfMembers);
        dto.setCreatedAt(this.getCreatedAt());
        dto.setRecruitmentStatusType(this.recruitmentStatus);
        dto.setTeamApplicants(this.makeApplicantDto());
        return dto;
    }

    @Override
    public void delete() {
        this.getTeamApplicants().forEach(TeamApplicant::delete);
        this.recruitmentStatus = RecruitmentStatusType.CANCELED;
        super.delete();
    }

    public void configureSkillLevel(SkillLevelList list){
        this.skillLevelBeginner = false;
        this.skillLevelLow = false;
        this.skillLevelMiddle = false;
        this.skillLevelHigh = false;

        if(list.isSkillLevelBeginner()) this.skillLevelBeginner = true;
        if(list.isSkillLevelLow()) this.skillLevelLow = true;
        if(list.isSkillLevelMiddle()) this.skillLevelMiddle = true;
        if(list.isSkillLevelHigh()) this.skillLevelHigh = true;
    }

    // 글의 작성자 ID와 현재 로그인한 사용자 ID가 일치하는지 확인
    public boolean isCorrespondTo(long loginId){
        return this.writer.getId().equals(loginId);
    }
    public void connectApplicant(TeamApplicant teamApplicant){
        if(teamApplicants.stream().filter(applicant -> applicant.getApplyStatus() == ApplyStatusType.WAITING).count() > MAX_APPLICANTS){
            throw new BaseException(OVER_LIMITED_NUMBERS);
        }
        this.teamApplicants.add(teamApplicant);
    }
    public void setRecruitmentStatus(RecruitmentStatusType recruitmentStatus){
        //todo : 같은 모집 상태로 변경 시 예외 처리

        this.recruitmentStatus = recruitmentStatus;
    }

    public void connectParentUser(User user){ // * writerId를 User 객체로 대체할 것!
        this.writer = user;
        this.writer.getTeamMatchings().add(this);
    }

    // * 리스트 컬렉션에 저장된 TeamApplicant 객체를 ToApplicantDto로 변환하여 리스트로 반환하는 기능을 수행합니다.
    public List<ToApplicantDto> makeApplicantDto(){
        List<TeamApplicant> teamApplicants = getTeamApplicants();
        List<ToApplicantDto> dto = teamApplicants.stream().filter(teamApplicant -> teamApplicant.getIsDeleted() == false).map(TeamApplicant::makeDto).collect(Collectors.toList());
        return dto;
    }
}
