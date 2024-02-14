package sync.slamtalk.community.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.community.entity.Community;
import sync.slamtalk.community.entity.CommunityCategory;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    List<Community> findByCategoryAndIsDeletedFalse(CommunityCategory category);
    List<Community> findByIsDeletedFalse();
    Optional<Community> findByCommunityIdAndIsDeletedFalse(Long communityId);
}
