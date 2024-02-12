package sync.slamtalk.community.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.user.entity.User;

@Entity
@Table(name = "community")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Community extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id", nullable = false)
    @Id
    private Long communityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunityCategory category;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityImage> images; // 게시글과 연관된 이미지 리스트

    // 제목 수정
    public void editTitle(String title) {
        this.title = title;
    }

    // 내용 수정
    public void editContent(String content) {
        this.content = content;
    }

    // 카테고리 수정
    public void editCategory(CommunityCategory category) {
        this.category = category;
    }

    // 이미지 리스트 등록
    public void updateImages(List<CommunityImage> images) {
        this.images = images;
        images.forEach(image -> image.updateCommunity(this)); // 이미지 목록에 존재하는 이미지들에 현재 게시글을 설정
    }

    // 이미지 리스트 편집 , 새로운 이미지들을 기존 리스트에 추가
    public void editImages(List<CommunityImage> images) {

        for (CommunityImage image : images) {
            if (!this.images.contains(image)) { // 현재 리스트에 이미지가 존재하는지 중복 체크
                this.images.add(image);
                image.updateCommunity(this); // 이미지에 현재 게시글 설정
            }
        }
    }
}
