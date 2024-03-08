package sync.slamtalk.chat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "messages")
public class Messages extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id; // 식별 아이디

    // 작성자
    @Column(name = "sender_nickname")
    private String senderNickname;

    // 작성자 아이디
    @Column(name = "sender_id")
    private Long senderId;

    // 메세지(내용)
    @Column(name = "content")
    private String content;

    // 메세지(작성시간)
    @Column(name = "creation_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String creationTime;

    // 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom; // 채팅방 참조
}
