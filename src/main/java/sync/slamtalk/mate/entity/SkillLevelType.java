package sync.slamtalk.mate.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkillLevelType {
    HIGH("고수"), MIDDLE("중수"), LOW("하수"), BEGINNER("입문");

    private final String level;
}
