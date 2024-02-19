package sync.slamtalk.community.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.community.entity.Community;
import sync.slamtalk.community.entity.CommunityCategory;
import sync.slamtalk.user.entity.User;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    List<Community> findByCategoryAndIsDeletedFalse(CommunityCategory category);
    List<Community> findByIsDeletedFalse();
    Optional<Community> findByCommunityIdAndIsDeletedFalse(Long communityId);

    /* 유저가 쓴 커뮤니티의 게시글 개수 조회*/
    Long countAllByUserAndIsDeletedFalse(User user);
}
