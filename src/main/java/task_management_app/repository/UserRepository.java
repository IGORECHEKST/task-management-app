package task_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import task_management_app.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
