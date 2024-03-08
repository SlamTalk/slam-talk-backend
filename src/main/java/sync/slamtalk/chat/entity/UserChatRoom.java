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

    // 채팅방 이름
    @Column(name = "chatroom_name")
    private String name;

    // 채팅방 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "chatroom_type")
    private RoomType roomType;

    // 농구장(courtId)
    @Column(name = "basketball_id")
    private Long BasketBallId;

    // 같이헤요(게시글Id)
    @Column(name = "together_id")
    private Long togetherId;

    // 팀매칭(게시글Id)
    @Column(name = "teamMatching_id")
    private Long teamMatchingId;

    @Column(name = "direct_id")
    private Long directId;


    @Column(name = "chatroom_img")
    private String imageUrl; // 채팅리스트에 뜨는 이미지


    // 사용자가 마지막으로 읽은 메세지의 아이디 값 저장
    @Column(name = "read_index")
    private Long readIndex;


    // 채팅방 입장 최초/재접속 판단
    @Column(name="isFirst")
    private Boolean isFirst = true; // 초기화


    /**
     * 유저 설정
     */
    public void setUsers(User user){
        this.user = user;
        if(!user.getUserChatRooms().contains(this)){
            user.getUserChatRooms().add(this);
        }
    }


    /**
     * 채팅방 설정
     */
    public void setChat(ChatRoom chat) {
        this.chat = chat;
        if (!chat.getUserChats().contains(this)) {
            chat.getUserChats().add(this);
        }
    }


    /**
     * 개인 채팅 아이디 설정
     */
    public void setDirectId(Long directId){
        this.directId = directId;
    }


    /**
     * readIndex 값 업데이트
     */
    public void updateReadIndex(Long newReadIndex) {
        this.readIndex = newReadIndex;
    }


    /**
     * 채팅방 입장 업데이트
     */
    public void updateIsFirst(Boolean isFirst){
        this.isFirst = isFirst;
    }

}