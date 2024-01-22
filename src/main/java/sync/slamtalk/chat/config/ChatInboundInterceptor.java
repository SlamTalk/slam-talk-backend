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
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.service.ChatServiceImpl;

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
    private final StompErrorHandler stompErrorHandler;
    private final ChatServiceImpl chatService;

    // 메세지가 전송되기 전에 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);


        // CONNECT
        /*
        1. 토큰 검증
         */
        if(StompCommand.CONNECT.equals(headerAccessor.getCommand())){
            // log.info("CONNECT 발생");
            // TODO Token 검증
        }


        // SUBSCRIBE
        /*
        1. 구독이 가능한(ChatRoom 에 존재하는) 채팅방인지 검증
        2. UserChatRoom 에 추가 ==> Token 로직 완성되면 작성(아직에러남)
         */
        if(StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())){
            // log.info("SUBSCRIBE 발생");

            // 채팅방의 존재 여부 검증
            isExistChatRoom(headerAccessor);

            // TODO: Token 로직 추가 되면 특정 경로와 유저 정보를 '사용자채팅방' 테이블에 추가하기
            // 아직 이 부분 작성을 아직 안해서 SEND 시 에러가 발생함
            // chatService.addMembers();
        }


        // SEND
        /*
        1. 메세지를 보낼 수 있는(ChatRoom 에 존재하는) 채팅방인지 검증
        2. 사용자가 구독한 채팅방(UserChatRoom 에 존재하는) 채팅방인지 검증 ==> Token 로직 완성되면 작성(아직에러남)
        3. 검증이 모두 통과되면 메세지저장(Message 저장)
         */
        if(StompCommand.SEND.equals(headerAccessor.getCommand())){
            // log.info(headerAccessor.getCommand().toString());

            // 채팅방의 존재 여부 검증
            isExistChatRoom(headerAccessor);

            // client 가 destination 에 메세지를 보낼 수 있는지 검증(사용자 채팅방에 있는 채팅방인지)
            // Token 붙이기 전이면 이거 비활성화해주고 실행해야 제대로 테스트됨
            //isExistUserChatRoom(headerAccessor);

            // TODO 메세지 저장

                // 메시지의 페이로드(본문) 추출
            String messageContent = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
            String content = extractRoomcontent(messageContent);


                // RoomId 만 추출
            String destination = headerAccessor.getDestination();
            Long roomId = extractLastNumber(destination);

                // content 가 null 이 아닐때만 메세지저장
            if(content != null){
                ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                        .roomId(roomId.toString())
                        .content(content)
                        .timestamp(LocalDateTime.now())
                        //.senderId() // token 로직 완성되면 추가하기
                        .build();

                chatService.saveMessage(chatMessageDTO);
            }
        }

        // TODO DISCONNECT
        /*
        일단 메세지 바디 확인해서 뒤로가기인지, 아예 방을 나가는 것인지 확인
        case1 : 뒤로 가기
            1. 해당 채팅룸 아이디로 메세지 테이블을 조회해서 가장 최근에 저장된 메세지 (최근에 발생한 메세지) 아이디 추출
            2. UserChatRoom readIndex 에 메세지 아이디 저장

        case2 : 아예 방 나가기
            1. UserChatRoom 에서 해당 채팅방을 soft delete 처리
            2. 'userName' 님이 채팅방을 나가셨습니다 메세지 보내기
         */
        if(StompCommand.DISCONNECT.equals(headerAccessor.getCommand())){

        }

        return message;
    }


    /*
    * TODO
    *
    *
    * */







    // 채팅방 존재하는지 검증하는 실질적인 메서드
    private void isExistChatRoom(StompHeaderAccessor accessor){
        String destination = accessor.getDestination();
        Long RoomId = extractLastNumber(destination);
        Optional<ChatRoom> existChatRoom = chatService.isExistChatRoom(RoomId);
        // ChatRoom 이 존재하지 않는다면
        if(!existChatRoom.isPresent()){
            throw new RuntimeException("NFR");
        }
    }


    // 사용자채팅방에 특정 채팅방이 존재하는지 검증하는 실질적인 메서드
    private void isExistUserChatRoom(StompHeaderAccessor accessor){
        String destination = accessor.getDestination();
        Long RoomId = extractLastNumber(destination);
        Optional<UserChatRoom> existUserChatRoom = chatService.isExistUserChatRoom(RoomId);
        // UserChatRoom 이 존재하지 않는다면
        if(!existUserChatRoom.isPresent()){
            throw  new RuntimeException("Auth");
        }
    }



    // Destination 에서 채팅방 아이디 추출
    private Long extractLastNumber(String path) {
        String[] parts = path.split("/");
        if (parts.length > 0) {
            try {
                // 마지막 부분을 숫자로 변환
                return Long.parseLong(parts[parts.length - 1]);
            } catch (NumberFormatException e) {
                // 마지막 부분이 숫자가 아닌 경우
                throw new RuntimeException(e.getMessage());
            }
        }
        return null; // 숫자가 없거나 변환할 수 없는 경우
    }



    // 채팅 콘텐츠 추출
    private String extractRoomcontent(String json){
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



}
