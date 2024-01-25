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
import sync.slamtalk.common.ErrorResponseCode;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

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
    private final UserRepository userRepository;

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


    // 채팅방 입장(STOMP: SUBSCRIBE)
    // 매 접속시 실행!
    @Override
    public void setUserChatRoom(Long userId, Long chatRoomId) {

        // userChatRoom 에 이미 있으면 바로 종료
        Optional<UserChatRoom> userChatRoomOptional = userChatRoomRepository.findByUserChatroom(userId, chatRoomId);
        if(userChatRoomOptional.isPresent()){
            return;
        }

        // user 가져오기
        Optional<User> userOptional = userRepository.findById(userId);

        // chatRoom 가져오기
        // UserChatRoom 추가하기
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoomId);
        if(chatRoomOptional.isPresent()) {
            ChatRoom chatRoom = chatRoomOptional.get();

            // UserChatRoom 생성
            UserChatRoom userChatRoom = UserChatRoom.builder()
                    .readIndex(0L) // 초기화
                    .isFirst(true) // 초기화
                    .chat(chatRoom)
                    .user(userOptional.get())
                    .build();
            // 저장
            userChatRoomRepository.save(userChatRoom);

            // ChatRoom 에도 userchatRoom 추가
            chatRoom.addUserChatRoom(userChatRoom);
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
                    .writer(chatMessageDTO.getSenderNickname())
                    .content(chatMessageDTO.getContent())
                    .creation_time(chatMessageDTO.getTimestamp().toString())
                    .build();
            messagesRepository.save(messages);
        }else{
            // TODO Exceptioin 처리
            throw new BaseException(ErrorResponseCode.CHAT_FAIL);
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

    // 사용자 채팅방에 존재 하는 방인지 확인
    @Override
    public Optional<UserChatRoom> isExistUserChatRoom(Long userId,Long chatRoomId) {

        Optional<UserChatRoom> userChatRoom = userChatRoomRepository.findByUserChatroom(userId, chatRoomId);
        if(userChatRoom.isPresent()){
            return userChatRoom;
        }
        return Optional.empty();
    }


    // 채팅리스트 가져오기
    @Override
    public List<ChatRoomDTO> getChatLIst(Long userId) {
        List<ChatRoomDTO> AnsList = new ArrayList<>();
        //userChatRoomRepository.findByUser_Id(); // Token 에서 userId 추출
        // 테스트 용도로 넣어둠
        List<UserChatRoom> chatRoom = userChatRoomRepository.findByUser_Id(userId);

        // 내역 없으면 예외 처리
        if(chatRoom.isEmpty()){
            throw new BaseException(ChatErrorResponseCode.CHAT_LIST_NOT_FOUND);
        }
        for(UserChatRoom ucr : chatRoom){
            ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
                    .roomId(ucr.getId().toString())
                    .name(ucr.getChat().getName())
                    .build();

            List<Messages> chatContent = messagesRepository.findByChatRoomId(ucr.getId());

            chatRoomDTO.setLast_message(chatContent.get(0).getContent());

            AnsList.add(chatRoomDTO);
        }
        return AnsList;
    }


    // 특정 방에서 주고 받은 모든 메세지 가져오기
    @Override
    public List<ChatMessageDTO> getChatMessage(Long chatRoomId) {
        List<ChatMessageDTO> ansList = new ArrayList<>();
        List<Messages> byChatRoomId = messagesRepository.findByChatRoomId(chatRoomId);

        // messageRepository 에서 가져온 메세지로 dto 생성하기
        for(Messages m : byChatRoomId){
            ChatMessageDTO messageDTO = ChatMessageDTO.builder()
                    .roomId(m.getChatRoom().getId().toString())
                    .senderNickname(m.getWriter())
                    .content(m.getContent())
                    .build();
            ansList.add(messageDTO);
        }
        return ansList;
    }


    // 특정 방에 저장된 메세지 중 가장 마지막 메세지 가져옴
    @Override
    public Messages getLastMessageFromChatRoom(Long chatRoomId) {

        List<Messages> allByChatRoom = messagesRepository.findAllByChatRoom(chatRoomId);
        return allByChatRoom.get(0);
    }

    // userChatRoom 에 readIndex 저장하기
    @Override
    public void saveReadIndex(Long userId,Long chatRoomId,Long readIndex) {

        // userchatRoomRepository 에서 userId 검색해서 chatRoom ( user가 참여한 chatRoomList 를 가져옴 )
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findByUser_Id(userId);


        // 가져온 userChatRoomList 에서 해당하는 chatRoomId 를 가진 userchatRoom 를 가져온다
        Optional<UserChatRoom> matchingChatRoom = userChatRooms.stream()
                .filter(userChatRoom -> userChatRoom.getChat().getId().equals(chatRoomId))
                .findFirst();

        // 해당하는 userchatRoom 의 readIndex 에 readIndex 를 업데이트
        if(matchingChatRoom.isPresent()){
            UserChatRoom userChatRoom = matchingChatRoom.get();
            userChatRoom.updateReadIndex(readIndex);
        }

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
