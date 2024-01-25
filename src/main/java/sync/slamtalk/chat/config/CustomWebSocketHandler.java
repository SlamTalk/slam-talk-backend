package sync.slamtalk.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

@Slf4j
@Component

public class CustomWebSocketHandler extends WebSocketHandlerDecorator {
    private final WebSocketHandler delegate;

    @Autowired
    public CustomWebSocketHandler(WebSocketHandler delegate) {
        super(delegate);
        this.delegate = delegate;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session,exception);

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        //super.afterConnectionClosed(session,closeStatus);
        log.info("웹 소켓 연결 종료");



        try{
            super.afterConnectionClosed(session,closeStatus);
            // 여기에 연결 종료 시 필요한 리소스 정리 로직을 추가
        }catch (TaskRejectedException e){
            log.warn("Task was rejected due to server shutdown. Session ID:{}",session.getId());
        }catch (Exception e){
            log.error("Exception Occurred in afterConnectionClosed:{}",e.getMessage());
        }
    }
}


