package sync.slamtalk.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 유저 정보에 표시될 유저의 포지션
 */
@Getter
@RequiredArgsConstructor
public enum UserBasketballPositionType {
    GUARD("가드"), FORWARD("포워드"), CENTER("센터"), UNSPECIFIED("무관"), UNDEFINED("미정");

    private final String position;

}
