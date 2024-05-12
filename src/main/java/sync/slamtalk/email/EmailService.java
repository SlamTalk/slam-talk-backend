package sync.slamtalk.email;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sync.slamtalk.chat.redis.RedisService;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.error.UserErrorResponseCode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisService redisService; //redis 관련
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;
    private static final String PASSWORD_RESET_TEMPLATE = "password-reset-template";
    private static final String REGISTER_EMAIL_CERTIFICATE_TEMPLATE = "register-email-verification-template";
    private static final String EMAIL_ENCODING = "UTF-8";
    private static final String HTML_CONTENT_TYPE = "html";
    private static final String EMAIL_SENDER = "slamtalk@naver.com";

    /**
     * 이메일을 통한 사용자 인증을 위한 검증 코드를 생성 및 전송하는 메서드입니다.
     * 이 메서드는 UUID를 사용하여 랜덤한 6자리의 인증 코드를 생성하고, 이를 사용자의 이메일로 전송합니다.
     * 또한, 생성된 인증 코드는 레디스를 통해 이메일 주소와 함께 24시간동안 저장되어, 추후 인증 절차에서 사용됩니다.
     *
     * @param email 전송하고자 하는 이메일 주소입니다. 이메일 형식이 유효해야 하며, 사용자가 접근 가능한 이메일이어야 합니다.
     *              해당 이메일 주소로 인증 코드가 포함된 이메일이 전송됩니다.
     * @return 없음. 이 메서드는 반환값이 없습니다. 인증 코드의 생성 및 이메일 전송 과정에서 발생할 수 있는
     * 예외는 내부적으로 처리되거나, 호출하는 곳에서 처리해야 합니다.
     */
    public void sendEmailVerificationMail(String email) {
        String code = UUID.randomUUID().toString().substring(0, 6); //랜덤 인증번호 uuid를 이용!

        checkEmailExistence(email);

        // 인증코드 이메일 전송하기
        sendEmailForTheTemplate(
                REGISTER_EMAIL_CERTIFICATE_TEMPLATE,
                email,
                "SlamTalk 인증 번호입니다",
                code
        );

        // 레디스에 해당 인증코드 저장하기
        redisService.saveEmailVerificationCode(
                email,
                code
        );
    }


    /**
     * 이메일 검증하는 메서드
     * - 레디스에서 <key,value> 로 저장된 <email, code> 을 추적하여 이메일 검증을 완료한다.
     * - 검증 코드가 일치하면 이메일 인증이 성공적으로 완료되며, 해당 이메일은 60분 동안 '인증됨' 상태가 된다.
     * - 인증 코드가 일치하지 않거나, 해당 이메일에 대한 인증 코드가 존재하지 않는 경우 예외가 발생한다.
     *
     * @param email 검증하고자 하는 이메일 주소
     * @param code  이메일에 대해 발급된 검증 코드
     * @throws BaseException 인증 코드가 존재하지 않거나, 인증 코드가 일치하지 않을 경우 예외를 발생시킨다.
     */
    public void authenticationCodeCheck(
            String email,
            String code
    ) {
        String codeFoundByEmail = redisService.getEmailVerificationCodeValue(
                email,
                code
        );
        // 인증번호를 받은적 없는 이메일일 경우
        if (codeFoundByEmail == null) {
            throw new BaseException(EmailErrorResponseCode.AUTHENTICATION_CODE_DOES_NOT_EXIST);
        }

        // 인증번호가 일치하지 않을 경우
        if (!codeFoundByEmail.equals(code)) {
            throw new BaseException(EmailErrorResponseCode.POST_USERS_INVALID_CODE);
        }

        // 인증 완료된 이메일 redis에 1시간 가량 저장.
        redisService.saveVerificationCompletionEmail(email);

        // 인증 후 인증코드 삭제
        redisService.deleteEmailVerificationCodeValue(email);
    }

    /**
     * 사용자에게 임시 비밀번호를 이메일로 전송합니다.
     * 이 메서드는 사용자가 비밀번호를 잊어버렸을 때 임시 비밀번호를 제공하기 위해 사용됩니다.
     * 임시 비밀번호가 포함된 이메일 템플릿을 사용하여 이메일을 전송합니다.
     *
     * @param email             사용자의 이메일 주소. 이 주소로 임시 비밀번호가 전송됩니다.
     * @param temporaryPassword 사용자에게 제공될 임시 비밀번호.
     *                          이 비밀번호는 사용자가 다음 번 로그인할 때 사용해야 합니다.
     */
    public void sendTemporaryPasswordViaEmail(
            String email,
            String temporaryPassword
    ) {
        sendEmailForTheTemplate(
                PASSWORD_RESET_TEMPLATE,
                email,
                "SlamTalk 임시 비밀번호 입니다",
                temporaryPassword);
    }

    /**
     * 회원가입 시 중복 이메일이 존재하는지 검사하는 메서드
     */
    private void checkEmailExistence(String email) {
        if (userRepository.findByEmailAndSocialType(email, SocialType.LOCAL).isPresent()) {
            log.debug("이미 존재하는 유저 이메일입니다.");
            throw new BaseException(UserErrorResponseCode.EMAIL_ALREADY_EXISTS);
        }
    }

    /**
     * 템플릿에 기반한 이메일을 전송하는 메서드
     * - 지정된 템플릿을 사용하여 이메일을 구성하고 전송한다.
     * - 이메일 전송 중 발생할 수 있는 예외를 처리한다.
     *
     * @param template 사용할 이메일 템플릿의 이름
     * @param email    수신자의 이메일 주소
     * @param subject  이메일의 제목
     * @param code     이메일 템플릿 내에서 사용될 코드
     * @throws BaseException 메일 전송 실패 시 발생하는 예외
     */
    private void sendEmailForTheTemplate(
            String template,
            String email,
            String subject,
            String code
    ) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("code", code);

            MimeMessage mimeMessage = createAMimeMessageEmailTransport(
                    email,
                    subject,
                    template,
                    variables
            );
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            log.error("메일 전송 실패 : {}", e.toString());
            throw new BaseException(EmailErrorResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * MimeMessage 객체를 생성하여 이메일 전송을 준비하는 로직입니다. 특정 템플릿과 변수를 사용하여 이메일 내용을 구성하고, 주어진 이메일 주소로 메일을 전송할 준비를 합니다.
     *
     * @param email        전송할 이메일 주소입니다.
     * @param subject      메일의 제목입니다.
     * @param templateName 사용할 메일 템플릿의 이름입니다.
     * @param variables    메일 템플릿에 적용할 변수들을 Map 형태로 받습니다.
     * @return 생성된 MimeMessage 객체를 반환합니다. 메일 전송에 실패할 경우, BaseException을 발생시킵니다.
     */
    private MimeMessage createAMimeMessageEmailTransport(
            String email,
            String subject,
            String templateName,
            Map<String, Object> variables
    ) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(Message.RecipientType.TO, email);
            message.setSubject(subject, EMAIL_ENCODING);

            String text = processTemplate(templateName, variables);
            message.setText(text, EMAIL_ENCODING, HTML_CONTENT_TYPE);
            message.setFrom(EMAIL_SENDER);

            return message;
        } catch (Exception e) {
            log.error("메일 전송 실패: {}", e.toString());
            throw new BaseException(EmailErrorResponseCode.MAIL_FAIL);
        }
    }

    /**
     * 주어진 템플릿 이름과 변수들을 사용하여 템플릿 처리를 수행하는 메서드입니다.
     * 이 메서드는 템플릿 엔진을 사용하여, 템플릿에 정의된 변수들을 주어진 변수 값들로 치환한 후,
     * 최종적으로 처리된 문자열을 반환합니다.
     *
     * @param templateName 처리할 템플릿의 이름입니다. 템플릿 파일의 식별자로 사용됩니다.
     * @param variables    템플릿 내에서 사용될 변수들의 이름과 값을 매핑한 Map 객체입니다.
     *                     각 키는 템플릿 내의 변수 이름에 해당하며, 각 값은 해당 변수에 치환될 실제 값입니다.
     * @return 처리된 템플릿의 최종 문자열을 반환합니다. 변수가 치환된 후의 템플릿 내용이 담긴 문자열입니다.
     */
    private String processTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        return templateEngine.process(templateName, context);
    }

}
