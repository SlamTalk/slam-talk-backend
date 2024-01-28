package sync.slamtalk.chat.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import sync.slamtalk.chat.dto.Request.ChatMessageDTO;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.Messages;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.service.ChatServiceImpl;
import sync.slamtalk.security.jwt.JwtTokenProvider;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
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


    // 메세지가 전송되기 전에 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);


        // CONNECT
        /*
        1. 토큰 검증
         */
        if(StompCommand.CONNECT.equals(headerAccessor.getCommand())){
            log.debug("===CONNECT===");

            Long authorization = tokenProvider.stompExtractUserIdFromToken(headerAccessor.getFirstNativeHeader("authorization").toString());
            Optional<User> userOptional = userRepository.findById(authorization);
            if(userOptional.isEmpty()){
                throw new RuntimeException("JWT");
            }
            log.debug("성공");
        }




        // SUBSCRIBE
        /*
        1. 구독이 가능한(ChatRoom 에 존재하는) 채팅방인지 검증
        2. UserChatRoom 에 추가 ==> Token 로직 완성 되면 🌟서비스부터 수정해야됨🌟
         */
        if(StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())){
            log.debug("===SUBSCRIBE===");

            log.debug("==SUBSCRIBE STEP1==");
            String d = headerAccessor.getDestination();
            log.debug("destination:{}",d);
            // 채팅방의 존재 여부 검증
            isExistChatRoom(headerAccessor);
            log.debug("==SUBSCRIBE STEP2==");


            // RoomId 만 추출
            String destination = headerAccessor.getDestination();
            Long roomId = extractRoomId(destination);

            //'사용자채팅방' 테이블에 추가하기
            addUserChatRoom(headerAccessor);
            log.debug("==SUBSCRIBE STEP3==");
        }





        // SEND
        /*
        1. 메세지를 보낼 수 있는(ChatRoom 에 존재하는) 채팅방인지 검증
        2. 사용자가 구독한 채팅방(UserChatRoom 에 존재하는) 채팅방인지 검증 ==> Token 로직 완성되면 작성(아직에러남)
        3. 일반메세지 / 뒤로가기 / 나가기 메세지 인지 구분
        -> 일단 메세지 바디 확인 해서 일반/뒤로가기/나가기 인지 구분
        case1 : 일반메세지
            1. 일반 메세지는 메세지 파싱해서 전달
            2. 메세지 바디에 content 부분 메세지 저장

        case2 : 뒤로 가기
            1. 해당 채팅룸 아이디로 메세지 테이블을 조회해서 가장 최근에 저장된 메세지 (최근에 발생한 메세지) 아이디 추출
            2. UserChatRoom readIndex 에 메세지 아이디 저장

        case3 : 아예 방 나가기
            1. UserChatRoom 에서 해당 채팅방을 soft delete 처리
            2. 'userName' 님이 채팅방을 나가셨습니다 메세지 보내기
         */
        if(StompCommand.SEND.equals(headerAccessor.getCommand())){
            log.debug("===SEND===");

            // 채팅방의 존재 여부 검증
            isExistChatRoom(headerAccessor);
            log.debug("==SEND STEP1==");

            // client 가 destination 에 메세지를 보낼 수 있는지 검증(사용자 채팅방에 있는 채팅방인지)
            // Token 붙이기 전이면 이거 비활성화해주고 실행해야 제대로 테스트됨
            isExistUserChatRoom(headerAccessor);
            log.debug("==SEND STEP2==");


            // destination 가져오기
            String destination = headerAccessor.getDestination();

            // roomId 가져오기
            Long roomId = extractRoomId(destination);

            // userId 가져오기
            Long userId = extractUserId(headerAccessor);


            // 이부분 유저아이디 이상한거 가지고 오는데???
            // 뒤로가기
            // 채팅방의 마지막 메세지를 저장
            if(destination.contains("back")){
                log.debug("==SEND STEP3==");
                Messages lastMessageFromChatRoom = chatService.getLastMessageFromChatRoom(roomId);
//                log.debug("lastmessage:{}",lastMessageFromChatRoom.getContent().toString());
//                log.debug("userId:{}",userId);
//                log.debug("roomId:{}",roomId);
                chatService.saveReadIndex(userId,roomId,lastMessageFromChatRoom.getId());
                log.debug("==SEND STEP4==");
            }

            // 나가기
            // softDelete
            if(destination.contains("exit")){
                log.debug("exit");
                //TODO

            }

            // 일반 메세지
            if(destination.contains("message")) {
                // 본문 바디 가져오기
                String messageContent = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);

                // 메시지의 페이로드(본문) 추출
                String content = extractRoomContent(messageContent);
                log.debug("extract message content:{}",content);

                // 메시지 보낸 유저의 닉네임 추출
                String nickname = extractNickname(messageContent);
                log.debug("extract message nickname:{}",nickname);

                if (content != null) {
                    ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                            .roomId(roomId.toString())
                            .content(content)
                            .senderNickname(nickname)
                            .timestamp(LocalDateTime.now())
                            .build();
                    chatService.saveMessage(chatMessageDTO);
                    log.debug("==SEND STEP5==");
                }
            }
            // 처음 입장 메세지
            if(destination.contains("enter")){
                Optional<UserChatRoom> existUserChatRoom = chatService.isExistUserChatRoom(userId, roomId);
                if(existUserChatRoom.isPresent()){

                    log.debug("enter");
                    // 본문 바디 가져오기
                    String messageContent = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);

                    // 메시지 보낸 유저의 닉네임 추출
                    String nickname = extractNickname(messageContent);

                    UserChatRoom userChatRoom = existUserChatRoom.get();
                    // 처음 입장
                }else{
                    throw new RuntimeException("JWT");
                }
            }
        }




        if(StompCommand.DISCONNECT.equals(headerAccessor.getCommand())){
            log.debug("===DISCONNECT===");
        }
        return message;
    }















    // 토큰에서 아이디 추출
    private Long extractUserId(StompHeaderAccessor accessor){
        List<String> authorization = accessor.getNativeHeader("authorization");
        String Token = authorization.get(0).toString();
        log.debug("Token:{}",Token);
        Long l = tokenProvider.stompExtractUserIdFromToken(Token);
        log.debug("tokenProvider:{}",l);
        Optional<User> byId = userRepository.findById(l);
        log.debug("usernickname:{}",byId.get().getNickname());

        return l;
    }


    // 채팅방 존재하는지 검증하는 실질적인 메서드
    private void isExistChatRoom(StompHeaderAccessor accessor){
        String destination = accessor.getDestination();
        Long RoomId = extractRoomId(destination);
        Optional<ChatRoom> existChatRoom = chatService.isExistChatRoom(RoomId);
        // ChatRoom 이 존재하지 않는다면
        if(!existChatRoom.isPresent()){
            throw new RuntimeException("NFR");
        }
    }


    // 사용자채팅방에 특정 채팅방이 존재하는지 검증하는 실질적인 메서드
    private void isExistUserChatRoom(StompHeaderAccessor accessor){

        Long userId = extractUserId(accessor);
        log.debug("userId:{}",userId);

        String destination = accessor.getDestination();
        Long RoomId = extractRoomId(destination);
        log.debug("RoomId:{}",RoomId);

        Optional<UserChatRoom> existUserChatRoom = chatService.isExistUserChatRoom(userId,RoomId);
        // UserChatRoom 이 존재하지 않는다면
        if(!existUserChatRoom.isPresent()){
            throw new RuntimeException("Auth");
        }
    }



    // Destination 에서 채팅방 아이디 추출
    private Long extractRoomId(String path) {
        String[] parts = path.split("/");
        if (parts.length > 0) {
            try {
                // 마지막 부분을 숫자로 변환
                return Long.parseLong(parts[parts.length - 1]);
            } catch (NumberFormatException e) {
                // 마지막 부분이 숫자가 아닌 경우
                throw new RuntimeException("Not A number");
            }
        }
        return null; // 숫자가 없거나 변환할 수 없는 경우
    }



    // 채팅 콘텐츠 추출
    private String extractRoomContent(String json){
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            if(rootNode.has("content")){
                return rootNode.get("content").toString();
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    // 채팅 닉네임 추출
    private String extractNickname(String json){
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            if(rootNode.has("senderNickname")){
                return rootNode.get("senderNickname").toString();
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }




    // 사용자 채팅방에 추가
    public void addUserChatRoom(StompHeaderAccessor accessor){
        Long userId = extractUserId(accessor);

        String destination = accessor.getDestination();
        Long roomId = extractRoomId(destination);

        chatService.setUserChatRoom(userId,roomId);
    }




}
