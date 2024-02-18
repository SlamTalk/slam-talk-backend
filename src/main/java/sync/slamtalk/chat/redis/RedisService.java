package sync.slamtalk.chat.redis;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.stereotype.Service;
import sync.slamtalk.chat.dto.Request.ChatMessageDTO;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RedisService {
    @Autowired
    private final RedisTemplate<String,String> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserRepository userRepository;

    // 메세지 저장
    public void saveMessage(ChatMessageDTO messageDTO, long timeoutInSeconds){
        String messageKey = "chat_room:" + messageDTO.getRoomId() + ":message:" + messageDTO.getMessageId();//System.nanoTime(); //고유한 메세지 아이디 생성
        Map<String,String> messageInfo = new HashMap<>();

        messageInfo.put("messageId",messageDTO.getMessageId());
        messageInfo.put("senderId",messageDTO.getSenderId().toString());
        messageInfo.put("roomId",messageDTO.getRoomId());
        messageInfo.put("senderNickName",messageDTO.getSenderNickname());
        messageInfo.put("messageContent",messageDTO.getContent());
        messageInfo.put("sendTime",messageDTO.getTimestamp());

        // 메시지 메타데이터 저장
        redisTemplate.opsForHash().putAll(messageKey,messageInfo);

        // 메시지 메타데이터에 대한 만료 시간 설정
        redisTemplate.expire(messageKey, timeoutInSeconds, TimeUnit.SECONDS);

        // 메시지 ID 를 정렬된 세트에 추가(메세지 ID 를 점수로 사용)
        String roomId = messageDTO.getRoomId();
        long longRoomId = Long.parseLong(roomId);

        String chatRoomMessageKey = String.format("chat_room%d:messages",longRoomId);
        redisTemplate.opsForZSet().add(chatRoomMessageKey,messageKey,longRoomId);

        // 정렬된 세트에 대한 만료 시간 설정도 고려해야 할 수 있음
        redisTemplate.expire(chatRoomMessageKey, timeoutInSeconds, TimeUnit.SECONDS);

        log.debug("====redis 저장 완료====");
    }


    // 메세지 가져오기
    public List<ChatMessageDTO> getMessages(Long roomId,Long readIndex) {


        List<ChatMessageDTO> chatList = new ArrayList<>();

        // 검색 key 생성
        // 특정 채팅방 메세지 모두 가져오기
        String messageKey = "chat_room:" + roomId + "*";
        // 디버그용
        //log.debug("=========> messageKey : {}",messageKey);
        Set<String> keys = stringRedisTemplate.keys(messageKey);

        List<String> keyCollect = keys.stream()
                .map(key -> key.split(":"))
                // 분할된 배열에서 메시지 ID가 숫자 형태인지, 그리고 readIndex보다 작은지 확인
                .filter(parts -> parts.length == 4 && parts[3].matches("\\d+") && Integer.parseInt(parts[3]) <= readIndex)
                // 원래 키 형태로 복원
                .map(parts -> String.join(":", parts))
                // 메시지 ID에 따라 내림차순 정렬
                .sorted((key1, key2) -> Integer.compare(Integer.parseInt(key2.split(":")[3]), Integer.parseInt(key1.split(":")[3])))
                // 상위 20개만 선택
                .limit(20)
                .collect(Collectors.toList());

        for(String key : keyCollect){
            Map<Object, Object> entry = stringRedisTemplate.opsForHash().entries(key);

            if(entry.isEmpty()){
                log.debug("==redisService key값들 아무것도 가져오지 못함");
            }

            Object senderId = entry.get("senderId");
            long userId = Long.parseLong(senderId.toString());
            Optional<User> optionalUser = userRepository.findById(userId);
            String userImg = null;
            if(optionalUser.isPresent()){
                userImg = optionalUser.get().getImageUrl();
            }

            ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                    .messageId(entry.get("messageId").toString())
                    .roomId(entry.get("roomId").toString())
                    .senderId(userId)
                    .senderNickname(entry.get("senderNickName").toString())
                    .imgUrl(userImg)
                    .content(entry.get("messageContent").toString())
                    .timestamp(entry.get("sendTime").toString())
                    .build();
            //log.debug("====완성:{}",chatMessageDTO.getContent());
            chatList.add(chatMessageDTO);
        }
        return chatList;
    }




    /* 이메일 인증관련 메서드*/
    public String getData(String key) {
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setData(String key, String value) {
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
