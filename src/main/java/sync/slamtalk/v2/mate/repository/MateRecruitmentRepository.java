package sync.slamtalk.v2.mate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sync.slamtalk.v2.mate.model.MateRecruitment;

import java.util.Optional;

public interface MateRecruitmentRepository extends JpaRepository<MateRecruitment, Long> {

    /**
     * id로 메이트 모집을 찾아 반환합니다.
     * soft-delete의 대상이 아닌 메이트 모집만 반환합니다.
     *
     * @param mateRecruitmentId 메이트 모집의 id
     * @return id로 찾은 메이트 모집
     */
    @Query("select m from MateRecruitment m where m.id = :mateRecruitmentId and m.isDeleted = false")
    Optional<MateRecruitment> findActiveOneById(Long mateRecruitmentId);
}
