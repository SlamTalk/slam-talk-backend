package sync.slamtalk.user.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.user.error.UserErrorResponseCode;

import java.security.SecureRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordGenerator {
    public static final String UPPER_CASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String NUMBERS = "0123456789";
    public static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+";

    public static String generatePassword(int length) {
        if (length < 8 || length > 16) {
            throw new BaseException(UserErrorResponseCode.FAILED_TO_CREATE_TEMPORARY_PASSWORD_ISSUANCE);
        }

        String combinedChars = UPPER_CASE_LETTERS + LOWER_CASE_LETTERS + NUMBERS + SPECIAL_CHARACTERS;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        // 비밀번호에 각 유형의 문자가 하나 이상 포함하기(소문자, 대문자, 특수문자, 숫자)
        password.append(UPPER_CASE_LETTERS.charAt(random.nextInt(UPPER_CASE_LETTERS.length())));
        password.append(LOWER_CASE_LETTERS.charAt(random.nextInt(LOWER_CASE_LETTERS.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // 나머지 비밀번호 길이를 모든 유형의 임의 문자로 채웁니다.
        for (int i = 4; i < length; i++) {
            password.append(combinedChars.charAt(random.nextInt(combinedChars.length())));
        }

        // 예측 가능한 패턴을 피하기 위해 생성된 비밀번호를 섞습니다.
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }
}
