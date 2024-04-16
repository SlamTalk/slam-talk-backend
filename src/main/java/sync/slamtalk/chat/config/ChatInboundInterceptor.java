package sync.slamtalk.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import sync.slamtalk.chat.dto.request.ChatMessageDTO;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.Messages;
import sync.slamtalk.chat.entity.RoomType;
import sync.slamtalk.chat.repository.ChatRoomRepository;
import sync.slamtalk.chat.service.ChatServiceImpl;
import sync.slamtalk.security.jwt.JwtTokenProvider;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatInboundInterceptor implements ChannelInterceptor {

    /**
     * 메세지 헤더에 존재하는 Authorization 으로 사용자 검증
     * 토큰 만료나 변조 시, 예외를 터트린다.
     */
    private final ChatServiceImpl chatService;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final StompHandler stompHandler;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);


        // CONNECT
        /**
         * 토큰 검증
         */
        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            log.debug("=== CONNECT 진입 ===");

            Long authorization = tokenProvider.stompExtractUserIdFromToken(headerAccessor.getFirstNativeHeader("authorization"));
            Optional<User> userOptional = userRepository.findById(authorization);
            if (userOptional.isEmpty()) {
                log.debug("인증실패");
                throw new RuntimeException("JWT");
            }
            log.debug("성공");
            log.debug("=== CONNECT 완료 ===");
        }


        // SUBSCRIBE
        /**
         * 1. 구독이 가능한(ChatRoom 에 존재하는) 채팅방인지 검증
         * (채팅방생성 시 participants 의 userchatroom 에 해당 채팅방 설정해주었음)
         */
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            log.debug("=== SUBSCRIBE 진입 ===");

            // 채팅방의 존재 여부 검증
            stompHandler.isExistChatRoom(headerAccessor);

            // RoomId 만 추출
            String destination = headerAccessor.getDestination();
            Long roomId = stompHandler.extractRoomId(destination);

            // userId 추출
            Long userId = stompHandler.extractUserId(headerAccessor);


            // userChatRoom 검사
            Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(roomId);
            if (chatRoomOptional.isPresent()) {
                ChatRoom chatRoom = chatRoomOptional.get();


                // basketball chat 이 아닌 경우 userchatRoom 에 이미 추가 되어 있어야 함
                if (chatRoom.getRoomType()!=RoomType.BASKETBALL) {
                    log.debug("이미 참여하고 있는 농구장");
                    stompHandler.isExistUserChatRoom(headerAccessor);
                }

                // basketball chat 인 경우 userChatRoom 에 추가
                // BasketBallChatRoom 은 구독했을 때 유저의 채팅리스트에 추가됨
                if (chatRoom.getRoomType().equals(RoomType.BASKETBALL)) {

                    // destination 가져오기
                    String dest = headerAccessor.getDestination();
                    Long basketRoomId = stompHandler.extractRoomId(dest);

                    // 이미 유저가 참여중인 농구장 채팅방인지 확인
                    Optional<Boolean> existAlreadyUserChatRoom = stompHandler.isExistAlreadyUserChatRoom(userId, basketRoomId);


                    // 존재하지 않는 경우에만 유저의 채팅 리스트에 추가
                    if (existAlreadyUserChatRoom.isEmpty()) {
                        log.debug("유저의 채팅 리스트에 추가");
                        stompHandler.addUserChatRoom(headerAccessor);
                    }

                    // 이미 존재하는 경우 디버그
                    if (existAlreadyUserChatRoom.isPresent()) {
                        log.debug("이미 참여 하고 있는 방 재 입장");
                    }
                }
            }

            log.debug("=== SUBSCRIBE 완료 ===");

        }


        // SEND
        /**
         * 1. 메세지를 보낼 수 있는(ChatRoom 에 존재하는) 채팅방인지 검사
         *
         * 2. 사용자가 구독한 채팅방(UserChatRoom 에 존재하는) 채팅방인지 검사
         * - 이때 basketball chat 은 채팅방 접속 시 userChatRoom 에 추가 되므로 검증 하지 않음
         * -  basketball chat 을 제외한 , direct/together/teamMatching chat 은 채팅방 생성 시점에
         *    참여자들의 userChatRoom 에 추가하므로 사용자가 구독한 채팅방인지 검사함
         *
         * 3. 일반메세지 / 뒤로가기 메세지 인지 구분
         * case1 : 일반메세지
         * 1. 일반 메세지는 메세지 파싱해서 전달
         * 2. 메세지 바디에 content 부분 메세지 저장
         *
         * case2 : 뒤로 가기
         * 1. 해당 채팅룸 아이디로 메세지 테이블을 조회해서 가장 최근에 저장된 메세지 (최근에 발생한 메세지) 아이디 추출
         * 2. UserChatRoom readIndex 에 메세지 아이디 저장
         */
        if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
            log.debug("=== SEND 진입 ===");

            // 채팅방의 존재 여부 검증
            stompHandler.isExistChatRoom(headerAccessor);
            log.debug("=== 채팅방 존재 여부 검사 ===");

            // client 가 destination 에 메세지를 보낼 수 있는지 검증(사용자 채팅방에 있는 채팅방인지)
            stompHandler.isExistChatRoom(headerAccessor);
            log.debug("=== 유저가 참여하고 있는 채팅방인지 검사 ===");


            // destination 가져오기
            String destination = headerAccessor.getDestination();

            // roomId 가져오기
            Long roomId = stompHandler.extractRoomId(destination);

            // userId 가져오기
            Long userId = stompHandler.extractUserId(headerAccessor);


            // 뒤로 가기
            // 채팅방의 마지막 메세지id 를 저장 == ReadIndex 업데이트
            if (destination.contains("back")) {
                log.debug("=== SEND_BACK 진입 ===");
                Messages lastMessageFromChatRoom = chatService.getLastMessageFromChatRoom(roomId);
                chatService.saveReadIndex(userId, roomId, lastMessageFromChatRoom.getId());
                log.debug("=== ReadIndex 저장 ===");
            }


            // 일반 메세지
            if (destination.contains("message")) {
                log.debug("=== SEND_MESSAGE 진입 ===");
                // 본문 바디 가져오기
                String messageContent = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);

                // 메시지의 페이로드(본문) 추출
                String content = stompHandler.extractRoomContent(messageContent);
                log.debug("=== extract message content:{}", content);

                // 메시지 보낸 유저의 닉네임 추출
                String nickname = stompHandler.extractNickname(messageContent);
                log.debug("=== extract message nickname:{}", nickname);

                // 메세지 보낸 유저의 아이디 추출
                String uid = stompHandler.extractUserId(messageContent);
                Long userid = Long.parseLong(uid);
                log.debug("=== extract userId:{}",uid);

                if (content != null) {
                    ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                            .roomId(roomId.toString())
                            .senderId(userid)
                            .content(content)
                            .senderNickname(nickname)
                            .timestamp(LocalDateTime.now().toString())
                            .build();
                    chatService.saveMessage(chatMessageDTO);
                    log.debug("=== MESSAGE 저장 완료 ===");
                }
            }
            log.debug("=== 메세지 발송 완료 ===");
        }


        /**
         * 연결종료, CustomWebSocketHandler 에 의해 리소스 해제
         */
        if (StompCommand.DISCONNECT.equals(headerAccessor.getCommand())) {
            log.debug("===DISCONNECT===");
        }

        return message;
    }
}
