package sync.slamtalk.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.chat.dto.ChatErrorResponseCode;
import sync.slamtalk.chat.dto.Request.ChatCreateDTO;
import sync.slamtalk.chat.dto.Request.ChatMessageDTO;
import sync.slamtalk.chat.dto.Response.ChatRoomDTO;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.service.ChatServiceImpl;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.common.ErrorResponseCode;
import sync.slamtalk.user.entity.User;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatServiceImpl chatService;

    // 채팅방 생성
    @PostMapping("/api/chat/create")
    @Operation(
            summary = "채팅방 생성",
            description = "이 기능은 채팅방을 생성하는 기능입니다.",
            tags = {"유저","관리자"}
    )
    public ApiResponse create(@RequestBody ChatCreateDTO dto){
        long chatRoom = chatService.createChatRoom(dto);
        return ApiResponse.ok(); // TODO ApiResponse 수정
    }


    // 채팅 리스트
    // TODO @Param 수정 @AuthenticationPrincipal User user
    @GetMapping("/api/chat/list")
    @Operation(
            summary = "채팅리스트 조회",
            description = "이 기능은 유저의 채팅리스트를 조회하는 기능입니다.",
            tags = {"유저"}
    )
    public ApiResponse list(){
        // Test
        List<ChatRoomDTO> chatLIst = chatService.getChatLIst(1L);
        return ApiResponse.ok(chatLIst);
    }

    // 채팅 참여
    // TODO @Param 수정 @AuthenticationPrincipal User user
    // TODO 페이징정책 확정되면 다시 수정해야함
    @PostMapping("/api/chat/participation")
    @Operation(
            summary = "과거 내역 전달",
            description = "이 기능은 채팅방에 입장할 때 채팅방의 과거 내역을 받을 수 있는 기능입니다.",
            tags = {"유저"}
    )
    public ApiResponse participation(@Param("roomId")Long roomId){

        // 채팅방에 속해있는지 검사
        Optional<UserChatRoom> existUserChatRoom = chatService.isExistUserChatRoom(roomId);
        if(!existUserChatRoom.isPresent()){
            System.out.println("없는방");
            throw new BaseException(ErrorResponseCode.CHAT_FAIL);
            // TODO ErrorResponse 코드 추가하기 ErrorResponseCode.CHATROOM_NOT_FOUND
        }
        // 채팅방에서 주고받았던 메세지 가져오기
        List<ChatMessageDTO> chatMessage = chatService.getChatMessage(roomId);

        return ApiResponse.ok(chatMessage);
    }





}
