package sync.slamtalk.mate.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Member {   // 다른 엔터티의 생성을 위한 임시적인 목업 엔터티

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private long id;


    private String name;

    @OneToMany(mappedBy = "member")
    private List<Participant> participants;

    public Member() {
    }

    public Member(String name) {
        this.name = name;
    }
}
