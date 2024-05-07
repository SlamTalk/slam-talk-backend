package sync.slamtalk.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.chat.dto.ChatErrorResponseCode;
import sync.slamtalk.chat.dto.request.ChatCreateDTO;
import sync.slamtalk.chat.dto.request.ChatMessageDTO;
import sync.slamtalk.chat.dto.response.ChatRoomDTO;
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
import sync.slamtalk.map.entity.BasketballCourt;
import sync.slamtalk.map.repository.BasketballCourtRepository;
import sync.slamtalk.notification.NotificationSender;
import sync.slamtalk.notification.dto.request.NotificationRequest;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessagesRepository messagesRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final UserRepository userRepository;
    private final BasketballCourtRepository basketballCourtRepository;
    private final RedisService redisService;
    private final NotificationSender notificationSender;

    /**
     * 채팅방을 생성한다.
     *
     * @param chatCreateDTO : 채팅방 생성 요청 DTO
     * @return chatRoomId
     */
    @Override
    public long createChatRoom(ChatCreateDTO chatCreateDTO) {
        long roomNum = 0L;
        RoomType roomType = RoomType.DIRECT;

        switch (chatCreateDTO.getRoomType()) {
            case "DM":
                break;
            case "TM":
                roomType = RoomType.TOGETHER;
                break;
            case "MM":
                roomType = RoomType.MATCHING;
                break;
        }

        // DM
        if (roomType.equals(RoomType.DIRECT)) {
            List<Long> participants = chatCreateDTO.getParticipants();
            Long userA = participants.get(0);
            Long userB = participants.get(1);

            Optional<UserChatRoom> optionalChatRoomA = userChatRoomRepository.findByDirectId(userA, userB);
            Optional<UserChatRoom> optionalChatRoomB = userChatRoomRepository.findByDirectId(userB, userA);

            if (optionalChatRoomA.isPresent() && optionalChatRoomB.isPresent()) {
                // A유저의 채팅방 중 roomType = DIRECT && directId 가 B유저
                // B유저의 채팅방 중 roomType = DIRECT && directId 가 A유저

                // A 유저, B 유저 모두 삭제 하지 않은 경우
                if (Boolean.TRUE.equals(!optionalChatRoomA.get().getIsDeleted()) && Boolean.TRUE.equals(!optionalChatRoomB.get().getIsDeleted())) {
                    // A유저 B유저 모두 동일한 채팅방 아이디를 가지고 있을 것이기 때문
                    return optionalChatRoomA.get().getChat().getId();
                }
                // A 유저, B 유저 둘 중 하나라도 삭제한 경우 새로 생성

            }

        }

        // TM,MM 은 id 로 검사
        if (roomType.equals(RoomType.TOGETHER)) {
            Long togetherId = chatCreateDTO.getTogetherId();
            Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByTogetherId(togetherId);

            // 채팅방에 해당하는 팀매칭 아이디 채팅방이 존재한다면 기존에 채팅방 정보 반환
            // 새로 생성 x
            if (optionalChatRoom.isPresent()) {
                return optionalChatRoom.get().getId();
            }
        }

        if (roomType.equals(RoomType.MATCHING)) {
            Long teamMatchingId = chatCreateDTO.getTeamMatchingId();
            Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByTeamMatchingId(teamMatchingId);

            // 채팅방에 존재한다면 새로 생성 x
            if (optionalChatRoom.isPresent()) {
                return optionalChatRoom.get().getId();
            }
        }


        // 위에서 종료되지 않았으면 새로 생성
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .roomType(roomType)
                .togetherId(chatCreateDTO.getTogetherId())
                .teamMatchingId(chatCreateDTO.getTeamMatchingId())
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
        for (Long user : chatCreateDTO.getParticipants()) {

            Optional<User> optionalUser = userRepository.findById(user);

            // UserChatRoom 생성
            UserChatRoom userChatRoom = UserChatRoom.builder()
                    .user(optionalUser.get())
                    .isFirst(true)
                    .roomType(roomType)
                    .readIndex(0L)
                    .chat(saved)
                    .name(chatCreateDTO.getName())
                    .togetherId(chatCreateDTO.getTogetherId())
                    .teamMatchingId(chatCreateDTO.getTeamMatchingId())
                    .build();

            if (roomType.equals(RoomType.DIRECT) || roomType.equals(RoomType.MATCHING)) {
                userChatRoom.setDirectId(directs.get(dIndex--));
            }
            UserChatRoom savedUserChatRoom = userChatRoomRepository.save(userChatRoom);
            log.debug("userChatRoom 저장 완료 : {}", savedUserChatRoom.getChat().getId());
        }

        // 생성 완료에 따른 알림
        for(Long id : participants){
            log.debug("알림을 줄 참여자 아이디 : {}",id);
            NotificationRequest req = NotificationRequest.of("채팅방이 생성되었습니다","",Set.of(id));
            notificationSender.send(req);
        }
        return roomNum;
    }

    /**
     * 특정 채팅방에 참여하고 있는 유저들에게 새로운 메세지 알림
     *
     * @param  roomId : 채팅방 Id
     */
    @Override
    public void notificationMessage(Long roomId) {
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findByChat_Id(roomId);
        for(UserChatRoom u : userChatRooms){
            NotificationRequest req = NotificationRequest.of("새로운 메세지가 도착했습니다.","",Set.of(u.getUser().getId()));
            notificationSender.send(req);
        }
    }

    /**
     * 농구장 채팅방을 생성한다.
     *
     * @param chatCreateDTO 채팅방 생성 시 필요한 요청 정보
     * @return chatRoomId(basketBall)
     */
    @Override
    public long createBasketballChatRoom(ChatCreateDTO chatCreateDTO) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatCreateDTO.getName())
                .roomType(RoomType.BASKETBALL)
                .basketBallId(chatCreateDTO.getBasketBallId())
                .build();
        ChatRoom saved = chatRoomRepository.save(chatRoom);

        // 농구장에 채팅방 연결
        Optional<BasketballCourt> optionalBasketballCourt = basketballCourtRepository.findById(chatCreateDTO.getBasketBallId());
        optionalBasketballCourt.ifPresent(saved::setBasketballCourt);

        return saved.getId();
    }


    /**
     * 채팅방에서 발생한 메세지를 저장한다.
     *
     * @param chatMessageDTO 발행된 메세지에 대한 정보
     */
    @Override
    public void saveMessage(ChatMessageDTO chatMessageDTO) {
        long chatRoomId = Long.parseLong(Objects.requireNonNull(chatMessageDTO.getRoomId()));
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isPresent()) { // chatRoom 이 존재하면

            ChatRoom room = chatRoom.get();

            // messages 저장
            // Message create
            Messages messages = Messages.builder()
                    .chatRoom(room)
                    .senderId(chatMessageDTO.getSenderId())
                    .senderNickname(chatMessageDTO.getSenderNickname())
                    .content(chatMessageDTO.getContent())
                    .creationTime(chatMessageDTO.getTimestamp())
                    .build();
            messagesRepository.save(messages);

            chatMessageDTO.setMessageId(messages.getId().toString());

            // redis 먼저 저장
            redisService.saveMessage(chatMessageDTO, 43200);

        } else {
            throw new BaseException(ErrorResponseCode.CHAT_FAIL);
        }
    }


    /**
     * 채팅방 존재를 검증한다.
     *
     * @param chatRoomId 채팅방 아이디
     * @return ChatRoom
     */
    @Override
    public Optional<ChatRoom> isExistChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId);

    }

    /**
     * 사용자가 참여하고 있는 채팅방인지 검사한다.
     *
     * @param userId     사용자 아이디
     * @param chatRoomId 채팅방 아이디
     * @return
     */
    @Override
    public Optional<UserChatRoom> isExistUserChatRoom(Long userId, Long chatRoomId) {
        return userChatRoomRepository.findByUserChatroom(userId, chatRoomId);
    }


    /**
     * 채팅리스트를 조회한다.
     *
     * @param userId 사용자 아이디
     * @return List(ChatRoomDTO) 채팅방 리스트
     */
    @Override
    public List<ChatRoomDTO> getChatLIst(Long userId) {
        List<ChatRoomDTO> chatRooms = new ArrayList<>();

        // 유저가 가지고 있는 채팅방 모두 가져오기
        log.debug("userId:{}", userId);
        List<UserChatRoom> chatRoom = userChatRoomRepository.findByUser_Id(userId);
        if (chatRoom.isEmpty()) {
            log.debug("유저가 가지고 있는 채팅방이 없습니다.");
            return Collections.emptyList();
        }


        // 유저가 가지고 있는 채팅방 리스트를 돌면서 가져오기
        for (UserChatRoom ucr : chatRoom) {

            boolean isDelete = ucr.getIsDeleted();

            // 삭제 되지 않은 채팅방만 가져옴
            if (!isDelete) {

                String profile = null;
                ChatRoomDTO dto = ChatRoomDTO.builder()
                        .roomId(ucr.getId().toString())
                        .roomType(ucr.getRoomType().toString())
                        .imgUrl(profile)
                        .name(ucr.getChat().getName())
                        .roomId(ucr.getChat().getId().toString())
                        .build();

                // 1:1 인 경우 상대방 프로필
                // 1:1 , 팀매칭만 상대방 프로필 나머지(같이하기, 농구장은 디폴트 프로필)
                if (ucr.getRoomType().equals(RoomType.DIRECT) || ucr.getRoomType().equals(RoomType.MATCHING)) {
                    List<UserChatRoom> optionalList = userChatRoomRepository.findByChat_Id(ucr.getChat().getId());

                    if (optionalList.isEmpty()) {
                        log.debug("1:1인데 가져온 채팅방 아이디 조회했을 때 가져온게 없음 : {}", ucr.getChat().getId());
                    }

                    for (UserChatRoom x : optionalList) {
                        if (x.getUser().getId().equals(userId) && x.getIsDeleted().equals(Boolean.FALSE)) { //&& !x.getUser().getId().equals(userId)
                            // 유저 가지고 있는 것중 삭제되지 않은 채팅방

                            // directId 는 상대방의 아이디
                            Long directId = x.getDirectId();
                            Optional<User> optionalUser = userRepository.findById(directId);

                            log.debug("현재 유저 : {}, 가져오려는 방 : {}", userId, directId);


                            // 상대방의 이미지, 상대방 이름, 상대방 아이디 저장
                            if (optionalUser.isPresent()) {
                                profile = optionalUser.get().getImageUrl();
                                dto.updateImgUrl(profile);
                                dto.updateName(optionalUser.get().getNickname());
                                dto.updatePartnerId(optionalUser.get().getId().toString());
                            }
                        }
                    }
                }

                // 팀매칭 게시판 아이디
                if (ucr.getRoomType().equals(RoomType.MATCHING)) {
                    dto.updatePartnerId(ucr.getTeamMatchingId().toString());
                }

                // 같이하기 게시판 아이디
                if (ucr.getRoomType().equals(RoomType.TOGETHER)) {
                    dto.updatePartnerId(ucr.getTogetherId().toString());
                }

                // 농구장 아이디
                if (ucr.getRoomType().equals(RoomType.BASKETBALL)) {
                    dto.updatecourtId(ucr.getChat().getBasketBallId());
                }


                // 마지막 메세지
                PageRequest pageRequest = PageRequest.of(0, 1);
                Page<Messages> latestByChatRoomId = messagesRepository.findLatestByChatRoomId(ucr.getChat().getId(), pageRequest);
                // 메세지 내용이 있는 경우만(메세지를 보내적이 없는 채팅방의 경우에는 해당하지 않음)
                if (latestByChatRoomId.hasContent()) {
                    Stream<Messages> messagesStream = latestByChatRoomId.get();
                    Messages messages = messagesStream.toList().get(0);

                    dto.setLast_message(messages.getContent());
                    dto.setLastMessageTime(messages.getCreationTime());
                    chatRooms.add(dto);

                    if(!messages.getId().equals(ucr.getReadIndex())){
                        dto.updateNoReadCnt(true);
                    }
                    //log.debug("마지막메세지아이디:{}",messages.getId());
                    //log.debug("유저가가지고있는메세지아이디:{}",ucr.getReadIndex());
                }
                if (latestByChatRoomId.isEmpty()) {
                    dto.setLast_message("주고 받은 메세지가 없습니다.");
                    chatRooms.add(dto);
                }

            }
        }
        // 마지막 메세지 날짜 순으로 채팅방 리스트 정렬
        // 발행된 메세지가 없는 경우 null 이므로, 별도처리
        Collections.sort(chatRooms,Comparator.comparing(ChatRoomDTO::getLastMessageTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return chatRooms;
    }


    /**
     * 특정 방에서 주고 받은 모든 메세지를 조회한다.
     *
     * @param chatRoomId
     * @param messageId
     * @return List(ChatMessageDTO)
     */
    @Override
    @Transactional
    public List<ChatMessageDTO> getChatMessages(Long chatRoomId, Long messageId) {
        List<ChatMessageDTO> ansList = new ArrayList<>();

        // ReadIndex 상관없이 가장 최신 메세지부터 30개씩 과거 메세지를 가져오기
        Pageable pageable = PageRequest.of(0,30);
        List<Messages> newMessages = messagesRepository.findByChatRoomIdAndMessageIdOrderByMessageIdDesc(chatRoomId, pageable);

        // 정렬
        Collections.sort(newMessages,Comparator.comparing(Messages::getId));

        // messageRepository 에서 가져온 메세지로 dto 생성하기
        for (Messages m : newMessages) {

            // 작성자 이미지 가져오기
            Long senderId = m.getSenderId();
            Optional<User> optionalUser = userRepository.findById(senderId);
            String imageUrl = "";
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                imageUrl = user.getImageUrl();
            } else {
                imageUrl = "null";
            }
            ChatMessageDTO messageDTO = ChatMessageDTO.builder()
                    .messageId(m.getId().toString())
                    .roomId(m.getChatRoom().getId().toString())
                    .senderId(m.getSenderId())
                    .senderNickname(m.getSenderNickname())
                    .imgUrl(imageUrl)
                    .content(m.getContent())
                    .timestamp(m.getCreationTime())
                    .build();
            ansList.add(messageDTO);

            // Redis 캐싱
            redisService.saveMessage(messageDTO, 43200); // 12시간
            log.debug("redis 캐싱 다음줄");
        }
        return ansList;
    }


    /**
     * 과거 메세지를 조회한다.
     *
     * @param userId     사용자 아이디
     * @param chatRoomId 채팅방 아이디
     * @param lastMessageId      기준 메세지 아이디
     * @return List(ChatMessageDTO)
     */
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<ChatMessageDTO>getPreviousChatMessages(Long userId, Long chatRoomId,Long lastMessageId) {

        List<ChatMessageDTO> chatMessageDTOList = new ArrayList<>();

        // 추가로 가져올 메세지 갯수
        int needCnt = 30;

        // redis 먼저 조회
        Optional<List<ChatMessageDTO>> optionalList = redisFirstDataBaseLater(userId, chatRoomId, lastMessageId);

        // redis 로 불러온 내역이 없는 경우
        if (optionalList.isEmpty()) {
            log.debug("===redis 로 불러온 내역이 없음====");

            // redis에 없는 경우 DB조회
            Optional<UserChatRoom> existUserChatRoom = isExistUserChatRoom(userId, chatRoomId);

            if (existUserChatRoom.isEmpty()) {
                log.debug("userChatRoom 존재하지않음");
                throw new BaseException(ChatErrorResponseCode.CHAT_ROOM_NOT_FOUND);
            }

            log.debug("userChatroom 존재함");
            UserChatRoom userChatRoom = existUserChatRoom.get();
            Long readIndex = userChatRoom.getReadIndex();
            log.debug("=== readIndex : {}", readIndex);


            // 20개씩 내역 페이징
            Pageable pageable = PageRequest.of(0, 30); // 첫 페이지, 최대 30개
            List<Messages> byChatRoomIdAndMessageIdLessThanOrderedByMessageIdDesc = messagesRepository.findByChatRoomIdAndMessageIdLessThanOrderedByMessageIdDesc(chatRoomId, readIndex, pageable);

            // 메세지가 아예 없는 경우
            if (byChatRoomIdAndMessageIdLessThanOrderedByMessageIdDesc.isEmpty()) {
                log.debug("채팅방에 아직 메세지 없음");
                throw new BaseException(ChatErrorResponseCode.CHAT_ROOM_NO_HISTORY_YET);
            }

            // 메세지가 있는 경우
            for (Messages msg : byChatRoomIdAndMessageIdLessThanOrderedByMessageIdDesc) {

                log.debug("db 에서 메세지 불러오기 성공");
                Optional<User> optionalUser = userRepository.findById(msg.getSenderId());
                String senderImgUrl = null;
                if (optionalUser.isPresent()) {
                    senderImgUrl = optionalUser.get().getImageUrl();
                }

                ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                        .messageId(msg.getId().toString())
                        .senderId(msg.getSenderId())
                        .roomId(msg.getChatRoom().getId().toString())
                        .content(msg.getContent())
                        .senderNickname(msg.getSenderNickname())
                        .timestamp(msg.getCreationTime())
                        .imgUrl(senderImgUrl)
                        .build();
                chatMessageDTOList.add(chatMessageDTO);

                // db에서 페이징하는 동시에 레디스에 내역 저장
                redisService.saveMessage(chatMessageDTO, 43200);
                log.debug("db페이징 후 레디스에 내역 저장");
            }
        }

        // redis 에서 가져온 내역이 있는 경우
        if (optionalList.isPresent()) {
            log.debug("Redis에서 가져온 내역이 있음");
            List<ChatMessageDTO> chatMessageDtos = optionalList.get();
            //마지막 메세지 아이디
            Long messageId = Long.valueOf(chatMessageDtos.get(chatMessageDtos.size() - 1).getMessageId());
            log.debug("레디스에서 가져온 마지막 메세지 아이디:{}", messageId);

            if (chatMessageDtos.size() < needCnt) {
                int more = needCnt - chatMessageDtos.size();
                // 20 개 - redis 로 가져온 내역 갯수 = 추가 내역 페이징
                Pageable pageable = PageRequest.of(0, more); // 첫 페이지, 최대 20개
                // redis 에서 가져온 마지막 아이디 보다 더 작은 메세지 추가로 가져오기
                List<Messages> messagesList = messagesRepository.findByChatRoomIdAndMessageIdLessThanOrderedByMessageIdDesc(chatRoomId, messageId, pageable);
                for (Messages m : messagesList) {
                    Optional<User> optionalUser = userRepository.findById(m.getSenderId());
                    String imgUrl = null;
                    if (optionalUser.isPresent()) {
                        imgUrl = optionalUser.get().getImageUrl();
                    }

                    ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                            .messageId(m.getId().toString())
                            .senderId(m.getSenderId())
                            .roomId(m.getChatRoom().getId().toString())
                            .content(m.getContent())
                            .senderNickname(m.getSenderNickname())
                            .timestamp(m.getCreationTime())
                            .imgUrl(imgUrl)
                            .build();
                    optionalList.get().add(chatMessageDTO);
                }
                // 중복되는 거 지운 후 반환
                Set<ChatMessageDTO> uniqueMessages = new LinkedHashSet<>(chatMessageDTOList);
                return uniqueMessages.isEmpty() ? Collections.emptyList() : new ArrayList<>(uniqueMessages);
            }
        }
        return optionalList.orElse(Collections.emptyList());
    }


    /**
     * 특정 방에 저장된 메세지 중 가장 마지막 메세지를 조회한다.
     *
     * @param chatRoomId 채팅방 아이디
     * @return Message
     */
    @Override
    public Messages getLastMessageFromChatRoom(Long chatRoomId) {

        List<Messages> allByChatRoom = messagesRepository.findAllByChatRoom(chatRoomId);
        return allByChatRoom.get(0);
    }

    /**
     * 사용자가 참여하고 있는 채팅방에 readIndex를 저장한다.
     *
     * @param userId     사용자 아이디
     * @param chatRoomId 채팅방 아이디
     * @param readIndex  사용자가 마지막으로 읽은 메세지 인덱스
     */
    @Override
    @Transactional
    public void saveReadIndex(Long userId, Long chatRoomId, Long readIndex) {

        Optional<UserChatRoom> matchingChatRoom = userChatRoomRepository.findByUserChatroom(userId, chatRoomId);

        // 해당하는 userchatRoom 의 readIndex 에 readIndex 를 업데이트
        if (matchingChatRoom.isPresent()) {
            UserChatRoom userChatRoom = matchingChatRoom.get();
            userChatRoom.updateReadIndex(readIndex);
        }
    }


    /**
     * 사용자 채팅방 정보를 삭제한다.
     *
     * @param userId     사용자 아이디
     * @param chatRoomId 채팅방 아이디
     * @return UserChatRoom
     */
    @Override
    public Optional<UserChatRoom> exitRoom(Long userId, Long chatRoomId) {
        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findByUserChatroom(userId, chatRoomId);

        // 사용자 채팅방 가져와서 softDelete = true 처리
        if (optionalUserChatRoom.isPresent()) {
            UserChatRoom userChatRoom = optionalUserChatRoom.get();
            userChatRoom.delete();
            return optionalUserChatRoom;
        }

        return Optional.empty();
    }


    /**
     * 채팅방에 최초 접속했는지 확인한다.
     *
     * @param userId 사용자 아이디
     * @param roomId 채팅방 아이디
     * @return Boolean
     */
    @Override
    public Optional<Boolean> isVisitedFirst(Long userId, Long roomId) {
        Optional<UserChatRoom> optionaluserchatRoom = userChatRoomRepository.findByUserChatroom(userId, roomId);
        // 해당 유저아이디와 해당 채팅방 아이디로 Userchatroom 가져오기
        if (optionaluserchatRoom.isPresent()) {
            UserChatRoom userChatRoom = optionaluserchatRoom.get();
            // 아직 방문하지 않았다면 == true
            if (userChatRoom.getIsFirst().equals(Boolean.TRUE)) {
                userChatRoom.updateIsFirst(Boolean.FALSE);
                return Optional.of(Boolean.TRUE);
            }
            // 이미 방문을 했다면
            return Optional.empty();
        }
        throw new BaseException(ChatErrorResponseCode.CHAT_ROOM_NOT_FOUND);
        // 존재하지 않는다면
    }


    /**
     * 사용자 채팅방에 채팅방을 추가한다.
     *
     * @param userId     사용자 아이디
     * @param chatRoomId 채팅방 아이디
     */
    public void createUserChatRoom(Long userId, Long chatRoomId) {

        // 유저 필드 가져오기
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new BaseException(ErrorResponseCode.CHAT_FAIL);
        }
        User user = optionalUser.get();


        // 농구장 채팅방 필드 가져오기
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        if (optionalChatRoom.isPresent()) {
            log.debug("채팅방 이름 : {}", optionalChatRoom.get().getName());

            // UserChatRoom 생성
            UserChatRoom userChatRoom = UserChatRoom.builder()
                    .chat(optionalChatRoom.get())
                    .user(user)
                    .name(optionalChatRoom.get().getName())
                    .BasketBallId(optionalChatRoom.get().getBasketBallId())
                    .roomType(RoomType.BASKETBALL)
                    .readIndex(0L)
                    .build();
            UserChatRoom saved = userChatRoomRepository.save(userChatRoom);
            log.debug("농구장 채팅방 userChatRoom 에 저장완료 : {}", saved);
        }
    }


    /**
     * redis 에서 메세지를 조회한다.
     *
     * @param userId     사용자 아이디
     * @param chatRoomId 채팅방 아이디
     * @param lastMessageId  기준 메세지 아이디
     * @return List(ChatMessageDTO)
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Optional<List<ChatMessageDTO>> redisFirstDataBaseLater(Long userId, Long chatRoomId,Long lastMessageId) {


        // UserChatRoom 에 존재하는 지 먼저 검증
        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findByUserChatroom(userId, chatRoomId);

        // userChatRoom에서 방의 존재가 확인이 안되는 경우 empty 로 반환
        if (optionalUserChatRoom.isEmpty()) {
            throw new BaseException(ChatErrorResponseCode.CHAT_ROOM_NOT_FOUND);
        }

        // redis에서 과거 내역 조회 30개씩
        List<ChatMessageDTO> messages = redisService.getMessages(chatRoomId, lastMessageId);

        // redis에서 가져온데이터가 없으면 empty return
        if (messages.isEmpty()) {
            log.debug("레디스로 가져온 데이터가 없습니다.");
            return Optional.empty();
        }

        // redis 로 가져온데이터 반환
        return Optional.of(messages);
    }

}
