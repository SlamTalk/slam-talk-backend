package sync.slamtalk.chat.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.RoomType;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.request.UserSignUpReq;
import sync.slamtalk.user.entity.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Slf4j
class UserChatRoomRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserChatRoomRepository userChatRoomRepository;

    public String email = "test1@naver.com";
    public String password = "123@password!";
    public String nickname = "nickname";


    @BeforeEach
    void beforeSet(){
        // user
        User user = User.of(email, password, nickname);
        entityManager.persist(user);

        // chatroom
        ChatRoom c1 = ChatRoom.builder()
                .name("농구방")
                .roomType(RoomType.BASKETBALL)
                .build();
        ChatRoom c2 = ChatRoom.builder()
                .name("같이해요방")
                .roomType(RoomType.TOGETHER)
                .build();
        ChatRoom c3 = ChatRoom.builder()
                .name("팀매칭")
                .roomType(RoomType.MATCHING)
                .build();

        entityManager.persist(c1);
        entityManager.persist(c2);
        entityManager.persist(c3);

        // userChatRoom
        UserChatRoom usc1 = UserChatRoom.builder()
                .user(user)
                .chat(c1)
                .readIndex(0L)
                .build();
        UserChatRoom usc2 = UserChatRoom.builder()
                .user(user)
                .chat(c2)
                .readIndex(0L)
                .build();
        UserChatRoom usc3 = UserChatRoom.builder()
                .user(user)
                .chat(c3)
                .readIndex(0L)
                .build();
        entityManager.persist(usc1);
        entityManager.persist(usc2);
        entityManager.persist(usc3);

    }

    @Test
    void findByChat_Id() {
        List<UserChatRoom> userChatRoomList = userChatRoomRepository.findByChat_Id(1L);

        for(UserChatRoom ucr : userChatRoomList){
            System.out.println("채팅방이름: "+ucr.getName());
        }

        assertTrue(!userChatRoomList.isEmpty());
    }

    @Test
    void findByUser_Id() {
        List<UserChatRoom> userChatRoomRepositoryByUserId = userChatRoomRepository.findByUser_Id(1L);
        assertEquals(userChatRoomRepositoryByUserId.size(), 3);

    }

    @Test
    void findByUserChatroom() {
        Optional<UserChatRoom> byUserChatroom = userChatRoomRepository.findByUserChatroom(1L, 3L);
        assertTrue(byUserChatroom.isPresent(),"존재합니다");

    }
}