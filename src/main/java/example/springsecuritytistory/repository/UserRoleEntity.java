package example.springsecuritytistory.repository;

import example.springsecuritytistory.security.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "user_roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoleEntity extends BaseEntity{
    @Id @GeneratedValue
    private Long id;
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    UserRoleEntity(UserEntity user, UserRole role) {
        this.user = user;
        this.role = role;
    }
}
