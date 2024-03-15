package sync.slamtalk.mate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.mate.dto.PositionListDto;

import java.util.List;
import java.util.Objects;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_table_id")
    private Long id; // 참여자 테이블 아이디

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_post_id")
    private MatePost matePost; // 참여자가 참여한 글

    @Column(nullable = false, name = "participant_id")
    private Long participantId;

    @Column(nullable = false)
    private String participantNickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplyStatusType applyStatus; // 참여자 신청 상태 "WAITING", "ACCEPTED", "REJECTED", "CANCELED"

    private PositionType position; // 포지션 "CENTER", "GUARD", "FORWARD", "UNSPECIFIED", * 향후에 통일된 Enum으로 변경 예정

    private SkillLevelType skillLevel; // 스킬 레벨  HIGH, MIDDLE, LOW, BEGINNER

    public Participant(long participantId, String participantNickname, PositionType position, SkillLevelType skillLevel, MatePost post) {

        this.participantId = participantId;
        this.participantNickname = participantNickname;
        this.position = position;
        this.skillLevel = skillLevel;
        this.applyStatus = ApplyStatusType.WAITING;
        this.connectParent(post);
    }

    public void updateApplyStatus(ApplyStatusType applyStatus) {
        this.applyStatus = Objects.requireNonNull(applyStatus);
    }

    public void softDeleteParticipant() {
        this.delete();
    }

    public void connectParent(MatePost matePost) {
        this.matePost = matePost;
        matePost.getParticipants().add(this);
    }

    public boolean isCorrespondTo(Long userId) {
        return this.participantId.equals(userId);
    }

    public void disconnectParent() {
        this.matePost.getParticipants().remove(this);
        this.matePost = null;
    }

    public boolean checkCapabilities(List<PositionListDto> requiredPosition, List<String> requiredSkillLevel) {
//        if(requiredSkillLevel.contains(this.skillLevel.getLevel()) == false){
//            return false;
//        }
//        for(PositionListDTO positionListDTO : requiredPosition){
//            if(positionListDTO.getPosition().equals(this.position.getPosition())){
//                if(positionListDTO.getMaxPosition() - positionListDTO.getCurrentPosition() <= 0){
//                    return false;
//                }
//            }
//        }
        return true;
    }

    @Override
    public String toString() { // 양방향 연관 관계로 인한 순환 참조 고려한 toString
        return "Participant{" +
                "participantTableId=" + id +
                ", participantId='" + participantId + '\'' +
                ", participantNickname='" + participantNickname + '\'' +
                ", applyStatus=" + applyStatus +
                ", position=" + position +
                ", skillLevel=" + skillLevel +
                '}';
    }
}
