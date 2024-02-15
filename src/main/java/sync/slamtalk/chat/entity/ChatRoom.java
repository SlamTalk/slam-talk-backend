package sync.slamtalk.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.map.entity.BasketballCourt;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "chatroom")
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chatroom_id") // 식별 아이디
    private Long id;


    @OneToOne(mappedBy = "chatroom", fetch = FetchType.LAZY)
    private BasketballCourt basketballCourt;


    @Column(name = "chatroom_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType; // 다이렉트메세지, 농구장, 같이해요, 팀매칭


    @Column(name = "chatroom_name")
    private String name; // 방 제목


    // 사용자는 여러개의 채팅방을 가질 수 있음
    @OneToMany(mappedBy = "chat",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserChatRoom> userChats = new HashSet<>();


    // UserChatRoom 추가
    public void addUserChatRoom(UserChatRoom userChatRoom) {
        this.userChats.add(userChatRoom);
        userChatRoom.setChat(this);
    }


    // chatroom 에 basketball 설정
    public void setBasketballCourt(BasketballCourt basketballCourt) {
        this.basketballCourt = basketballCourt;
    }

}
