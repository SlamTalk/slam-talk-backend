package sync.slamtalk.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponseDto {
    private Long userId;
    private String nickname;
    private String imageUrl;
    private Boolean firstLoginCheck;
}
