package sync.slamtalk.mate.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkillLevelType {
    HIGH("고수"), MIDDLE("중수"), LOW("초보"), BEGINNER("입문");

    private final String level;

    public static SkillLevelType fromLevel(String description) {
        for (SkillLevelType level : values()) {
            if (level.getLevel().equals(description)) {
                return level;
            }
        }
        throw new IllegalArgumentException("일치하는 레벨이 없습니다 " + description + " found");
    }
}
