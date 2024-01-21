package sync.slamtalk.chat.service;

import sync.slamtalk.chat.dto.Request.ChatCreateDTO;
import sync.slamtalk.chat.dto.Request.ChatMessageDTO;
import sync.slamtalk.chat.dto.Response.ChatRoomDTO;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.UserChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatService {


    // 채팅방 생성하기
    long createChatRoom(ChatCreateDTO chatCreateDTO);

    // 채팅방에 유저 넣어주기
    void addMembers(Long ChatRoomId);

    // 메세지 저장하기
    void saveMessage(ChatMessageDTO chatMessageDTO);


    // 정상적으로 존재하는 채팅방인지 확인
    Optional<ChatRoom> isExistChatRoom(Long ChatRoomId);


    // 사용자 채팅방에 있는 채팅방인지 확인(구독여부 확인하는)
    Optional<UserChatRoom> isExistUserChatRoom(Long ChatRoomId);


    // 특정 방에서 주고받은 모든 메세지 가져오기
    List<ChatMessageDTO> getChatMessage(Long chatRoomId);

    // 채팅리스트 가져오기
    List<ChatRoomDTO> getChatLIst(Long userId);
















}
