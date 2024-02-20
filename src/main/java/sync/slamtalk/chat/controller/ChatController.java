package sync.slamtalk.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.chat.dto.Request.ChatCreateDTO;
import sync.slamtalk.chat.dto.Request.ChatMessageDTO;
import sync.slamtalk.chat.dto.Response.ChatRoomDTO;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.service.ChatServiceImpl;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.common.ErrorResponseCode;
import sync.slamtalk.security.jwt.JwtTokenProvider;
import sync.slamtalk.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatServiceImpl chatService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // 채팅방 생성
    @PostMapping("/api/chat/create")
    @Operation(
            summary = "채팅방 생성",
            description = "이 기능은 채팅방을 생성하는 기능입니다.",
            tags = {"채팅"}
    )
    public ApiResponse create(@RequestBody ChatCreateDTO dto){
        long chatRoom = chatService.createChatRoom(dto);
        return ApiResponse.ok(chatRoom,"채팅방이 생성되었습니다."); // TODO ApiResponse 수정
    }


    // 채팅 리스트
    @GetMapping("/api/chat/list")
    @Operation(
            summary = "채팅리스트 조회",
            description = "이 기능은 유저의 채팅리스트를 조회하는 기능입니다.",
            tags = {"채팅"}
    )
    public ApiResponse list(@AuthenticationPrincipal Long userId){
        List<ChatRoomDTO> chatLIst = chatService.getChatLIst(userId);
        return ApiResponse.ok(chatLIst);
    }

    // 채팅방 접속 시 채팅 내역
    @PostMapping("/api/chat/participation")
    @Operation(
            summary = "새로운 채팅 내역 조회",
            description = "이 기능은 채팅방에 재입장 시 과거 마지막으로 읽은 메세지 이후에 발생한 메세지를 보내주는 기능입니다.",
            tags = {"채팅"}
    )
    public ApiResponse participation(@Param("roomId")Long roomId,@AuthenticationPrincipal Long userId){

        // userChatRoom 에 있는 지 검사
        Optional<UserChatRoom> existUserChatRoom = chatService.isExistUserChatRoom(userId,roomId);
        if(!existUserChatRoom.isPresent()){
            throw new BaseException(ErrorResponseCode.CHAT_FAIL);
        }

        UserChatRoom userChatRoom = existUserChatRoom.get();
        // 사용자가 마지막으로 읽은 메세지 아이디
        Long readIndex = userChatRoom.getReadIndex();


        // 채팅방에서 주고받았던 메세지 가져오기
        List<ChatMessageDTO> chatMessage = chatService.getChatMessages(roomId,readIndex);

        return ApiResponse.ok(chatMessage);
    }


    // 과거 내역 추가 요청
    @PostMapping("/api/chat/history")
    @Operation(
            summary = "과거 내역 조회",
            description = "이 기능은 과거 마지막으로 읽은 메세지 전의 메세지를 보내주는 기능입니다.",
            tags = {"채팅"}
    )
    public ApiResponse history(@Param("roomId")Long roomId, @AuthenticationPrincipal Long userId,@Param("count") int count){
        List<ChatMessageDTO> previousChatMessages = chatService.getPreviousChatMessages(userId, roomId,count);
        if (previousChatMessages == null) {
            log.debug("과거 메세지가 없습니다");
            previousChatMessages = new ArrayList<>(); // 빈 리스트로 초기화
        }
        return ApiResponse.ok(previousChatMessages);

    }



}
