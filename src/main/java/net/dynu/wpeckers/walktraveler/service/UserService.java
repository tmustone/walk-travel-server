package net.dynu.wpeckers.walktraveler.service;

import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Long create(UserEntity user) {
        UserEntity savedUser = userRepository.save(user);
        user.setUserId(savedUser.getUserId());
        return savedUser.getUserId();
    }

    public UserEntity login(String email) {
        UserEntity user = userRepository.readByEmail(email);
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setRegisterDate(new Date());
            Long userId = create(user);
            log.info("Login: Auto registered user to database with e-mail {} and user ID {}", email, userId);
        } else {
            log.info("Login: Using already existing user from database : {}" , user.getUserId());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
        }
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
