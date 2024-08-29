package example.springsecuritytistory.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @EntityGraph(attributePaths = {"roles"})
    Optional<UserEntity> findByUsername(String username);
}
