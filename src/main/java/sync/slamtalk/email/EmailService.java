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
    ) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.addRecipients(Message.RecipientType.TO, email);
            message.setSubject("SlamTalk 인증 번호입니다.", "UTF-8");

            String imageLogoURI = "https://slamtalks3.s3.ap-northeast-2.amazonaws.com/1561891asdfmme1561.png";
            String text = "<!DOCTYPE html>\n" +
                    "<html lang=\"ko\">\n" +
                    "<head>\n" +
                    "<meta charset=\"UTF-8\" />\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "<title>슬램톡 | 이메일 인증</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div class=\"container\" style=\"color: #404040; min-width: 360px; max-width: 600px; margin-top: 50px; padding: 0 300px;\">\n" +
                    "<div class=\"content\">\n" +
                    "<img src=\"" + imageLogoURI + "\" alt=\"slam talk logo\" style=\"width: 128px; display: block; margin: 0 auto;\">\n" +
                    "<h1 style=\"color: black; text-align: center;\">이메일 인증을 진행해주세요</h1>\n" +
                    "<p style=\"text-align: center; color: black;\">안녕하세요. 슬램톡을 이용해주셔서 감사합니다 :)</p>\n" +
                    "<p style=\"text-align: center; color: black;\">슬램톡 회원가입을 위해 아래 인증코드를 화면에 입력해주세요.</p>\n" +
                    "</div>\n" +
                    "<div class=\"code\" style=\"border-radius: 6px; color: black; height: 120px; margin: 40px 0; background-color: ghostwhite; text-align: center; font-size: 30px;\"><b style=\"line-height: 120px;\">" + code + "</b></div>\n" +
                    "<p style=\"text-align: center; color: black;\">본 인증코드의 유효기간은 5 분입니다. 시간이 지나면 인증코드 재발급을 해주세요.</p>\n" +
                    "<div style=\"width: 100%\">\n" +
                    "<div>\n" +
                    "<a href=\"https://www.slam-talk.site/signup\" class=\"homepage-btn\" style=\"background-color: #ff634a; color: white; text-align: center; font-size: medium; text-decoration: none; border-radius: 6px; display: block; margin: 30px 0; padding: 10px\">\n" +
                    "홈페이지로 이동하기\n" +
                    "</a>\n" +
                    "</div>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";

            message.setText(text, "UTF-8", "html");
            message.setFrom("slamtalk@naver.com"); //보내는사람.
            return message;
        } catch (Exception e){
            log.error("메일 전송 실패 : = {}", e.toString());
            throw new BaseException(EmailErrorResponseCode.MAIL_FAIL);
        }
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
    ) {
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
     * 이메일 검증하는 메서드
     * - 레디스에서 <key,value> 로 저장된 <code, email> 을 추적하여 이메일 검증을 완료한다
     *
     * @param email : 검증하고자 하는 이메일
     * @param code  : 검증 코드
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
