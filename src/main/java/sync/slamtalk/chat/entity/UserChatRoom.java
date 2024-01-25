package sync.slamtalk.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.user.entity.User;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "user_chatroom")
public class UserChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_chatroom_id")
    private Long id; // 식별 아이디

    // 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;


    // 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id",nullable = false)
    private ChatRoom chat;


    // 사용자가 마지막으로 읽은 메세지의 아이디 값 저장
    @Column(name = "read_index")
    private Long readIndex;


    // 채팅방 입장 최초/재접속 판단
    @Column(name="isFirst")
    private Boolean isFirst;


    public void setUsers(User user){
        this.user = user;
        if(!user.getUserChatRooms().contains(this)){
            user.getUserChatRooms().add(this);
        }
    }


    // 채팅방 설정
    public void setChat(ChatRoom chat) {
        this.chat = chat;
        if (!chat.getUserChats().contains(this)) {
            chat.getUserChats().add(this);
        }
    }


    // readIndex 값 업데이트하기
    public void updateReadIndex(Long newReadIndex) {
        this.readIndex = newReadIndex;
    }
}