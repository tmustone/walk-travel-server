package net.dynu.wpeckers.walktraveler.database.repository;

import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    public UserEntity readByEmail(String email);
}
