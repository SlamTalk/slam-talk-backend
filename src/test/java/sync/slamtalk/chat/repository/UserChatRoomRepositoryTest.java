package sync.slamtalk.chat.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.RoomType;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.user.dto.UserSignUpRequestDto;
import sync.slamtalk.user.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class UserChatRoomRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserChatRoomRepository userChatRoomRepository;

    @Test
    void findByChat_Id() {
    }

    @Test
    void findByUser_Id() {
    }

    @Test
    void findByUserChatroom() {
        // user

//        UserSignUpRequestDto usd=new UserSignUpRequestDto();
//        User user = User.from(usd);
//        entityManager.persist(user);


        // chatroom
//        ChatRoom c1 = ChatRoom.builder()
//                .name("농구방")
//                .roomType(RoomType.BASKETBALL)
//                .build();
//        ChatRoom c2 = ChatRoom.builder()
//                .name("같이해요방")
//                .roomType(RoomType.TOGETHER)
//                .build();
//        ChatRoom c3 = ChatRoom.builder()
//                .name("팀매칭")
//                .roomType(RoomType.MATCHING)
//                .build();
//
//        entityManager.persist(c1);
//        entityManager.persist(c2);
//        entityManager.persist(c3);
//
//        // userChatRoom
//        UserChatRoom usc1 = UserChatRoom.builder()
//                .user(user)
//                .chat(c1)
//                .readIndex(0L)
//                .build();
//        UserChatRoom usc2 = UserChatRoom.builder()
//                .user(user)
//                .chat(c2)
//                .readIndex(0L)
//                .build();
//        UserChatRoom usc3 = UserChatRoom.builder()
//                .user(user)
//                .chat(c3)
//                .readIndex(0L)
//                .build();
//        entityManager.persist(usc1);
//        entityManager.persist(usc2);
//        entityManager.persist(usc3);
//
//        Optional<UserChatRoom> userChatRoom = userChatRoomRepository.findByUserChatroom(user.getId(), c2.getId());
//        assertTrue(userChatRoom.isPresent(),"존재합니다");


    }
}