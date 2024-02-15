package sync.slamtalk.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import sync.slamtalk.map.repository.BasketballCourtRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
    private final ChatRoomRepository chatRoomRepository;
    private final MessagesRepository messagesRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final UserRepository userRepository;
    private final BasketballCourtRepository basketballCourtRepository;

    // 채팅방 생성(농구장:미리해놓기, 그외 모두 SUBSCRIBE 시에)
    @Override
    @Transactional
    public long createChatRoom(ChatCreateDTO chatCreateDTO) {

        // chatRoom 이 기존에 있는 지 검증
        // 채팅방 이름, 채팅방 타입
        Long creatorId = chatCreateDTO.getCreator_id();
        String roomName = chatCreateDTO.getName();
        String roomType = chatCreateDTO.getRoomType();
        RoomType rt = RoomType.BASKETBALL;

        switch (roomType){
            case "BM" : rt = RoomType.BASKETBALL;
            break;
            case "DM" : rt = RoomType.DIRECT;
            break;
            case "TM" : rt = RoomType.TOGETHER;
            break;
            case "MM" : rt = RoomType.MATCHING;
            break;
        }

        log.debug("roomType:{}",rt);
        String key = rt.getKey();

        // 참여자 + 유저 의 userchatroom 에 (채팅방이름, 채팅방타입) 이 동일하게 모두 있는 지 확인
        Optional<Long> optionalroomId = checkParticipnats(chatCreateDTO.getParticipants(), roomName, rt);
        // 존재하는 방이라면 chatRoomId 반환
        if(optionalroomId.isPresent()){
            Long userChatRoomId = optionalroomId.get();
            Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findById(userChatRoomId);
            return optionalUserChatRoom.get().getChat().getId();
        }


        // 기존에 없는 경우 새로운 채팅방 생성
        // 1:1 메세지만 채팅방 이름을 따로 받지 않음
        if(chatCreateDTO.getRoomType().equals(RoomType.DIRECT)){

            List<Long> partnerId = chatCreateDTO.getParticipants();
            Long pid = partnerId.get(0);
            Optional<User> optionalpartner = userRepository.findById(pid);

            // 파트너가 등록되지 않은 아이디이면 Exception
            if(optionalpartner.isEmpty()){
                throw new BaseException(ChatErrorResponseCode.CHAT_TARGET_NOT_FOUND);
            }

            // 채팅방 생성
            ChatRoom chatRoom = ChatRoom.builder()
                    .roomType(typeOfRoom(chatCreateDTO))
                    .name("DirectMessageRoom")
                    .build();
            ChatRoom save = chatRoomRepository.save(chatRoom);


            // 생성자 채팅방 목록에 추가
            Optional<Long> userChatRoom = createUserChatRoom(chatCreateDTO.getCreator_id(), save.getId());
            if(userChatRoom.isEmpty()){
                throw new BaseException(ChatErrorResponseCode.CHAT_ROOM_NOT_FOUND);
            }

            // 참여자 가져오기
            List<Long> participants = chatCreateDTO.getParticipants();
            // 참여자 채팅방 목록에 추가
            for(Long u : participants){
                createUserChatRoom(u,save.getId());
            }

            // 생성된 채팅방 아이디 반환
            return save.getId();
            // 농구장, 팀매칭, 같이해요 이름을 모두 받음
        }else{
            // 채팅방 생성
            ChatRoom chatRoom = ChatRoom.builder()
                    .roomType(typeOfRoom(chatCreateDTO))
                    .name(chatCreateDTO.getName())
                    .build();
            ChatRoom save = chatRoomRepository.save(chatRoom);

            // 생성자 채팅방 목록에 추가
            createUserChatRoom(chatCreateDTO.getCreator_id(), save.getId());

            // 참여자 채팅방 목록에 추가
            List<Long> participants = chatCreateDTO.getParticipants();
            for(Long u : participants){
                createUserChatRoom(u,save.getId());
            }

            // 생성된 채팅방 아이디 반환
            return save.getId();
        }
    }



    // 채팅방에 메세지 저장(STOMP: SEND)
    @Override
    @Transactional
    public void saveMessage(ChatMessageDTO chatMessageDTO) {
        long chatRoomId = Long.parseLong(chatMessageDTO.getRoomId());
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if(chatRoom.isPresent()){ // chatRoom 이 존재하면
            ChatRoom Room = chatRoom.get();
            // Message create
            Messages messages = Messages.builder()
                    .chatRoom(Room)
                    .senderId(chatMessageDTO.getSenderId())
                    .senderNickname(chatMessageDTO.getSenderNickname())
                    .content(chatMessageDTO.getContent())
                    .creation_time(chatMessageDTO.getTimestamp().toString())
                    .build();
            messagesRepository.save(messages);
        }else{
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
        List<ChatRoomDTO> chatRooms = new ArrayList<>();

        // 유저가 가지고 있는 채팅방 모두 가져오기
        log.debug("userId:{}",userId);
        List<UserChatRoom> chatRoom = userChatRoomRepository.findByUser_Id(userId);
        if(chatRoom.isEmpty()){
            log.debug("nothing");
            return null;
        }

        // 유저가 가지고 있는 채팅방 리스트를 돌면서 가져오기
        for(UserChatRoom ucr : chatRoom){

            boolean isDelete = ucr.getIsDeleted().booleanValue();
            log.debug("isDelete:{}",isDelete);


            // 삭제 되지 않은 채팅방만 가져옴
            if(!isDelete){

                String profile = null;

                ChatRoomDTO dto = ChatRoomDTO.builder()
                        .roomId(ucr.getId().toString())
                        .roomType(ucr.getRoomType().toString())
                        .imgUrl(profile)
                        .name(ucr.getChat().getName())
                        .roomId(ucr.getChat().getId().toString())
                        .build();

                // 1:1 인 경우 상대방 프로필
                // 1:1, 팀매칭만 상대방 프로필 나머지(같이하기, 농구장은 디폴트 프로필)
                if(ucr.getRoomType().equals(RoomType.DIRECT) || ucr.getRoomType().equals(RoomType.MATCHING)){
                    log.debug("여기까지옴??");
                    List<UserChatRoom> optionalList = userChatRoomRepository.findByChat_Id(ucr.getChat().getId());
                    for(UserChatRoom x : optionalList){
                        if(!x.getUser().getId().equals(userId) && x.getIsDeleted().equals(Boolean.FALSE)){
                            // 자기 자신이 아니고, 삭제가 되지 않은 경우
                            profile = x.getUser().getImageUrl();
                            dto.updateImgUrl(profile);
                            if(ucr.getRoomType().equals(RoomType.DIRECT)){
                                dto.updateName(x.getUser().getNickname());
                            }
                        }
                    }
                }

                if(ucr.getRoomType().equals(RoomType.BASKETBALL)){
                    dto.updatecourtId(ucr.getChat().getBasketballCourt().getCourtId());
                }
                // 마지막 메세지
                PageRequest pageRequest = PageRequest.of(0, 1);
                Page<Messages> latestByChatRoomId = messagesRepository.findLatestByChatRoomId(ucr.getChat().getId(), pageRequest);
                if(latestByChatRoomId.hasContent()){
                    Stream<Messages> messagesStream = latestByChatRoomId.get();
                    Messages messages = messagesStream.collect(Collectors.toList()).get(0);

                    dto.setLast_message(messages.getContent());
                    chatRooms.add(dto);
                }
                if(latestByChatRoomId.isEmpty()){
                    dto.setLast_message("주고 받은 메세지가 없습니다.");
                    chatRooms.add(dto);
                }

            }
        }
        return chatRooms;
    }


    // 특정 방에서 주고 받은 모든 메세지 가져오기
    @Override
    public List<ChatMessageDTO> getChatMessage(Long chatRoomId, Long messageId) {
        List<ChatMessageDTO> ansList = new ArrayList<>();

        // 특정 방 메세지 중 현재 messageId 보다 큰 Id값을 가진 메세지들 가져오기
        List<Messages> newMessages = messagesRepository.findByChatRoomIdAndIdGreaterThan(chatRoomId, messageId);


        // messageRepository 에서 가져온 메세지로 dto 생성하기
        for(Messages m : newMessages){

            // 작성자 이미지 가져오기
            Long senderId = m.getSenderId();
            Optional<User> optionalUser = userRepository.findById(senderId);
            String imageUrl = "";
            if(optionalUser.isPresent()){
                User user = optionalUser.get();
                imageUrl = user.getImageUrl();
            }else{
                imageUrl = "null";
            }
            ChatMessageDTO messageDTO = ChatMessageDTO.builder()
                    .messageId(m.getId().toString())
                    .roomId(m.getChatRoom().getId().toString())
                    .senderId(m.getSenderId())
                    .senderNickname(m.getSenderNickname())
                    .imgUrl(imageUrl)
                    .content(m.getContent())
                    .timestamp(m.getCreation_time())
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

        Optional<UserChatRoom> matchingChatRoom = userChatRoomRepository.findByUserChatroom(userId, chatRoomId);

        // 해당하는 userchatRoom 의 readIndex 에 readIndex 를 업데이트
        if(matchingChatRoom.isPresent()){
            UserChatRoom userChatRoom = matchingChatRoom.get();
            userChatRoom.updateReadIndex(readIndex);
        }
    }


    // 특정방을 나갈 때 userChatRoom softDelete
    @Override
    @Transactional
    public Optional<UserChatRoom> exitRoom(Long userId, Long chatRoomId) {
        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findByUserChatroom(userId, chatRoomId);

        // 사용자 채팅방 가져와서 softDelete = true 처리
        if(optionalUserChatRoom.isPresent()){
            UserChatRoom userChatRoom = optionalUserChatRoom.get();
            userChatRoom.delete();
            return optionalUserChatRoom;
        }

        return Optional.empty();
    }

    @Override
    public Optional<Boolean> isVisitedFirst(Long userId, Long roomId) {
        Optional<UserChatRoom> optionaluserchatRoom = userChatRoomRepository.findByUserChatroom(userId, roomId);
        // 해당 유저아이디와 해당 채팅방 아이디로 Userchatroom 가져오기
        if(optionaluserchatRoom.isPresent()){
            UserChatRoom userChatRoom = optionaluserchatRoom.get();
            // 아직 방문하지 않았다면 == true
            if(userChatRoom.getIsFirst().equals(Boolean.TRUE)){
                userChatRoom.updateIsFirst(Boolean.FALSE);
                return Optional.of(Boolean.TRUE);
            }
            return Optional.empty();
        }
        throw new BaseException(ChatErrorResponseCode.CHAT_ROOM_NOT_FOUND);
        // 존재하지 않는다면
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

    // userChatRoom 에 추가하기
    public Optional<Long> createUserChatRoom(Long userId, Long chatRoomId){

        // 유저 필드 가져오기
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            throw new BaseException(ErrorResponseCode.CHAT_FAIL);
        }
        User user = optionalUser.get();

        // 채팅방 필드 가져오기
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoomId);
        if(chatRoomOptional.isPresent()){

            ChatRoom chatRoom = chatRoomOptional.get();

            // userChatRoom entity 생성
            UserChatRoom userChatRoom = UserChatRoom.builder()
                    .user(user)
                    .roomType(chatRoom.getRoomType())
                    .chat(chatRoom)
                    .isFirst(true) // 방문 초기화
                    .readIndex(0L) // 읽은 메세지 초기화
                    .imageUrl(user.getImageUrl())
                    .build();

            // userChatRoom entity 저장
            UserChatRoom saved = userChatRoomRepository.save(userChatRoom);

            // chatRoom 에 userChatRoom 추가
            //chatRoom.addUserChatRoom(saved);

            return Optional.ofNullable(saved.getId());
        }
        return Optional.empty();
    }



    private Optional<Long> checkParticipnats(List<Long> pid,String roomName,RoomType roomType){
        Long chatRoomId = 0L;
        for(Long partner : pid){
            // 특정 유저, 특정 방이름, 특정 타입이 userChatRoomRepository 에 존재하는 지 확인
            Optional<UserChatRoom> byUserChatroomExist = userChatRoomRepository.findByUserChatroomExist(partner, roomName, roomType);
            // 존재하지않거나 || 방을 나간 경우
            if(byUserChatroomExist.isEmpty() || byUserChatroomExist.get().getIsDeleted().equals(true)){
                return Optional.empty();
            }
            chatRoomId = byUserChatroomExist.get().getId();
        }

        return Optional.of(chatRoomId);
    }

}
