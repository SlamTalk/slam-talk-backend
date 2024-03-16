package sync.slamtalk.user.dto.response;

import lombok.Getter;
import sync.slamtalk.user.entity.User;

@Getter
public class UserSimpleResponseDto {

    private Long id;
    private String nickname;
    private String imageUrl;

    public static UserSimpleResponseDto from(User user) {
        UserSimpleResponseDto userSimpleResponseDto = new UserSimpleResponseDto();
        userSimpleResponseDto.id = user.getId();
        userSimpleResponseDto.nickname = user.getNickname();
        userSimpleResponseDto.imageUrl = user.getImageUrl();
        return userSimpleResponseDto;
    }
}
