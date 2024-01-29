package sync.slamtalk.chat.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import java.util.List;

@RestController
@RequestMapping("/redis")
@AllArgsConstructor
@Slf4j
public class RedisController {

    @Autowired
    private final RedisService redisService;
    private ObjectMapper objectMapper;

    /*
     * dto 생성
     * String key = objectMapper.writeValueAsString('여기에 dto'); --> String type 으로 변환
     * redisService.saveMessage(key,convertdtoToString,convertostringtypetimestame);
     * */

    @GetMapping("/save")
    public String save(){
        // test 코드여서 TODO 페이징과 연결해야함
//        ChatMessageDTO dto1 = ChatMessageDTO.builder()
//                .roomId("1")
//                .content("hello!!!!")
//                .senderNickname("yeji")
//                .timestamp(LocalDateTime.now())
//                .build();
//        ChatMessageDTO dto2 = ChatMessageDTO.builder()
//                .roomId("1")
//                .content("common!!!!")
//                .senderNickname("yeji")
//                .timestamp(LocalDateTime.now())
//                .build();
//        ChatMessageDTO dto3 = ChatMessageDTO.builder()
//                .roomId("1")
//                .content("NicetoMeetyou!!!!!")
//                .senderNickname("jiyooon")
//                .timestamp(LocalDateTime.now())
//                .build();
//
//
//        try{
//            String s1 = objectMapper.writeValueAsString(dto1);
//            String s2 = objectMapper.writeValueAsString(dto2);
//            String s3 = objectMapper.writeValueAsString(dto3);
//
//            redisService.saveMessage(dto1.getRoomId(), s1,dto1.getTimestamp().toString());
//            redisService.saveMessage(dto2.getRoomId(), s2,dto2.getTimestamp().toString());
//            redisService.saveMessage(dto3.getRoomId(), s3,dto3.getTimestamp().toString());
//        }catch (Exception e){
//            throw new BaseException(ErrorResponseCode.CHAT_FAIL);
//        }
//        return "ok";
        return null;

    }

    @GetMapping("/get/{chatRoomId}")
    public ApiResponse<List<String>> getMessages(@PathVariable String chatRoomId, @RequestParam(defaultValue = "0") int count) throws JsonProcessingException {
        // test 코드여서 TODO 페이징과 연결해야함
//        List<String> messages = redisService.getMessages(chatRoomId, 0, 2);
//        int idx = 0;
//        for(String s : messages){
//            log.debug("messages: {}",messages.get(idx++));
//        }
//        return ApiResponse.ok(messages);
        return  null;
    }




}
