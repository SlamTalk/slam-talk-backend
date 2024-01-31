package sync.slamtalk.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.user.entity.UserRole;

@Getter
@AllArgsConstructor
public class UserLoginResponseDto {
    private Long userId;
    private String nickname;
    private String imageUrl;
    private Boolean firstLoginCheck;
}
