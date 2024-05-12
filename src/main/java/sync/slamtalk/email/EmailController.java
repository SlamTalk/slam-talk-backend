package sync.slamtalk.email;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.email.dto.PostMailReq;

@Tag(name = "이메일 인증")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;


    //인증메일 발송 연습 api
    @PostMapping("/send-mail")
    @Operation(
            summary = "인증 코드 보내기",
            description = "사용자가 보낸 이메일로 인증 코드를 발송한다."
    )
    public ApiResponse<Void> sendEmailVerificationMail(@Valid @RequestBody PostMailReq postMailreq) {
        emailService.sendEmailVerificationMail(postMailreq.getEmail());
        return ApiResponse.ok();
    }

    @GetMapping("/mail-check")
    @Operation(
            summary = "인증 코드 검사",
            description = "사용자가 보낸 인증코드가 이메일에 일치하는지 검증한다."
    )
    public ApiResponse<Void> authenticationCodeCheck(
            @RequestParam String email,
            @RequestParam String code
    ) {
        emailService.authenticationCodeCheck(email, code);
        return ApiResponse.ok();
    }
}
