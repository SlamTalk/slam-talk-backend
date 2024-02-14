package sync.slamtalk.email;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sync.slamtalk.chat.redis.RedisService;
import sync.slamtalk.common.BaseException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisService redisService; //redis 관련

    /**
     * 이메일 메세지 생성하는 메서드
     *
     * @param code  : 이메일 인증 코드
     * @param email : 전송하고자하는 이메일 주소
     */
    private MimeMessage createMessage(
            String code,
            String email
    ) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("SlamTalk 인증 번호입니다.");
        message.setText("[이메일 인증코드]\n" + code);
        message.setFrom("slamtalk@naver.com"); //보내는사람.

        return message;
    }

    /**
     * 이메일 전송하는 메서드
     *
     * @param code  : 이메일 인증 코드
     * @param email : 전송하고자 하는 이메일 주소
     */
    public void sendMail(
            String code,
            String email
    ) throws Exception {
        try {
            MimeMessage mimeMessage = createMessage(code, email);
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            log.error("메일 전송 실패 : = {}", e.toString());
            throw new BaseException(EmailErrorResponseCode.DATABASE_ERROR);
        }
    }

    /**
     * 검증 코드 생성및 전송하는 메서드
     * - 레디스를 통하여 5분간 <code, email> 을 저장하여 이메일 검증을 준비한다.
     *
     * @param email : 전송하고자 하는 이메일 주소
     */
    public void sendCertificationMail(String email) {
        try {
            String code = UUID.randomUUID().toString().substring(0, 6); //랜덤 인증번호 uuid를 이용!
            sendMail(code, email);
            redisService.setDataExpire(code, email, 60 * 5L); // {key,value} 5분동안 저장.
        } catch (Exception e) {
            log.error("redis 서버에서 오류 발생 : = {}", e.toString());
            throw new BaseException(EmailErrorResponseCode.DATABASE_ERROR);
        }
    }

    /**
     *  이메일 검증하는 메서드
     *  - 레디스에서 <key,value> 로 저장된 <code, email> 을 추적하여 이메일 검증을 완료한다
     * @param email : 검증하고자 하는 이메일
     * @param code : 검증 코드
     */
    public boolean verifyEmailCode(
            String email,
            String code
    ) {
        String codeFoundByEmail = redisService.getData(code);
        if (codeFoundByEmail == null) {
            return false;
        }
        redisService.setDataExpire(email, "OK", 60 * 5L); // 인증 완료 되었을 경우, 5분 동안 유효한 상태
        boolean isEquals = redisService.getData(code).equals(email);
        redisService.deleteData(code);

        return isEquals;
    }
}
