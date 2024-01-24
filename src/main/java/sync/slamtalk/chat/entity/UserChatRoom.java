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
    private UserMock user;


    // 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id",nullable = false)
    private ChatRoom chat;


    // 사용자가 마지막으로 읽은 메세지의 아이디 값 저장
    @Column(name = "read_index")
    private Long readIndex;


    // 유저 설정
    public void setUser(UserMock user) {
        this.user = user;
        if (!user.getUserChats().contains(this)) {
            user.getUserChats().add(this);
        }
    }

    // TODO 봉승님 유저로 편의메서드 작성
    // 생각해보니까 채팅방 리스트가 유저정보에서 관리가 되어야됨(양방향 매핑해야할듯)
    // 봉승님 코드에서 cascade = CascadeType.ALL, orphanRemoval = true 추가 요청
    public void setUsers(User user){
//        this.user = user;
//        if(!user.getUserChatRooms().contains(this)){
//            user.getUserChatRooms().add(this);
//        }
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