package sync.slamtalk.mate.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillLevelList {
    private boolean skillLevelHigh;
    private boolean skillLevelMiddle;
    private boolean skillLevelLow;
    private boolean skillLevelBeginner;
}
