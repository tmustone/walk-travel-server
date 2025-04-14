package net.dynu.wpeckers.walktraveler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.database.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final String FAST_LOGIN_SECRET_SALT = "SALT-OF-SECRET-HASH";

    private String createFastLoginSecret(String email) {
        return new DigestUtils("SHA3-256").digestAsHex(FAST_LOGIN_SECRET_SALT + email);
    }

    public Long create(UserEntity user) {
        UserEntity savedUser = userRepository.save(user);
        user.setUserId(savedUser.getUserId());
        return savedUser.getUserId();
    }

    public UserEntity readOrCreateUserAndLoginByEmail(String email) {
        UserEntity user = userRepository.readByEmail(email);
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setRegisterDate(new Date());
            user.setFastLoginSecret(createFastLoginSecret(email));
            Long userId = create(user);
            log.info("Login: Auto registered user to database with e-mail {} and user ID {}", email, userId);
        } else {
            log.info("Login: Using already existing user from database : {}" , user.getUserId());
            user.setLastLoginDate(new Date());
            if (user.getFastLoginSecret() == null) {
                user.setFastLoginSecret(this.createFastLoginSecret(user.getEmail()));
                log.info("Updated existing user fast login secret {} for user {}", user.getFastLoginSecret(), user.getEmail());
            }
            userRepository.save(user);
        }
        return user;
    }

    public UserEntity readAndLoginUserByFastLoginSecret(String fastLoginSecret) {
        UserEntity user = userRepository.readByFastLoginSecret(fastLoginSecret);
        if (user == null) {
            log.warn("User not found with fast login secret {} from database!", fastLoginSecret);
            return null;
        }
        user.setLastLoginDate(new Date());
        userRepository.save(user);
        return user;
    }

    public void update(UserEntity user) {
        userRepository.save(user);
    }

    public UserEntity read(Long id) {
        return userRepository.findById(id).get();
    }

    public UserEntity readByEmail(String email) {
        return userRepository.readByEmail(email);
    }

    public Iterable<UserEntity> readAll() {
        return userRepository.findAll();
    }

}
