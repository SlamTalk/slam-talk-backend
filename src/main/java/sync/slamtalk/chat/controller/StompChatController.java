package sync.slamtalk.chat.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import sync.slamtalk.chat.dto.Request.ChatMessageDTO;
import sync.slamtalk.chat.service.ChatServiceImpl;
import sync.slamtalk.user.UserRepository;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompChatController {


    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
    private final ChatServiceImpl chatService;
    private final UserRepository userRepository;

    // Client 가 SEND 할 수 있는 경로
    /*stompConfig에서 설정한 applicationDestinationPrefixes와
     @MessageMapping 경로가 병합됨*/

    // "/pub/chat/room" 로 날린 데이터에 대해서
    // "/pub/chat/room/roomId" 로 구독자들에게 해당 메세지 전달
    @MessageMapping(value = "/chat/enter/{roomId}")
    @SendTo("/sub/chat/room/{roomId}")
    public String enter(ChatMessageDTO message){
        Long userId = message.getSenderId();
        String StringRoomId = message.getRoomId();
        long roomId = Long.parseLong(StringRoomId);

        Optional<Boolean> visitedFirst = chatService.isVisitedFirst(userId,roomId);
        // 방문한적이 없다면 문구 리턴
        if(visitedFirst.isPresent()){
            Boolean visited = visitedFirst.get();
            if(visited.equals(Boolean.TRUE)){
                return message.getSenderNickname() + "님이 입장하셨습니다.";
            }
        }
        // 방문한적이 있다면 빈문자열로 리턴
        return "";
    }


    // "/pub/chat/message" 로 날린 데이터에 대해서
    // "/sub/chat/room/roomId" 로 구독자들에게 해당 message 를 전달
    @MessageMapping("/chat/message/{roomId}")
    @SendTo("/sub/chat/room/{roomId}")
    public ChatMessageDTO message(ChatMessageDTO message){
        return message;
    }


    // 뒤로가기
    @MessageMapping("/chat/back/{roomId}")
    @SendTo("/sub/chat/back/{roomId}")
    public ChatMessageDTO back(ChatMessageDTO message){
        return message;
    }

    // 나가기
    @MessageMapping("/chat/exit/{roomId}")
    @SendTo("/sub/chat/exit/{roomId}")
    public ChatMessageDTO exit(ChatMessageDTO message){
        return message;
    }

}
