package example.springsecuritytistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInformationRepository extends JpaRepository<UserInformationEntity, Long> {
}
