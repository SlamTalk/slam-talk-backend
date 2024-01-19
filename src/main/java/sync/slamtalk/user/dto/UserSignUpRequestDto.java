package sync.slamtalk.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 시 받을 dto
 * */
@NoArgsConstructor
@Getter
public class UserSignUpRequestDto {

    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String nickname;
}
