package sync.slamtalk.mate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.common.BaseException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static sync.slamtalk.mate.error.MateErrorResponseCode.DECREASE_POSITION_NOT_AVAILABLE;
import static sync.slamtalk.mate.error.MateErrorResponseCode.INCREASE_POSITION_NOT_AVAILABLE;

@Entity
@Getter
@Table(name = "matepost")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatePost extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "mate_post_id")
        private long matePostId;

        //@ManyToOne(fetch = FetchType.LAZY)
        //@JoinColumn(nullable = false, name="writer_id")
        @Column(name="writer_id")
        private long writerId; // 글 작성자 아이디 * User 테이블과 매핑 필요

        @Column(nullable = true, name="location_detail")
        private String locationDetail; // 상세 시합 장소

        @Column(nullable = false)
        private String title; // 글 제목

        @Column(nullable = false)
        private String content; // 글 내용

        @Column(nullable = false, name="skill_level_type")
        @Enumerated(EnumType.STRING)
        private RecruitedSkillLevelType skillLevel; // 원하는 스킬 레벨 범위 BEGINNER, OVER_BEGINNER, UNDER_LOW, OVER_LOW, UNDER_MIDDLE, OVER_MIDDLE, UNDER_HIGH, HIGH

        @Column(nullable = false, name="start_scheduled_time")
        private LocalDateTime startScheduledTime; // 예정된 시작 시간

        @Column(nullable = false, name="end_scheduled_time")
        private LocalDateTime endScheduledTime; // 예정된 종료 시간

        @Column(nullable = true, name="chat_room_id") // 채팅방 아이디 * 매핑 필요
        private long chatRoomId;

        @Column(nullable = false, name="recruitment_status_type")
        @Enumerated(EnumType.STRING)
        private RecruitmentStatusType recruitmentStatus; // 모집 마감 여부 "RECRUITING", "COMPLETED", "CANCEL"

        @Column(nullable = false, name="max_participants_forward")
        private int maxParticipantsForwards; // 포워드 최대 참여 인원

        @Column(nullable = false, name="current_participants_forward")
        private int currentParticipantsForwards; // 포워드 현재 참여 인원

        @Column(nullable = false, name="max_participants_center")
        private int maxParticipantsCenters; // 센터 최대 참여 인원

        @Column(nullable = false, name="current_participants_center")
        private int currentParticipantsCenters; // 센터 현재 참여 인원

        @Column(nullable = false, name="max_participants_guard")
        private int maxParticipantsGuards; // 가드 최대 참여 인원

        @Column(nullable = false, name="current_participants_guard")
        private int currentParticipantsGuards; // 가드 현재 참여 인원

        @Column(nullable = false, name="max_participants_others")
        private int maxParticipantsOthers; // 모집 포지션 무관 최대 참여 인원

        @Column(nullable = false, name="current_participants_others")
        private int currentParticipantsOthers; // 모집 포지션 무관 현재 참여 인원

        @OneToMany(mappedBy = "matePost" , cascade = CascadeType.ALL)
        private List<Participant> participants = new ArrayList<>(); // 참여자 목록


        /**
         * 메이트찾기 게시글 삭제
         * soft delete
         * @return 게시글을 삭제(soft delete)하는데 성공하면 true 반환
         */
        public boolean softDeleteMatePost(){
                softDeleteParticipantAll();
                this.delete();
                return true;
        }

        /**
         * 메이트찾기 게시글에 속한 참여자 목록 삭제한다.(글 작성자는 참여자 목록에 속하지 않음)
         * soft delete
         * @return soft delete 실패 시 false 반환
         */
        public boolean softDeleteParticipantAll(){
                for(Participant participant : participants){
                        if(!participant.softDeleteParticipant()){
                                return false;
                        }
                }
                return true;
        }

        //todo: User의 연관관계 컬렉션 필드 생성 시 수정 필요
