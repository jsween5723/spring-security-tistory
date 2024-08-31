package example.springsecuritytistory.service;

import example.springsecuritytistory.repository.UserEntity;
import example.springsecuritytistory.repository.UserInformationEntity;
import example.springsecuritytistory.repository.UserInformationRepository;
import example.springsecuritytistory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserManager {

    private final UserRepository repository;
    private final UserInformationRepository informationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public long join(JoinUser joinUser) {
        UserEntity userEntity = repository.save(
            new UserEntity(joinUser.username(), passwordEncoder.encode(joinUser.password())));
        return userEntity.getId();
    }


    @Transactional
    public void renewInformation(long userId, UserInformation information) {
        informationRepository.save(new UserInformationEntity(userId, information.name()));
    }

    @Override
    public Long getIdByUsername(String username) {
        return repository.getIdByUsername(username);
    }
}
