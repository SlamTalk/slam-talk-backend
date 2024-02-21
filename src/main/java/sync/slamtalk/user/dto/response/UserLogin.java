package sync.slamtalk.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLogin {
    private Long userId;
    private String nickname;
    private String imageUrl;
    private Boolean firstLoginCheck;
}
