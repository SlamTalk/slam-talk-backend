package sync.slamtalk.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Slf4j
@EnableWebSocketMessageBroker
@Configuration
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatInboundInterceptor chatInboundInterceptor;

    // webSocket 접속 경로 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/slamtalk")
                .setAllowedOrigins("*");

                //.withSockJS(); // 사용시 /websocket 붙여서 테스트
    }


    // 메세지 브로커 기반 통신 설정 -> STOMP Messaging protocol
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
    }


    // client 요청 검증 수행
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // TODO
        // registration.interceptors(chatInboundInterceptor);
    }
}
