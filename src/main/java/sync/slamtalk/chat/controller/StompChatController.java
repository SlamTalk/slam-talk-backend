package sync.slamtalk.chat.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import sync.slamtalk.chat.dto.ChatMessageDTO;

@Controller
@RequiredArgsConstructor
public class StompChatController {


    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

    // Client 가 SEND 할 수 있는 경로
    /*stompConfig에서 설정한 applicationDestinationPrefixes와
     @MessageMapping 경로가 병합됨*/

    // "/pub/chat/room" 로 날린 데이터에 대해서
    // "/pub/chat/room/roomId" 로 구독자들에게 해당 메세지 전달
    @MessageMapping(value = "/chat/enter/{roomId}")
    @SendTo("/sub/chat/room/{roomId}")
    public ChatMessageDTO enter(ChatMessageDTO message){
        message.setContent(message.getSenderId()+"님이 채팅방에 참여하였습니다.");
        return message;
    }


    // "/pub/chat/message" 로 날린 데이터에 대해서
    // "/sub/chat/room + roomId" 로 구독자들에게 해당 message를 전달
    @MessageMapping("/chat/message/{roomId}")
    @SendTo("/sub/chat/room/{roomId}")
    public ChatMessageDTO message(ChatMessageDTO message){
        return message;
    }


}
