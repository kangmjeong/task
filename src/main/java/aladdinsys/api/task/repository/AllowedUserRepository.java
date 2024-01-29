/* (C) 2024 AladdinSystem License */
package aladdinsys.api.task.repository;


import aladdinsys.api.task.entity.AllowedUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllowedUserRepository extends JpaRepository<AllowedUserEntity, String> {
  Optional<AllowedUserEntity> findByNameAndRegNo(String name, String regNo);
}
