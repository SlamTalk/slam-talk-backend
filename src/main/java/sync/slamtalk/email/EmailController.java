package sync.slamtalk.email;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.email.dto.PostMailreq;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;


    //인증메일 발송 연습 api
    @ResponseBody
    @PostMapping("/send-mail")
    @Operation(
            summary = "인증 코드 보내기",
            description = "사용자가 보낸 이메일로 인증 코드를 발송한다.",
            tags = {"이메일 인증"}
    )
    public ApiResponse<String> mailAuthentication(@Valid @RequestBody PostMailreq postMailreq) {
        emailService.sendCertificationMail(postMailreq.getEmail());
        return ApiResponse.ok();
    }

    @GetMapping("/mail-check")
    @Operation(
            summary = "인증 코드 검사",
            description = "사용자가 보낸 인증코드가 이메일에 일치하는지 검증한다.",
            tags = {"이메일 인증"}
    )
    public ApiResponse<String> mailCheck(
            @RequestParam String email,
            @RequestParam String code
    ) {
        //email 인증 코드 확인

        boolean authCode = emailService.verifyEmailCode(email, code);
        if (!authCode) {
            throw new BaseException(EmailErrorResponseCode.POST_USERS_INVALID_CODE);
        }
        return ApiResponse.ok();
    }
}
