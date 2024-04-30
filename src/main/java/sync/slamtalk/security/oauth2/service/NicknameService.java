package sync.slamtalk.security.oauth2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sync.slamtalk.user.UserRepository;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class NicknameService {
    private final UserRepository userRepository;

    /**
     * 소셜 닉네임을 기반으로 사용 가능한 닉네임을 생성합니다.
     *
     * 이 메서드는 주어진 소셜 닉네임에서 특수문자를 제거하고 소문자로 변환합니다. 만일 변환된 닉네임이 13자 이상이거나
     * 이미 데이터베이스에 동일한 닉네임이 존재한다면, "익명"에 랜덤 숫자를 추가한 새로운 닉네임을 생성합니다.
     * 그렇지 않다면, 변환된 닉네임을 그대로 사용합니다.
     *
     * @param socialNickname 소셜 닉네임
     * @return String 사용 가능한 닉네임
     */
    public String createAvailableNickname(String socialNickname) {
        String processedNickname = removeSpecialCharactersAndConvertToLowercase(socialNickname);

        if (processedNickname.length() >= 13 || isNicknameAlreadyExists(processedNickname)) {
            return generateAnonymousNickname(1); // "익명" + 랜덤 숫자 생성
        }

        return processedNickname; // 조건에 부합하면 처리된 닉네임 반환
    }


    /**
     * 재귀적으로 "익명" + 랜덤 숫자를 조합하여 고유한 닉네임을 생성합니다.
     *
     * @param depth 재귀 호출의 깊이
     * @return String 생성된 고유 닉네임
     */
    private String generateAnonymousNickname(int depth) {
        int length = (depth == 1) ? 4 : (depth == 2) ? 8 : 11; // 재귀 단계에 따른 랜덤 숫자 길이
        String randomDigits = generateRandomDigits(length);
        String newNickname = "익명" + randomDigits;

        if (isNicknameAlreadyExists(newNickname)) {
            return generateAnonymousNickname(depth + 1); // 재귀 호출 시 깊이 증가
        } else {
            return newNickname;
        }
    }

    /**
     * 주어진 길이에 따라 랜덤 숫자 문자열을 생성합니다.
     *
     * @param length 생성할 랜덤 숫자 문자열의 길이
     * @return String 생성된 랜덤 숫자 문자열
     */
    private String generateRandomDigits(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 0에서 9 사이의 랜덤한 숫자 추가
        }

        return sb.toString();
    }

    /**
     * 문자열에서 특수 문자를 제거하고 영문자를 소문자로 변환합니다.
     *
     * @param str 처리할 문자열
     * @return String 특수 문자가 제거되고 소문자로 변환된 문자열
     */
    private String removeSpecialCharactersAndConvertToLowercase(String str) {
        String noSpecialChars = str.replaceAll("[^a-zA-Z0-9ㄱ-ㅎ가-힣]", "");
        return noSpecialChars.toLowerCase();
    }

    /**
     * 주어진 닉네임이 이미 존재하는지 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return boolean 닉네임의 존재 여부
     */
    private boolean isNicknameAlreadyExists(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }
}
