package sync.slamtalk.mate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.mate.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

}
