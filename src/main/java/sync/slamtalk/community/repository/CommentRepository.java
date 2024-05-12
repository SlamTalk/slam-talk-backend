package sync.slamtalk.community.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.community.entity.Comment;
import sync.slamtalk.community.entity.Community;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByCommunity_communityIdAndIsDeletedFalse(Long communityId);
    Optional<Comment> findByCommentIdAndIsDeletedFalse(Long commentId);

    Long countByCommunityAndIsDeletedFalse(Community community);

}
