package sync.slamtalk.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatInboundInterceptor implements ChannelInterceptor {

    /**
     * 메세지 헤더에 존재하는 Authorization 으로 사용자 검증
     * 토큰 만료나 변조 시, 예외를 터트린다.
     */
    private final ChatErrorHandler chatErrorHandler;

    // 메세지가 전송되기 전에 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT.equals(headerAccessor.getCommand())){
            // TODO Token 검증
            // TEST 용도
            //log.debug("CONNECT 발생");
            //log.debug("message.getHeaders().toString() : {}",message.getHeaders().toString());
        }

        if(StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())){
            // TODO : 구독이 가능한 상태 인지 검증
        }


        if(StompCommand.SEND.equals(headerAccessor.getCommand())){
            // TODO : client 가 destination 에 메세지를 보낼 수 있는지 검증
            // Test 용도
            //String s = extractRoomId(headerAccessor.getDestination());
            //if(s.equals("2")){
            //    log.info("정상적인 접근");
            //}else{
            //    throw new RuntimeException("존재하지않는방"); // RuntimeException throw then Handler 가 받음
            //}
        }

        return message;

    }


    // 채팅방 ID 추출
    private String extractRoomId(String destination){
        // destination 경로를 '/' 로 분할하여 채팅방 ID 추출
        String[] pathSegments = destination.split("/");
        // 예 : /pub/chat/enter/1 에서 마지막 부분(1) 이 채팅방 ID 일 경우
        return pathSegments[pathSegments.length-1];
    }

    // 채팅방 존재 여부
    private boolean chatRoomExists(String chatRoomId){
        // repository 작업 후 테스트 진행할 예정
        return true;
    }


}
