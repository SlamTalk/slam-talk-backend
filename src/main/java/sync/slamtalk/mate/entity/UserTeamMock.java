package sync.slamtalk.mate.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;


@Getter
public class UserTeamMock {   // 다른 엔터티의 생성을 위한 임시적인 목업 엔터티

    @Id
    @GeneratedValue
    @Column(name = "User_id")
    private long userId;

    private String userNickname;


    @OneToMany(mappedBy = "userId")
    private List<MatePost> matePostList;

    public UserTeamMock() {
    }

    public UserTeamMock(String name) {
        this.userNickname = name;
    }
}
