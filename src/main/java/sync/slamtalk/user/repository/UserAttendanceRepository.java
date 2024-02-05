package sync.slamtalk.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.entity.UserAttendance;

import java.time.LocalDate;
import java.util.Optional;

public interface UserAttendanceRepository extends JpaRepository<UserAttendance, Long> {
    Boolean existsByUserAndAttDate(User user, LocalDate attDate);

    Optional<Long> countUserAttendancesByUser(User user);
}
