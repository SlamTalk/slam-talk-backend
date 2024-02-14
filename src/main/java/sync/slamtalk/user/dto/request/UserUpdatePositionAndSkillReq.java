package sync.slamtalk.user.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private UserBasketballSkillLevelType basketballSkillLevel;
    @NotNull
    @Enumerated(EnumType.STRING)
    private UserBasketballPositionType basketballPosition;
}
