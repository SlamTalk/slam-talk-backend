package sync.slamtalk.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserChangePasswordReq {
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>\\/?|\\\\])[A-Za-z0-9~!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>\\/?|\\\\]{8,16}$",
            message = "비밀번호는 영문자, 숫자, 특수문자를 각각 최소 하나 이상 포함하며, 8자 이상 16자 이하이어야 합니다.")
    private String password;
}
