package sync.slamtalk.chat.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import sync.slamtalk.chat.dto.request.ChatMessageDTO;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.email.EmailErrorResponseCode;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserRepository userRepository;

    // 메세지 저장
    public void saveMessage(ChatMessageDTO messageDTO, long timeoutInSeconds) {
        String messageKey = "chat_room:" + messageDTO.getRoomId() + ":message:" + messageDTO.getMessageId();//System.nanoTime(); //고유한 메세지 아이디 생성
        Map<String, String> messageInfo = new HashMap<>();

        messageInfo.put("messageId", messageDTO.getMessageId());
        messageInfo.put("senderId", messageDTO.getSenderId().toString());
        messageInfo.put("roomId", messageDTO.getRoomId());
        messageInfo.put("senderNickName", messageDTO.getSenderNickname());
        messageInfo.put("messageContent", messageDTO.getContent());
        messageInfo.put("sendTime", messageDTO.getTimestamp());

        // 메시지 메타데이터 저장
        redisTemplate.opsForHash().putAll(messageKey, messageInfo);

        // 메시지 메타데이터에 대한 만료 시간 설정
        redisTemplate.expire(messageKey, timeoutInSeconds, TimeUnit.SECONDS);

        // 메시지 ID 를 정렬된 세트에 추가(메세지 ID 를 점수로 사용)
        String roomId = messageDTO.getRoomId();
        long longRoomId = Long.parseLong(roomId);

        String chatRoomMessageKey = String.format("chat_room%d:messages", longRoomId);
        redisTemplate.opsForZSet().add(chatRoomMessageKey, messageKey, longRoomId);

        // 정렬된 세트에 대한 만료 시간 설정도 고려해야 할 수 있음
        redisTemplate.expire(chatRoomMessageKey, timeoutInSeconds, TimeUnit.SECONDS);

        log.debug("===redis 저장 된 아이디 :{}", messageDTO.getMessageId());
        log.debug("====redis 저장 완료====");
    }


    // 메세지 가져오기
    public List<ChatMessageDTO> getMessages(Long roomId, Long lastMessageId) {


        List<ChatMessageDTO> chatList = new ArrayList<>();

        // 검색 key 생성
        // 특정 채팅방 메세지 모두 가져오기
        String messageKey = "chat_room:" + roomId + "*";

        // 디버그용
        //log.debug("=========> messageKey : {}",messageKey);
        Set<String> keys = stringRedisTemplate.keys(messageKey);


        Set<String> sortedKeys = keys.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toCollection(LinkedHashSet::new)); // 순서를 유지하는 Set으로 수집

        for (String k : sortedKeys) {
            log.debug("== all key:{}", k);
        }


        log.debug("== lastMessageId : {}", lastMessageId);

        List<String> keyCollect = sortedKeys.stream()
                .map(key -> key.split(":"))
                // 분할된 배열에서 메시지 ID가 숫자 형태인지, 그리고 lastMessageId보다 작은지 확인
                .filter(parts -> parts.length == 4 && parts[3].matches("\\d+") && Long.parseLong(parts[3]) <= lastMessageId)
                // 원래 키 형태로 복원
                .map(parts -> String.join(":", parts))
                // 메시지 ID에 따라 내림차순 정렬
                // 상위 30개만 선택
                .limit(30)
                .collect(Collectors.toList());

        for (String key : keyCollect) {
            log.debug("== key : {}", key);
            Map<Object, Object> entry = stringRedisTemplate.opsForHash().entries(key);

            if (entry.isEmpty()) {
                log.debug("==redisService key값들 아무것도 가져오지 못함");
            }

            Object senderId = entry.get("senderId");
            long userId = Long.parseLong(senderId.toString());
            Optional<User> optionalUser = userRepository.findById(userId);
            String userImg = null;
            if (optionalUser.isPresent()) {
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
            log.debug("====완성:{}", chatMessageDTO.getContent());
            chatList.add(chatMessageDTO);
        }
        return chatList;
    }



    /**
     * 이메일 인증 코드를 저장하는 메소드입니다.
     * 입력받은 이메일 주소와 인증 코드를 이용하여, Redis 등의 데이터베이스에 인증 코드를 저장합니다.
     * 저장된 인증 코드는 24시간 동안 유효합니다.
     *
     * @param email 이메일 주소. 인증 코드를 받을 대상의 이메일 주소입니다.
     * @param code  인증 코드. 이메일을 통해 사용자에게 전송된 인증 코드입니다.
     *              <p>
     *              주의: 이 메소드는 Redis 서버와의 연결 오류가 발생할 경우,
     *              EmailErrorResponseCode.DATABASE_ERROR 예외를 발생시킵니다.
     */
    public void saveEmailVerificationCode(
            String email,
            String code
    ) {

        String key = generateEmailVerificationKey(email);
        setValueExpire(key, code, 60 * 60 * 24L); // {key,value} 24시간 동안 저장.
    }

    /**
     * 이메일 인증이 완료되어 로그인 가능한 상태를 저장하는 메서드입니다.
     * 이 메서드는 이메일 주소를 기준으로 한 키를 생성하고, 해당 키와 "OK" 값을 데이터베이스에 저장하여,
     * 이메일 인증이 성공적으로 완료되었음을 표시합니다. 저장된 데이터는 1시간 동안 유효합니다.
     * 만약 데이터베이스 연결에 문제가 발생할 경우, EmailErrorResponseCode.DATABASE_ERROR 예외를 발생시킵니다.
     *
     * @param email 이메일 인증이 완료된 사용자의 이메일 주소입니다. 이 주소를 기반으로 데이터베이스에 저장될 키가 생성됩니다.
     *              <p>
     *              Note: Redis 서버와의 연결 오류가 발생하면, 로깅 후 EmailErrorResponseCode.DATABASE_ERROR 예외가 발생합니다.
     */
    public void saveVerificationCompletionEmail(String email) {

        String key = generateEmailVerificationCompletedKey(email);
        setValueExpire(key, "OK", 60 * 60 * 1L); // {key,value} 1시간 동안 저장.
    }

    /**
     * 이 메소드의 목적은 Redis에서 이메일 인증 코드에 해당하는 값을 가져오는 것입니다.
     *
     * @param email 이메일 주소. 인증 코드와 함께 사용되어 Redis에서 해당 값을 검색하는 데 사용되는 키를 생성하는 데 사용됩니다.
     * @param code 이메일과 함께 사용되어 Redis에서 값을 검색하는 데 사용되는 인증 코드입니다.
     * @return String 타입으로, Redis에서 검색된 이메일 인증 코드의 값입니다. 만약 해당되는 값이 없다면 null이 반환될 수 있습니다.
     */
    public String getEmailVerificationCodeValue(
            String email,
            String code
    ) {
        String key = generateEmailVerificationKey(email);
        return getValue(key);
    }

    /**
     * 이 메서드의 목적은 Redis에서 이메일 인증이 완료된 값을 가져오는 것입니다.
     * 사용자가 이메일 인증 절차를 완료하였을 때, 해당 인증의 상태를 확인하기 위해 Redis에서 값을 조회합니다.
     *
     * @param email 이메일 주소입니다. 이메일 인증이 완료된 값을 조회하기 위한 키를 생성하는 데 사용됩니다.
     * @return String 타입으로, Redis에서 조회한 이메일 인증 완료 값입니다. 해당하는 값이 없을 경우 null이 반환될 수 있습니다.
     */
    public String getEmailVerificationCompletedValue(String email){
        String key = generateEmailVerificationCompletedKey(email);
        return getValue(key);
    }

    /**
     * 이 메서드의 목적은 Redis에서 특정 이메일에 대한 이메일 인증 코드 값을 삭제하는 것입니다.
     * 이메일 인증 절차가 완료되었거나, 더 이상 해당 인증 코드가 필요 없을 때 사용되어 Redis 내의 데이터를 관리할 수 있게 합니다.
     * 이를 통해 불필요한 데이터를 정리하고, Redis의 저장 공간을 효율적으로 사용할 수 있습니다.
     *
     * @param email 삭제하고자 하는 이메일 인증 코드와 관련된 이메일 주소입니다. 이 이메일 주소를 기반으로 Redis에서 삭제할 키를 생성합니다.
     */
    public void deleteEmailVerificationCodeValue(String email){
        String key = generateEmailVerificationKey(email);
        deleteValue(key);
    }

    /**
     * 이 메서드의 목적은 Redis에서 특정 이메일에 대한 이메일 인증 완료 값을 삭제하는 것입니다.
     * 사용자가 이메일 인증 절차를 성공적으로 마친 후, 해당 인증 관련 정보가 더 이상 필요하지 않을 경우 이 메서드를 통해 데이터를 삭제합니다.
     * 이를 통해 Redis 내에 불필요한 데이터를 정리하고, 저장 공간을 효율적으로 활용할 수 있습니다.
     *
     * @param email 삭제하고자 하는 이메일 인증 완료 값과 관련된 이메일 주소입니다. 이 이메일 주소를 기반으로 Redis에서 삭제할 키를 조회합니다.
     */
    public void deleteEmailVerificationCompletedValue(String email){
        String key = generateEmailVerificationCompletedKey(email);
        deleteValue(key);
    }

    /**
     * 이 메서드의 주목적은 Redis에서 주어진 키에 해당하는 값을 조회하는 것입니다.
     * Redis에 저장된 데이터 중 특정 키를 이용하여 그에 맞는 값을 불러오는 기능을 수행합니다.
     * 이 과정에서 Redis의 ValueOperations를 사용하여 키-값 쌍의 데이터를 다룹니다.
     *
     * @param key 조회하고자 하는 값의 키입니다. 이 키는 Redis 내에서 해당 값에 접근하기 위해 사용됩니다.
     * @return String 타입으로, Redis에서 키에 해당하는 값입니다. 만약 해당 키로 저장된 값이 없다면 null이 반환될 수 있습니다.
     */
    private String getValue(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /**
     * 이 메서드의 목적은 주어진 키와 값을 Redis에 저장하고, 설정된 시간 동안만 유효하게 만드는 것입니다.
     * 특정 키에 대한 값을 Redis에 저장할 때, 이 값이 일정 시간 후에 자동으로 만료되도록 설정합니다.
     * 만료 시간이 지나면, Redis에서 해당 키-값 쌍은 자동으로 삭제됩니다.
     * 이 기능은 일시적인 데이터를 저장할 때 유용하며, 데이터의 유효기간을 관리하는 데 사용됩니다.
     * 만약 Redis 서버와의 통신 중 오류가 발생하면, 예외를 발생시켜 오류를 알립니다.
     *
     * @param key 저장할 데이터의 키입니다. 이 키를 사용하여 나중에 값을 검색할 수 있습니다.
     * @param value 저장할 데이터의 값입니다. 이 값은 지정된 키와 함께 Redis에 저장됩니다.
     * @param duration 값이 Redis에 저장될 시간(초 단위)입니다. 이 시간이 지나면, 값은 자동으로 Redis에서 삭제됩니다.
     * @throws BaseException Redis 서버와의 통신 중 오류가 발생하면, 이 메서드는 EmailErrorResponseCode.DATABASE_ERROR을 포함하는 BaseException을 발생시킵니다.
     */
    private void setValueExpire(
            String key,
            String value,
            long duration
    ) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            Duration expireDuration = Duration.ofSeconds(duration);
            valueOperations.set(key, value, expireDuration);
        } catch (Exception e) {
            log.error("redis 서버에서 오류 발생 : = {}", e.toString());
            throw new BaseException(EmailErrorResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 이 메서드는 Redis에서 특정 키에 해당하는 값을 삭제하는 목적을 가집니다.
     * Redis에 저장된 데이터 중에서 더 이상 필요하지 않거나 유효하지 않은 데이터를 제거할 때 사용됩니다.
     * 이를 통해 Redis 내의 데이터 관리가 용이해지며, 불필요한 데이터로 인한 자원 낭비를 방지할 수 있습니다.
     *
     * @param key 삭제할 데이터의 키입니다. 이 키에 해당하는 값이 Redis에서 삭제됩니다.
     */
    private void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 이메일 인증을 위한 레디스 전용 키를 생성합니다. 생성된 키는 'authentication:email:{이메일주소}' 형식을 가집니다.
     *
     * @param email 사용자의 이메일 주소
     * @return 생성된 이메일 인증 키를 문자열로 반환합니다.
     */
    private static String generateEmailVerificationKey(String email) {
        // key 생성
        StringJoiner sj = new StringJoiner(":");
        sj.add("authentication");
        sj.add("email");
        sj.add(email);
        return sj.toString();
    }


    /**
     * 이 메서드는 이메일 인증이 완료되었을 때 사용할 키를 생성합니다.
     * 생성된 키는 주로 데이터베이스에서 해당 이메일 인증 정보를 식별하기 위해 사용됩니다.
     * 이메일 주소를 기반으로 키를 구성하며, ":"를 사용하여 "email"이라는 문자열과 이메일 주소를 결합합니다.
     * 예를 들어, 이메일 주소가 "example@example.com"인 경우, 생성된 키는 "email:example@example.com"이 됩니다.
     *
     * @param email 이메일 주소. 인증이 완료된 사용자의 이메일 주소입니다.
     * @return 생성된 키 문자열. 데이터베이스에서 이메일 인증 정보를 식별하기 위해 사용됩니다.
     */
    private static String generateEmailVerificationCompletedKey(String email) {
        // key 생성
        StringJoiner sj = new StringJoiner(":");
        sj.add("completion");
        sj.add("email");
        sj.add(email);
        return sj.toString();
    }
}
