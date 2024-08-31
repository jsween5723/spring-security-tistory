package example.springsecuritytistory.service;

public interface UserManager {
    long join(JoinUser joinUser);
    void renewInformation(long userId, UserInformation information);

    Long getIdByUsername(String username);
}
