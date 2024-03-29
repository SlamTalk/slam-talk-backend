package sync.slamtalk.chat.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.chat.dto.request.ChatMessageDTO;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.repository.UserChatRoomRepository;
import sync.slamtalk.chat.service.ChatServiceImpl;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompChatController {
    private final ChatServiceImpl chatService;
    private final UserChatRoomRepository userChatRoomRepository;

    /**
     * 입장/퇴장 메세지 발행
     * "/pub/chat/bot/roomId" 로 날린 데이터에 대해서
     * "/sub/chat/bot/roomId" 로 구독자들(클라이언트)에게 해당 message 를 전달
     * - 입장 : 첫접속/ 재접속 인지 확인하고 입장 메세지 발행
     * - 퇴장 : 퇴장 메세지 발행
     */
    @MessageMapping(value = "/chat/bot/{roomId}") // 발행
    @SendTo("/sub/chat/bot/{roomId}") // 수신
    @Transactional
    public String enter(ChatMessageDTO message) {


        Long userId = message.getSenderId();
        String stringRoomId = message.getRoomId();
        long roomId = Long.parseLong(stringRoomId);

        // 퇴장
        if (message.getContent() != null) {
            if (message.getContent().equals("EXIT")) {
                Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findByUserChatroom(userId, roomId);

                if (optionalUserChatRoom.isEmpty()) {
                    log.debug("해당 유저는 해당 채팅방에 참여하고 있지 않음");
                }
                if (optionalUserChatRoom.isPresent()) {
                    log.debug("현재 유저가 가지고 있는 방의 상태 : {}", optionalUserChatRoom.get().getChat().getIsDeleted());
                    log.debug("{}번 유저가 {}번째 채팅방에서 나가기를 시도", userId, optionalUserChatRoom.get().getChat().getId());
                    UserChatRoom userChatRoom = optionalUserChatRoom.get();
                    userChatRoom.delete(); // softDelete
                }

                return message.getSenderNickname() + " 님이 퇴장하셨습니다.";
            }
        }

        // 입장
        Optional<Boolean> visitedFirst = chatService.isVisitedFirst(userId, roomId);
        // 방문한적이 없다면 문구 리턴
        if (visitedFirst.isPresent()) {
            Boolean visited = visitedFirst.get();
            if (visited.equals(Boolean.TRUE)) {
                return message.getSenderNickname() + " 님이 입장하셨습니다.";
            }
        }

        // 방문한적이 있다면 빈문자열로 리턴
        return "";
    }

    /**
     * 메세지 발행
     * "/pub/chat/message" 로 날린 데이터에 대해서
     * "/sub/chat/room/roomId" 로 구독자들(클라이언트)에게 해당 message 를 전달
     */
    @MessageMapping("/chat/message/{roomId}")
    @SendTo("/sub/chat/room/{roomId}")
    public ChatMessageDTO message(ChatMessageDTO message) {
        // TODO
        // chatService.saveMessage(message);
        return message;
    }


    /**
     * 뒤로 가기 메세지 발행(🌟readIndex update🌟)
     * "/pub/chat/back" 으로 날린 데이터에 대해서
     * "/sub/chat/room/roomId" 로 구독자(클라이언트)들에게 해당 message 를 전달
     * ChatInboundInterceptor 에서 readIndex 가 업데이트 되도록 함
     */
    @MessageMapping("/chat/back/{roomId}")
    @SendTo("/sub/chat/back/{roomId}")
    public ChatMessageDTO back(ChatMessageDTO message) {
        return message;
    }


}
