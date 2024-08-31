package example.springsecuritytistory.repository;

import static example.springsecuritytistory.security.UserRole.USER;

import example.springsecuritytistory.security.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<UserRoleEntity> roles= new LinkedHashSet<>();;

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles.add(new UserRoleEntity(this, USER));
    }

    public UserEntity(Long id) {
        this.id = id;
    }

    public Set<UserRole> getRoles() {
        return roles.stream().map(UserRoleEntity::getRole).collect(Collectors.toSet());
    }
}
