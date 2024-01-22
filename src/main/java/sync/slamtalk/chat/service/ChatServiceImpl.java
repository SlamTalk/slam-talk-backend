package sync.slamtalk.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.chat.dto.ChatErrorResponseCode;
import sync.slamtalk.chat.dto.Request.ChatCreateDTO;
import sync.slamtalk.chat.dto.Request.ChatMessageDTO;
import sync.slamtalk.chat.dto.Response.ChatRoomDTO;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.Messages;
import sync.slamtalk.chat.entity.RoomType;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.repository.ChatRoomRepository;
import sync.slamtalk.chat.repository.MessagesRepository;
import sync.slamtalk.chat.repository.UserChatRoomRepository;
import sync.slamtalk.common.BaseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
    private final ChatRoomRepository chatRoomRepository;
    private final MessagesRepository messagesRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    // 채팅방 생성(농구장:미리해놓기, 그외 모두 SUBSCRIBE 시에)
    @Override
    public long createChatRoom(ChatCreateDTO chatCreateDTO) {
        // chatRoom create
        ChatRoom chatRoom = ChatRoom.builder()
                .roomType(typeOfRoom(chatCreateDTO))
                .name(nameOfRoom(chatCreateDTO))
                .build();
        ChatRoom save = chatRoomRepository.save(chatRoom);
        return save.getId();
    }


    // 채팅방 입장(STOMP: SUBSCRIBE) ❗️최초 입장일때만 ==> Token 완료 시 테스트해야함
    @Override
    public void addMembers(Long chatRoomId) {
        // add member to chatRoom
        Optional<ChatRoom> chatroom = chatRoomRepository.findById(chatRoomId);
        if(chatroom.isPresent()){ // chatRoom 이 존재하면
            ChatRoom chatRoom = chatroom.get();
            // userChatRoom setting
            UserChatRoom userChatRoom = UserChatRoom.builder()
                    //.user() // token
                    .chat(chatRoom)
                    .readIndex(0L)
                    .build();
            // chatRoom add userChatRoom
            chatRoom.addUserChatRoom(userChatRoom);
            // userChatRoom save
            userChatRoomRepository.save(userChatRoom);
        }else {
            // Exception 처리
            log.info("Exception");
        }
    }


    // 채팅방에 메세지 저장(STOMP: SEND)
    @Override
    public void saveMessage(ChatMessageDTO chatMessageDTO) {
        long chatRoomId = Long.parseLong(chatMessageDTO.getRoomId());
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if(chatRoom.isPresent()){ // chatRoom 이 존재하면
            ChatRoom Room = chatRoom.get();
            // Message create
            Messages messages = Messages.builder()
                    .chatRoom(Room)
                    .content(chatMessageDTO.getContent())
                    .creation_time(chatMessageDTO.getTimestamp().toString())
                    .build();
            messagesRepository.save(messages);
        }else{
            // TODO Exceptioin 처리
        }
    }

    // 존재하는 방인지 확인
    @Override
    public Optional<ChatRoom> isExistChatRoom(Long chatRoomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isPresent()){
            return chatRoom;
        }
        return Optional.empty();

    }

    // 사용자 채팅방에 존재하는 방인지 확인
    @Override
    public Optional<UserChatRoom> isExistUserChatRoom(Long chatRoomId) {
        Optional<UserChatRoom> chatRoom = userChatRoomRepository.findByChat_Id(chatRoomId);
        if(chatRoom.isPresent()){
            return chatRoom;
        }
        return Optional.empty();
    }

    // 채팅리스트 가져오기 ==> Token 완료 시 테스트해야함
    @Override
    public List<ChatRoomDTO> getChatLIst(Long userId) {
        List<ChatRoomDTO> AnsList = new ArrayList<>();
        //userChatRoomRepository.findByUser_Id(); // Token 에서 userId 추출
        // 테스트 용도로 넣어둠
        List<UserChatRoom> chatRoom = userChatRoomRepository.findByUser_Id(1L);

        // 내역 없으면 예외 처리
        if(chatRoom.isEmpty()){
            throw new BaseException(ChatErrorResponseCode.CHAT_LIST_NOT_FOUND);
        }
        for(UserChatRoom ucr : chatRoom){
            ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
                    .roomId(ucr.getId().toString())
                    .name(ucr.getChat().getName())
                    .build();

            Optional<Messages> chatContent = messagesRepository.findByChatRoomId(ucr.getId());
            if(chatContent.isPresent()){
                chatRoomDTO.setLast_message(chatContent.get().toString());
            }
            AnsList.add(chatRoomDTO);
        }
        return AnsList;
    }


    // TODO 채팅 내역 가져 오기
    @Override
    public List<ChatMessageDTO> getChatMessage(Long chatRoomId) {
        return null;
    }


    // ChatRoom 타입
    private RoomType typeOfRoom(ChatCreateDTO dto){
        if(dto.getRoomType().startsWith("D")){
            return RoomType.DIRECT;
        }
        if(dto.getRoomType().startsWith("B")){
            return RoomType.BASKETBALL;
        }
        if(dto.getRoomType().startsWith("M")){
            return RoomType.MATCHING;
        }
        return RoomType.TOGETHER;
    }


    // ChatRoom 이름
    private String nameOfRoom(ChatCreateDTO dto){
        // 같이하기
        if(dto.getRoomType().startsWith("T")){
            return RoomType.TOGETHER.getKey();
        }
        // 1:1, 농구장, 팀매칭 -> 이름 하나
        return dto.getName();
    }






}
