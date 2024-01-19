package sync.slamtalk.chat.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
public class UserMock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserChatRoom> userChats = new HashSet<>();


    // UserChatRoom 추가를 위한 편의 메소드
    public void addUserChatRoom(UserChatRoom userchatRoom){
        // 사용자채팅방을 모아놓는 set 에 userchatRoom 추가
        userChats.add(userchatRoom);
        // 사용자채팅방에 유저를 자기자신으로 설정
        userchatRoom.setUser(this);
    }

}
