package sync.slamtalk.mate.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 포지션 타입
 * 포지션은 포워드, 가드, 센터, 전체(무관)로 구성
 * 추후 협의를 통해 하나의 ENUM으로 통합할 수 있음
 */
@Getter
@RequiredArgsConstructor
public enum PositionType {
    FORWARD("포워드"), GUARD("가드"), CENTER("센터"), UNSPECIFIED("무관");

    private final String position;
}
