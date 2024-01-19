package aladdinsys.api.task.repository;

import aladdinsys.api.task.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
     Optional<UserEntity> findByUserId(String userId);
     Optional<UserEntity> findByUserIdAndPassword(String userId, String password);

}
