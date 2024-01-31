package sync.slamtalk.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.user.entity.UserBasketballPositionType;
import sync.slamtalk.user.entity.UserBasketballSkillLevelType;
import sync.slamtalk.user.error.UserErrorResponseCode;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePositionAndSkillRequestDto {
    @NotNull
    private String basketballSkillLevel;
    @NotNull
    private String basketballPosition;

    public UserBasketballSkillLevelType convertStringToSkillLevel() {
        for (UserBasketballSkillLevelType level : UserBasketballSkillLevelType.values()) {
            if (level.getLevel().equals(this.basketballSkillLevel)) {
                return level;
            }
        }
        throw new BaseException(UserErrorResponseCode.ENUM_TYPE_NOT_FOUND);
    }

    public UserBasketballPositionType convertStringToPosition() {
        for (UserBasketballPositionType positionType : UserBasketballPositionType.values()) {
            if (positionType.getPosition().equals(this.basketballPosition)) {
                return positionType;
            }
        }
        throw new BaseException(UserErrorResponseCode.ENUM_TYPE_NOT_FOUND);
    }
}
