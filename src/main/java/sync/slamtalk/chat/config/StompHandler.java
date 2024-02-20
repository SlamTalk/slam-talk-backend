package sync.slamtalk.chat.config;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.service.ChatServiceImpl;
import sync.slamtalk.security.jwt.JwtTokenProvider;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class StompHandler {

    private final ChatServiceImpl chatService;
    private final JwtTokenProvider tokenProvider;



    // 토큰에서 아이디 추출
    public Long extractUserId(StompHeaderAccessor accessor){
        List<String> authorization = accessor.getNativeHeader("authorization");
        String Token = authorization.get(0).toString();
        Long userid = tokenProvider.stompExtractUserIdFromToken(Token);

        return userid;
    }


    // 채팅 유저 아이디 추출
    public String extractUserId(String json){
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            if(rootNode.has("senderId")){
                return rootNode.get("senderId").toString();
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }



    // 채팅방 존재하는지 검증하는 실질적인 메서드
    public void isExistChatRoom(StompHeaderAccessor accessor){
        String destination = accessor.getDestination();
        Long RoomId = extractRoomId(destination);
        Optional<ChatRoom> existChatRoom = chatService.isExistChatRoom(RoomId);
        // ChatRoom 이 존재하지 않는다면
        if(!existChatRoom.isPresent()){
            throw new RuntimeException("NFR");
        }
    }



    // 사용자채팅방에 특정 채팅방이 존재하는지 검증하는 실질적인 메서드
    public void isExistUserChatRoom(StompHeaderAccessor accessor){

        log.debug("=== 사용자 채팅방에 특정 채팅방이 존재하는지 검사 === ");
        Long userId = extractUserId(accessor);

        String destination = accessor.getDestination();
        Long RoomId = extractRoomId(destination);

        Optional<UserChatRoom> existUserChatRoom = chatService.isExistUserChatRoom(userId,RoomId);
        // UserChatRoom 이 존재하지 않는다면
        if(!existUserChatRoom.isPresent()){
            throw new RuntimeException("Auth");
        }
    }



    // Destination 에서 채팅방 아이디 추출
    public Long extractRoomId(String path) {
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
    public String extractRoomContent(String json){
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
    public String extractNickname(String json){
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



    // 방문 체크 추출
    public String extractVisited(String json){
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            if(rootNode.has("visited")){
                return rootNode.get("visited").toString();
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }



    // 사용자 채팅방에 추가
    public Optional<Long> addUserChatRoom(StompHeaderAccessor accessor){
        Long userId = extractUserId(accessor);

        String destination = accessor.getDestination();
        Long roomId = extractRoomId(destination);

        Optional<Long> userChatRoom = chatService.createUserChatRoom(userId, roomId);
        if(userChatRoom.isEmpty()){
            return Optional.empty();
        }
        return userChatRoom;
    }




}