//        public boolean connectParent(User user){
//                this.user = user;
//                if(!user.getMatePosts().contains(this)){
//                        user.getMatePosts().add(this);
//                        return true;
//                }
//                return false;
//        }

        //todo : convertToSkillLevelList() 메서드 구현 필요

        public void updateTitle(String title){
                this.title = title;
        }

        public void updateContent(String content){
                this.content = content;
        }

        public void updateStartScheduledTime(LocalDateTime scheduledTime){
                this.startScheduledTime = scheduledTime;
        }

        public void updateEndScheduledTime(LocalDateTime scheduledTime){
                this.endScheduledTime = scheduledTime;
        }

        public void updateLocationDetail(String locationDetail){
                this.locationDetail = locationDetail;
        }

        public void updateSkillLevel(RecruitedSkillLevelType skillLevel){
                this.skillLevel = skillLevel;
        }


        public void updateMaxParticipantsForwards(int maxParticipantsForwards){
                this.maxParticipantsForwards = maxParticipantsForwards;
        }

        public void updateCurrentParticipantsForwards(int currentParticipantsForwards){
                this.currentParticipantsForwards = currentParticipantsForwards;
        }

        public void updateMaxParticipantsCenters(int maxParticipantsCenters){
                this.maxParticipantsCenters = maxParticipantsCenters;
        }

        public void updateCurrentParticipantsCenters(int currentParticipantsCenters){
                this.currentParticipantsCenters = currentParticipantsCenters;
        }

        public void updateMaxParticipantsGuards(int maxParticipantsGuards){
                this.maxParticipantsGuards = maxParticipantsGuards;
        }

        public void updateCurrentParticipantsGuards(int currentParticipantsGuards){
                this.currentParticipantsGuards = currentParticipantsGuards;
        }

        public void updateMaxParticipantsOthers(int maxParticipantsOthers){
                this.maxParticipantsOthers = maxParticipantsOthers;
        }

        public void updateCurrentParticipantsOthers(int currentParticipantsOthers){
                this.currentParticipantsOthers = currentParticipantsOthers;
        }

        public ApiResponse increasePositionNumbers(PositionType position){
                switch(position){
                        case CENTER:
                                if(getCurrentParticipantsCenters() >= getMaxParticipantsCenters()){
                                        throw new BaseException(INCREASE_POSITION_NOT_AVAILABLE);
                                }else{
                                        updateCurrentParticipantsCenters(getCurrentParticipantsCenters() + 1);
                                }
                                break;
                        case GUARD:
                                if(getCurrentParticipantsGuards() >= getMaxParticipantsGuards()){
                                        throw new BaseException(INCREASE_POSITION_NOT_AVAILABLE);
                                }else{
                                        updateCurrentParticipantsGuards(getCurrentParticipantsGuards() + 1);
                                }
                                break;
                        case FORWARD:
                                if(getCurrentParticipantsForwards() >= getMaxParticipantsForwards()){
                                        throw new BaseException(INCREASE_POSITION_NOT_AVAILABLE);
                                }else{
                                        updateCurrentParticipantsForwards(getCurrentParticipantsForwards() + 1);
                                }
                                break;
                        case UNSPECIFIED:
                                if(getCurrentParticipantsOthers() >= getMaxParticipantsOthers()){
                                        throw new BaseException(INCREASE_POSITION_NOT_AVAILABLE);
                                }else{
                                        updateCurrentParticipantsOthers(getCurrentParticipantsOthers() + 1);
                                }
                                break;
                }
                return ApiResponse.ok("성공적으로 인원을 늘렸습니다.");
        }


        public ApiResponse reducePositionNumbers(PositionType position){
                switch(position){
                        case CENTER:
                                if(getCurrentParticipantsCenters() == 0){
                                        throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
                                }else{
                                        updateCurrentParticipantsCenters(getCurrentParticipantsCenters() - 1);
                                }
                                break;
                        case GUARD:
                                if(getCurrentParticipantsGuards() == 0){
                                        throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
                                }else{
                                        updateCurrentParticipantsGuards(getCurrentParticipantsGuards() - 1);
                                }
                                break;
                        case FORWARD:
                                if(getCurrentParticipantsForwards() == 0){
                                        throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
                                }else{
                                        updateCurrentParticipantsForwards(getCurrentParticipantsForwards() - 1);
                                }
                                break;
                        case UNSPECIFIED:
                                if(getCurrentParticipantsOthers() == 0){
                                        throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
                                }else{
                                        updateCurrentParticipantsOthers(getCurrentParticipantsOthers() - 1);
                                }
                                break;
                }
                return ApiResponse.ok("성공적으로 인원을 줄였습니다.");
        }

        // todo : 참여자 목록의 최대 포지션 인원을 조정할 때 현재 인원보다 낮게 조정하지 못하도록 하는 기능 구현 필요
        // todo : 참여자 목록의 최대 포지션 인원을 일정 수치 이상 올리지 못하도록 하는 기능 구현 필요

        // todo : 해당 모집 글의 요구 실력에 미달하거나 초과할 경우 참여자 목록에 추가하지 못하도록 하는 기능 구현 필요

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                MatePost matePost = (MatePost) o;
                return matePostId == matePost.matePostId && Objects.equals(title, matePost.title);
        }

        @Override
        public int hashCode() {
                return Objects.hash(matePostId, title);
        }

        @Override
        public String toString() {
                return "MatePost{" +
                        "matePostId=" + matePostId +
                        ", locationDetail='" + locationDetail + '\'' +
                        ", title='" + title + '\'' +
                        ", content='" + content + '\'' +
                        ", skillLevel=" + skillLevel +
                        ", startScheduledTime=" + startScheduledTime +
                        ", endScheduledTime=" + endScheduledTime +
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


        public LocalDateTime getCreatedAt() {
                return super.getCreatedAt();
        }
}
