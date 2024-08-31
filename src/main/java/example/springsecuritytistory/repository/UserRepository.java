package example.springsecuritytistory.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @EntityGraph(attributePaths = {"roles"})
    Optional<UserEntity> findByUsername(String username);

    @Query("select id from users where username=:username")
    Long getIdByUsername(String username);
}
