package sync.slamtalk.chat.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import sync.slamtalk.chat.dto.Request.ChatMessageDTO;
import sync.slamtalk.chat.service.ChatServiceImpl;

@Controller
@RequiredArgsConstructor
public class StompChatController {


    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
    private final ChatServiceImpl chatService;

    // Client 가 SEND 할 수 있는 경로
    /*stompConfig에서 설정한 applicationDestinationPrefixes와
     @MessageMapping 경로가 병합됨*/

    // "/pub/chat/room" 로 날린 데이터에 대해서
    // "/pub/chat/room/roomId" 로 구독자들에게 해당 메세지 전달
    @MessageMapping(value = "/chat/enter/{roomId}")
    @SendTo("/sub/chat/room/{roomId}")
    public ChatMessageDTO enter(ChatMessageDTO message){
        return message;
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
