package sync.slamtalk.chat.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import sync.slamtalk.chat.entity.RoomType;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.repository.ChatRoomRepository;
import sync.slamtalk.chat.repository.UserChatRoomRepository;

import java.util.List;

@Getter
@AllArgsConstructor
@Service
public class MessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    static final String path  = "https://www.slam-talk.site/chatting";


    /**
     * 채팅방 생성 메세지
     * @param chatRoomId
     * @return
     */
    public String createChatRoom(Long chatRoomId){
        return chatRoomRepository.findById(chatRoomId).map(chatRoom -> {
            if(chatRoom.getRoomType().equals(RoomType.DIRECT)){
                return "1:1 채팅방이 생성되었습니다.";
            }else{
                return chatRoom.getName() + "에서 채팅방이 생성되었습니다.";
            }
        }).orElse("채팅방 없음");
    }


    /**
     * 새로운 메세지 메세지
     * @param chatRoomId
     * @param userId
     * @return
     */

    public String newMessage(Long chatRoomId,Long userId){
        return chatRoomRepository.findById(chatRoomId).map(chatRoom -> {
            if(chatRoom.getRoomType().equals(RoomType.DIRECT)){
                List<UserChatRoom> userChatRooms = userChatRoomRepository.findByChat_Id(chatRoomId);

                for(UserChatRoom u: userChatRooms){
                    if(u.getUser().getId().equals(userId)){
                        return u.getUser().getNickname() + "님과 대화에서 새로운 메세지가 도착했습니다.";
                    }
                }
            }
            return chatRoom.getName() + "에서 새로운 메세지가 도착했습니다.";
        }).orElse("채팅방 없음");
    }

    public String getPath(Long chatRoomId){
        return  path;
    }

}
