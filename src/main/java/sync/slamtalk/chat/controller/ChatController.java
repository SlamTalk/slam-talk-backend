package sync.slamtalk.chat.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.chat.dto.Request.ChatCreateDTO;
import sync.slamtalk.chat.service.ChatServiceImpl;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatServiceImpl chatService;



    // TODO : refactor

    // 채팅방 생성 test
    @PostMapping("/api/create")
    public String create(@RequestBody ChatCreateDTO dto){
        long chatRoom = chatService.createChatRoom(dto);
        return "저장완료" + chatRoom;

    }


}
