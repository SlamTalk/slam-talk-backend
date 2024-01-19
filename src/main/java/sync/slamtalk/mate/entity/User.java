package sync.slamtalk.mate.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;


@Getter
public class User {   // 다른 엔터티의 생성을 위한 임시적인 목업 엔터티

    @Id
    @GeneratedValue
    @Column(name = "User_id")
    private long userId;

    private String userNickname;

    private String userLocation;

    @OneToMany(mappedBy = "userId")
    private List<MatePost> matePostList;

    public User() {
    }

    public User(String name, String location) {
        this.userNickname = name;
        this.userLocation = location;
    }
}
