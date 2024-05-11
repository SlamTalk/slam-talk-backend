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

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisService redisService; //redis 관련
    private final TemplateEngine templateEngine;
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

        // 인증코드 이메일 전송하기
        sendEmailForTheTemplate(
                REGISTER_EMAIL_CERTIFICATE_TEMPLATE,
                email,
                "SlamTalk 인증 번호입니다",
                code
        );

        // 레디스에 해당 인증코드 저장하기
        saveVerificationCodeToRedis(email, code);
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
        String key = generateEmailVerificationKey(email);
        String codeFoundByEmail = redisService.getData(key);
        // 인증번호를 받은적 없는 이메일일 경우
        if (codeFoundByEmail == null) {
            throw new BaseException(EmailErrorResponseCode.AUTHENTICATION_CODE_DOES_NOT_EXIST);
        }

        // 인증번호가 일치하지 않을 경우
        if (!codeFoundByEmail.equals(code)) {
            throw new BaseException(EmailErrorResponseCode.POST_USERS_INVALID_CODE);
        }

        redisService.setDataExpire(generateAuthenticatedEmailKey(email), "OK", 60 * 60L); // 인증 완료 되었을 경우, 60분 동안 회원가입 가능

        // 인증 후 인증코드 삭제
        redisService.deleteData(key);
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

    /**
     * 이메일 인증을 위한 레디스 전용 키를 생성합니다. 생성된 키는 'authentication:email:{이메일주소}' 형식을 가집니다.
     *
     * @param email 사용자의 이메일 주소
     * @return 생성된 이메일 인증 키를 문자열로 반환합니다.
     */
    private static String generateEmailVerificationKey(String email) {
        // key 생성
        StringJoiner sj = new StringJoiner(":");
        sj.add("authentication");
        sj.add("email");
        sj.add(email);
        return sj.toString();
    }


    /**
     * 레디스를 위한 이메일 인증 키를 생성하는 메서드입니다. 생성된 키는 'email:{사용자이메일}' 형식을 따릅니다.
     *
     * @param email 사용자의 이메일 주소입니다.
     * @return 생성된 이메일 인증 키를 문자열로 반환합니다.
     */
    private static String generateAuthenticatedEmailKey(String email) {
        // key 생성
        StringJoiner sj = new StringJoiner(":");
        sj.add("email");
        sj.add(email);
        return sj.toString();
    }

    /**
     * 이메일 인증 코드를 Redis에 저장합니다.
     * 이메일 주소와 인증 코드를 사용하여 Redis에 특정 키를 생성하고, 이 키를 통해 인증 정보를 저장합니다.
     *
     * @param email 인증 코드가 발급된 사용자의 이메일 주소입니다. 이 주소는 저장할 데이터의 키 생성에 사용됩니다.
     * @param code  사용자에게 발급된 인증 코드입니다. 이 코드는 키 생성에 사용되며, 검증 시 해당 코드의 유효성을 검사하는 데 사용됩니다.
     */
    private void saveVerificationCodeToRedis(String email, String code) {
        String key = generateEmailVerificationKey(email);

        savingRedisData(key, code);
    }

    /**
     * Redis에 데이터를 저장하는 메서드
     * - 지정된 키(key)와 값(value)을 받아 Redis에 저장한다. 해당 데이터는 24시간 동안 유지된다.
     * - Redis 서비스를 통해 데이터 저장 시, 만료 시간을 설정하여 데이터가 자동으로 만료되도록 한다.
     * - 데이터 저장 중 예외 발생 시, 로그를 기록하고 예외를 발생시킨다.
     *
     * @param key   저장할 데이터의 키
     * @param value 저장할 데이터의 값
     * @throws BaseException Redis 서버 오류 발생 시 예외를 발생시킨다.
     */
    private void savingRedisData(String key, String value) {
        try {
            redisService.setDataExpire(key, value, 60 * 60 * 24L); // {key,value} 24시간 동안 저장.
        } catch (Exception e) {
            log.error("redis 서버에서 오류 발생 : = {}", e.toString());
            throw new BaseException(EmailErrorResponseCode.DATABASE_ERROR);
        }
    }

}
