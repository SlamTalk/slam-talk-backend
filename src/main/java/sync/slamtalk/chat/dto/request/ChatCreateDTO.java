package sync.slamtalk.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값이 아닌 필드만 포함
public class ChatCreateDTO implements Serializable {
    private List<Long> participants; // 참여자 아이디 , 알림을 주어야 하니
    private String roomType; // 1:1 이냐 단체방 이냐
    @JsonProperty("basket_ball_id")
    private Long basketBallId; // 농구장 아이디
    @JsonProperty("together_id")
    private Long togetherId; // 같이 하기 글 아이디
    @JsonProperty("teamMatching_id")
    private Long teamMatchingId; // 팀매칭 글 아이디
    private String name; // 채팅방 이름
}
