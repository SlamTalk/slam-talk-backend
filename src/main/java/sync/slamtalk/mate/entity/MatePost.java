package sync.slamtalk.mate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.common.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        @Column(nullable = false, name="writer_id")
        private long writerId; // 글 작성자 아이디 * User 테이블과 매핑 필요

        @Column(nullable = true, name="location_detail")
        private String locationDetail; // 상세 시합 장소

        @Column(nullable = false)
        private String title; // 글 제목

        @Column(nullable = false)
        private String content; // 글 내용

        @Column(nullable = false, name="skill_level_type")
        @Enumerated(EnumType.STRING)
        private SkillLevelType skillLevel; // 원하는 스킬 레벨 "BEGINNER", "INTERMEDIATE", "MASTER", "IRRELEVANT"

        @Column(nullable = false, name="scheduled_time")
        private LocalDateTime scheduledTime; // 예정된 시간

        @Column(nullable = true, name="chat_room_id") // 채팅방 아이디 * 매핑 필요
        private long chatRoomId;

        @Column(nullable = false, name="soft_delete")
        private boolean softDelete; // 삭제 여부

        @Column(nullable = false, name="recruitment_status_type")
        @Enumerated(EnumType.STRING)
        private RecruitmentStatusType recruitmentStatus; // 모집 마감 여부 "RECRUITING", "COMPLETED", "CANCEL"

        @Column(nullable = false, name="max_participants")
        private int maxParticipants; // 최대 참여 인원

        @Column(nullable = false, name="current_participants")
        private int currentParticipants; // 현재 참여 인원

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

        public void addParticipant(Participant participant){
                this.participants.add(participant);
        }

        /**
         * 메이트찾기 게시글 삭제
         * soft delete
         * @return 게시글을 삭제(soft delete)하는데 성공하면 true 반환
         */
        public boolean softDeleteMatePost(){
                softDeleteParticipantAll();
                this.softDelete = true;
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


        public void updateTitle(String title){
                this.title = title;
        }

        public void updateContent(String content){
                this.content = content;
        }

        public void updateScheduledTime(LocalDateTime scheduledTime){
                this.scheduledTime = scheduledTime;
        }

        public void updateLocationDetail(String locationDetail){
                this.locationDetail = locationDetail;
        }

        public void updateSkillLevel(SkillLevelType skillLevel){
                this.skillLevel = skillLevel;
        }


        public void updateMaxParticipants(int maxParticipants){
                this.maxParticipants = maxParticipants;
        }

        public void updateMaxParticipantsForwards(int maxParticipantsForwards){
                this.maxParticipantsForwards = maxParticipantsForwards;
        }

        public void updateMaxParticipantsCenters(int maxParticipantsCenters){
                this.maxParticipantsCenters = maxParticipantsCenters;
        }

        public void updateMaxParticipantsGuards(int maxParticipantsGuards){
                this.maxParticipantsGuards = maxParticipantsGuards;
        }

        public void updateMaxParticipantsOthers(int maxParticipantsOthers){
                this.maxParticipantsOthers = maxParticipantsOthers;
        }

}
