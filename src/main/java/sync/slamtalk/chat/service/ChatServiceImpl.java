package sync.slamtalk.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.chat.dto.ChatErrorResponseCode;
import sync.slamtalk.chat.dto.Request.ChatCreateDTO;
import sync.slamtalk.chat.dto.Request.ChatMessageDTO;
import sync.slamtalk.chat.dto.Response.ChatRoomDTO;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.Messages;
import sync.slamtalk.chat.entity.RoomType;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.redis.RedisService;
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
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
    private final ChatRoomRepository chatRoomRepository;
    private final MessagesRepository messagesRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final UserRepository userRepository;
    private final BasketballCourtRepository basketballCourtRepository;
    private final RedisService redisService;

    // 채팅방 생성
    // * 생성시점에 userChatRoom 에 추가됨 *
    @Override
    public long createChatRoom(ChatCreateDTO chatCreateDTO) {
        long roomNum = 0L;
        RoomType roomType = RoomType.DIRECT;

        switch (chatCreateDTO.getRoomType()){
            case "DM" : break;
            case "TM" : roomType = RoomType.TOGETHER;
            break;
            case "MM" : roomType = RoomType.MATCHING;
            break;
        }

        // DM
        if(roomType.equals(RoomType.DIRECT)){
            List<Long> participants = chatCreateDTO.getParticipants();
            Long userA = participants.get(0);
            Long userB = participants.get(1);

            // 유저 한명만 조회하면 됨 다른 유저도 똑같이 상대 유저의 id 를 directId 로 가지고 있을 것이기 때문
            Optional<UserChatRoom> optionalUC1 = userChatRoomRepository.findByDirectId(userA, userB);
            if(optionalUC1.isPresent()){
                log.debug("동일한 디엠방 존재함");
                return optionalUC1.get().getChat().getId();
            }
        }

        // TM,MM 은 id 로 검사
        if(roomType.equals(RoomType.TOGETHER)){
            Long togetherId = chatCreateDTO.getTogether_id();
            Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByTogetherId(chatCreateDTO.getTogether_id());

            // 채팅방에 해당하는 팀매칭 아이디 채팅방이 존재한다면 기존에 채팅방 정보 반환
            // 새로 생성 x
            if(optionalChatRoom.isPresent()) {
                return optionalChatRoom.get().getId();
            }
        }

        if(roomType.equals(RoomType.MATCHING)){
            Long teamMatchingId = chatCreateDTO.getTeamMatching_id();
            Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByTeamMatchingId(chatCreateDTO.getTeamMatching_id());

            // 채팅방에 존재한다면 새로 생성 x
            if(optionalChatRoom.isPresent()){
                return optionalChatRoom.get().getId();
            }
        }


        // 위에서 종료되지 않았으면 새로 생성
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .roomType(roomType)
                //.basketballCourt()
                .togetherId(chatCreateDTO.getTogether_id())
                .teamMatchingId(chatCreateDTO.getTeamMatching_id())
                .name(chatCreateDTO.getName())
                .build();

        ChatRoom saved = chatRoomRepository.save(chatRoom);
        roomNum = saved.getId();
        
        
        // direct 상대방 아이디
        List<Long> participants = chatCreateDTO.getParticipants();
        Long userA = participants.get(0);
        Long userB = participants.get(1);
        List<Long> directs = new ArrayList<>();
        directs.add(userA);
        directs.add(userB);

        int dIndex = 1;
        for(Long user : chatCreateDTO.getParticipants()){

            Optional<User> optionalUser = userRepository.findById(user);

            UserChatRoom userChatRoom = UserChatRoom.builder()
                    .user(optionalUser.get())
                    .isFirst(true)
                    .roomType(roomType)
                    .readIndex(0L)
                    .chat(saved)
                    .name(chatCreateDTO.getName())
                    .togetherId(chatCreateDTO.getTogether_id())
                    .build();

            if(roomType.equals(RoomType.DIRECT)){
                userChatRoom.setDirectId(directs.get(dIndex--));
            }
            UserChatRoom savedUserChatRoom = userChatRoomRepository.save(userChatRoom);
        }

        return roomNum;
    }


    // 농구장 채팅방 생성
    @Override
    public long createBasketballChatRoom(ChatCreateDTO chatCreateDTO) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatCreateDTO.getName())
                .roomType(RoomType.BASKETBALL)
                .build();
        ChatRoom saved = chatRoomRepository.save(chatRoom);
        return saved.getId();
    }


    // 채팅방에 메세지 저장(STOMP: SEND)
    @Override
    public void saveMessage(ChatMessageDTO chatMessageDTO) {
        long chatRoomId = Long.parseLong(chatMessageDTO.getRoomId());
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if(chatRoom.isPresent()){ // chatRoom 이 존재하면

            ChatRoom Room = chatRoom.get();

            // messages 저장
            // Message create
            Messages messages = Messages.builder()
                    .chatRoom(Room)
                    .senderId(chatMessageDTO.getSenderId())
                    .senderNickname(chatMessageDTO.getSenderNickname())
                    .content(chatMessageDTO.getContent())
                    .creation_time(chatMessageDTO.getTimestamp().toString())
                    .build();
            messagesRepository.save(messages);

            chatMessageDTO.setMessageId(messages.getId().toString());

            // redis 먼저 저장
            redisService.saveMessage(chatMessageDTO,43200);

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
            log.debug("=========== 유저가 가지고 있는 채팅방이름 : {}, 채팅방 아이디 : {}",ucr.getChat().getName(),ucr.getChat().getId());
            log.debug("isDelete:{}",isDelete);


            // 삭제 되지 않은 채팅방만 가져옴
            if(isDelete==false){

                String profile = null;
                if(ucr.getRoomType()==null){
                    log.debug("getroomType null!!!!");
                }

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
                    List<UserChatRoom> optionalList = userChatRoomRepository.findByChat_Id(ucr.getChat().getId());

                    if(optionalList.isEmpty()){
                        log.debug("1:1,팀매칭인데 가져온 채팅방 아이디 조회했을 때 가져온게 없음 : {}",ucr.getChat().getId());
                    }

                    for(UserChatRoom x : optionalList){
                        if(x.getUser().getId().equals(userId) && x.getIsDeleted().equals(Boolean.FALSE)){ //&& !x.getUser().getId().equals(userId)
                            // 자기 자신이고, 삭제가 되지 않은 경우
                            Long directId = x.getDirectId();
                            Optional<User> optionalUser = userRepository.findById(directId);

                            log.debug("현재 유저 : {}, 가져오려는 방 : {}",userId,directId);

                            if(optionalUser.isPresent()){
                                profile = optionalUser.get().getImageUrl();
                                dto.updateImgUrl(profile);
                                dto.updateName(optionalUser.get().getNickname());
                            }

                            log.debug("지금 내 자신 : {}",userId);
                            log.debug("자기 자신이 아닌 상대방 :{}",directId);
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
    @Transactional
    public List<ChatMessageDTO> getChatMessages(Long chatRoomId, Long messageId) {
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

            // Redis 캐싱
            redisService.saveMessage(messageDTO,43200); // 12시간
            log.debug("redis 캐싱 다음줄");
        }
        return ansList;
    }


    // 과거 메세지 추가 요청
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<ChatMessageDTO>getPreviousChatMessages(Long userId, Long chatRoomId,int count) {

        List<ChatMessageDTO> chatMessageDTOList = new ArrayList<>();

        // 추가로 가져올 메세지 갯수
        int needCnt = 20 * count;

        // redis 먼저 조회
        Optional<List<ChatMessageDTO>> optionalList = redisFirstDataBaseLater(userId, chatRoomId, needCnt);

        // redis 로 불러온 내역이 없는 경우
        if(optionalList.isEmpty()) {
            log.debug("===redis 로 불러온 내역이 없음====");

            // redis에 없는 경우 DB조회
            Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoomId);
            Optional<UserChatRoom> existUserChatRoom = isExistUserChatRoom(userId, chatRoomId);

            if (existUserChatRoom.isEmpty()) {
                log.debug("userChatRoom 존재하지않음");
                throw new BaseException(ChatErrorResponseCode.CHAT_ROOM_NOT_FOUND);
            }

            if (existUserChatRoom.isPresent()) {
                log.debug("userChatroom 존재함");
                UserChatRoom userChatRoom = existUserChatRoom.get();
                Long readIndex = userChatRoom.getReadIndex();
                log.debug("=== readIndex : {}",readIndex);

                // readIndex 가 초기값이면 바로 null return
                if(readIndex.equals(0L)){
                    return null;
                }

                // 20개씩 내역 페이징
                Pageable pageable = PageRequest.of(0, 20); // 첫 페이지, 최대 20개
                List<Messages> byChatRoomIdAndMessageIdLessThanOrderedByMessageIdDesc = messagesRepository.findByChatRoomIdAndMessageIdLessThanOrderedByMessageIdDesc(chatRoomId, readIndex, pageable);

                // 메세지가 아예 없는 경우
                if (byChatRoomIdAndMessageIdLessThanOrderedByMessageIdDesc.isEmpty()) {
                    log.debug("채팅방에 아직 메세지 없음");
                    throw new BaseException(ChatErrorResponseCode.CHAT_ROOM_NO_HISTORY_YET);
                }

                // 메세지가 있는 경우
                for (Messages msg : byChatRoomIdAndMessageIdLessThanOrderedByMessageIdDesc) {
//                    log.debug("messageId: {}", msg.getId());
//                    log.debug("content: {}", msg.getContent());

                    log.debug("db에서 메세지 불러오기 성공");
                    Optional<User> optionalUser = userRepository.findById(msg.getSenderId());
                    String senderImgUrl = null;
                    if(optionalUser.isPresent()){
                        senderImgUrl = optionalUser.get().getImageUrl();
                    }

                    ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                            .messageId(msg.getId().toString())
                            .senderId(msg.getSenderId())
                            .roomId(msg.getChatRoom().getId().toString())
                            .content(msg.getContent())
                            .senderNickname(msg.getSenderNickname())
                            .timestamp(msg.getCreation_time())
                            .imgUrl(senderImgUrl)
                            .build();
                    chatMessageDTOList.add(chatMessageDTO);

                    // db에서 페이징하는 동시에 레디스에 내역 저장
                    redisService.saveMessage(chatMessageDTO,43200);
                    log.debug("db페이징 후 레디스에 내역 저장");
                }
            }
        }
        // redis 에서 가져온 내역이 있는 경우

        // TODO 갯수 20개 못가져온경우 추가적으로 db 페이징
        List<ChatMessageDTO> chatMessageDTOS = optionalList.get();
        String messageId = chatMessageDTOS.get(chatMessageDTOS.size() - 1).getMessageId();
        if(chatMessageDTOS.size()<needCnt){
            int more = needCnt - chatMessageDTOS.size();
            // 20 개 - redis 로 가져온 내역 갯수 = 추가 내역 페이징
            Pageable pageable = PageRequest.of(0, more); // 첫 페이지, 최대 20개
            List<Messages> messagesList = messagesRepository.findByChatRoomIdAndMessageIdLessThanOrderedByMessageIdDesc(chatRoomId, Long.parseLong(messageId), pageable);
            for(Messages m : messagesList){
                Optional<User> optionalUser = userRepository.findById(m.getSenderId());
                String imgUrl = null;
                if(optionalUser.isPresent()){
                    imgUrl = optionalUser.get().getImageUrl();
                }

                ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                        .messageId(m.getId().toString())
                        .senderId(m.getSenderId())
                        .roomId(m.getChatRoom().getId().toString())
                        .content(m.getContent())
                        .senderNickname(m.getSenderNickname())
                        .timestamp(m.getCreation_time())
                        .imgUrl(imgUrl)
                        .build();
                optionalList.get().add(chatMessageDTO);
            }
        }

        return optionalList.get();
    }

    // 특정 방에 저장된 메세지 중 가장 마지막 메세지 가져옴
    @Override
    public Messages getLastMessageFromChatRoom(Long chatRoomId) {

        List<Messages> allByChatRoom = messagesRepository.findAllByChatRoom(chatRoomId);
        return allByChatRoom.get(0);
    }

    // userChatRoom 에 readIndex 저장하기
    @Override
    @Transactional
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
            // 이미 방문을 했다면
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
                    .BasketBallId(chatRoom.getBasketBallId())
                    .name(chatRoom.getName())
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


    // redis 에서 메세지 가져오기
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Optional<List<ChatMessageDTO>> redisFirstDataBaseLater(Long userId,Long chatRoomId,int count){

        List<ChatMessageDTO> msgList = new ArrayList<>();
        
        // user의 readIndex 를 가져와야함
        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findByUserChatroom(userId, chatRoomId);

        // userChatRoom에서 방의 존재가 확인이 안되는 경우 empty 로 반환
        if(optionalUserChatRoom.isEmpty()){
            throw new BaseException(ChatErrorResponseCode.CHAT_ROOM_NOT_FOUND);
        }

        UserChatRoom userChatRoom = optionalUserChatRoom.get();
        // 특정 유저가 특정 채팅방에 가지고 있는 readIndex
        Long readIndex = userChatRoom.getReadIndex();
        log.debug("유저의 ReadIndex:{}",readIndex);

        // redis에서 과거 내역 조회 20개씩
        List<ChatMessageDTO> messages = redisService.getMessages(chatRoomId, readIndex);

        // redis에서 가져온데이터가 없으면 empty return
        if(messages.isEmpty()){
            log.debug("레디스로 가져온 데이터가 없습니다.");
            return Optional.empty();
        }

        // redis 로 가져온데이터 반환
        return Optional.of(messages);
    }


}
