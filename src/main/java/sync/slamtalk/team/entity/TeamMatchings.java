package sync.slamtalk.team.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "teammatchinglist")
@Getter
public class TeamMatchings extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long teamMatchingId;

    @Column(name = "writer_id")
    private long writerId; // * 작성자 User 객체로 변경해야 함

    private long opponentId; // * 상대방 User 객체로 변경해야 함

    private String teamName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String locationDetail;

    @Column(nullable = false)
    private int numberOfMembers;

    @Enumerated(EnumType.STRING)
    private RecruitedSkillLevelType skillLevel;

    @Column(nullable = false)
    private LocalDateTime startScheduledTime;

    @Column(nullable = false)
    private LocalDateTime endScheduledTime;


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
        TeamMatchings that = (TeamMatchings) o;
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

    @Override
    public void delete() {
        super.delete();
    }

    // 글의 작성자 ID와 현재 로그인한 사용자 ID가 일치하는지 확인
    public boolean isCorrespondTo(long loginId){
        return this.teamMatchingId == loginId;
    }

}
