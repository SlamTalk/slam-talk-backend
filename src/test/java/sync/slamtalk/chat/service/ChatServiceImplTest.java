package sync.slamtalk.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.RoomType;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.repository.ChatRoomRepository;
import sync.slamtalk.chat.repository.UserChatRoomRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.request.UserSignUpReq;
import sync.slamtalk.user.entity.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Slf4j
@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserChatRoomRepository userChatRoomRepository;

    @InjectMocks
    private ChatServiceImpl chatService;
    @Test
    @DisplayName("팀매칭 완료 시 UserChatRoom에 User 리스트 추가 테스트")
    void setUserListChatRoom() {
        User entity1 = new UserSignUpReq("test1@naver.com", "password", "hi1").toEntity();
        User entity2 = new UserSignUpReq("test2@naver.com", "password", "hi2").toEntity();
        User entity3 = new UserSignUpReq("test3@naver.com", "password", "hi3").toEntity();

        entity1.testSetUserId(1L); // 수동으로 ID 설정
        entity2.testSetUserId(1L); // 수동으로 ID 설정
        entity3.testSetUserId(1L); // 수동으로 ID 설정

        ChatRoom chatRoom = ChatRoom.builder()
                .id(1L)
                .name("하이")
                .roomType(RoomType.TOGETHER)
                .userChats(new HashSet<>())
                .build();
        List<User> userList = List.of(entity1, entity2, entity3);
        List<UserChatRoom> userChatRooms = userList.stream().map(user -> {
            // UserChatRoom 생성
            UserChatRoom userChatRoom = UserChatRoom.builder()
                    .readIndex(0L) // 초기화
                    .isFirst(true) // 초기화
                    .chat(chatRoom)
                    .user(user)
                    .build();

            // ChatRoom 에도 userchatRoom 추가
            chatRoom.addUserChatRoom(userChatRoom);
            return userChatRoom;
        }).toList();

        Mockito.when(chatRoomRepository.findById(chatRoom.getId()))
                .thenReturn(Optional.of(chatRoom));
        Mockito.when(userRepository.findAllById(List.of(entity1.getId(), entity2.getId(), entity3.getId())))
                .thenReturn(userList);
        Mockito.when(userChatRoomRepository.saveAll(Mockito.any(List.class)))
                .thenReturn(userChatRooms);


        chatService.setUserListChatRoom(chatRoom.getId(), List.of(entity1.getId(), entity2.getId(), entity3.getId()));

    }
}