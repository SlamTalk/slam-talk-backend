package sync.slamtalk.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    /**
     * WebSocket Exception 처리
     */
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

        return switch (ex.getCause().getMessage()){
            // JWT 가 메세지에 포함된 경우
            case "JWT" -> handleJwtException(clientMessage, ex);
            // Auth 가 메세지에 포함된 경우
            case "Auth" -> handleUnauthorizedException(clientMessage, ex);
            // NFR 이 메세지에 포함된 경우
            case "NFR" -> handleNotFoundException(clientMessage, ex);
            default -> super.handleClientMessageProcessingError(clientMessage, ex);
        };
    }

    /**
     * 생성된 방이 없는 경우 에러 코드 반환
     */
    private Message<byte[]> handleNotFoundException(Message<byte[]> clientMessage, Throwable ex) {
        return prepareErrorMessage(RoomCode.NOT_FOUND_ROOM);
    }


    /**
     * 방에 대한 권한이 없는 경우 처리 하는 메소드
     */
    private Message<byte[]> handleUnauthorizedException(Message<byte[]> clientMessage, Throwable ex) {
        return prepareErrorMessage(RoomCode.NOT_PARTICIPATE_ROOM);
    }


    /**
     * JWT 예외
     */
    private Message<byte[]> handleJwtException(Message<byte[]> clientMessage, Throwable ex) {
        return prepareErrorMessage(RoomCode.NO_PERMISSION);
    }

    /**
     * 메세지 작성
     */
    private Message<byte[]> prepareErrorMessage(RoomCode roomCode) {
        String code = roomCode.getMessage();
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        // STOMP message 설정
        accessor.setMessage(String.valueOf(roomCode.getCode()));
        accessor.setLeaveMutable(true);// 메세지가 생성된 후에도 값을 변경할 수 있도록 가변성을 준것
        return MessageBuilder.createMessage(code.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}
