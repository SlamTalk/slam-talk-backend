package sync.slamtalk.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sync.slamtalk.user.entity.UserBasketballPositionType;
import sync.slamtalk.user.entity.UserBasketballSkillLevelType;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePositionAndSkillReq {
    @NotNull
    private UserBasketballSkillLevelType basketballSkillLevel;
    @NotNull
    private UserBasketballPositionType basketballPosition;
}
