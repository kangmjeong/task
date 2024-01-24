/* (C) 2024 AladdinSystem License */
package aladdinsys.api.task.repository;


import aladdinsys.api.task.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
  Optional<UserEntity> findByUserId(String userId);
}
