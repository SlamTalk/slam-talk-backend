package sync.slamtalk.mate.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sync.slamtalk.common.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class MatePost extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "mate_post_id")
        private long id;

        @Column(nullable = false)
        private String title; // 글 제목

        @Column(nullable = false)
        private String content; // 글 내용

        @Column(nullable = false, name="skill_level_wanted")
        private String skill_level; // 원하는 스킬 레벨 "초보", "중수", "고수", "무관"

        @Column(nullable = false, name="scheduled_time")
        private LocalDateTime scheduledTime; // 예정된 시간

        @Column(nullable = false, name="location")
        private String location; // 지역 * 추가적인 정의 필요. 서비스 지역을 어떻게 한정 지을지, 지역 단위를 어떻게 할지 등등...

        @Column(nullable = true, name="chat_room_id") // 채팅방 아이디 * 매핑 필요
        private long chatRoomId;

        @Column(nullable = false, name="soft_delete")
        private boolean softDelete; // 삭제 여부

        @Column(nullable = false, name="recruitment_status")
        private boolean isClosed; // 모집 마감 여부

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

        @JsonIgnore
        @OneToMany(mappedBy = "matePost", cascade = CascadeType.ALL)
        private List<Participant> participants; // 참여자 목록


        public MatePost() {

        }
}
