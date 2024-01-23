package aladdinsys.api.task.repository;

import aladdinsys.api.task.entity.AllowedUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllowedUserRepository extends JpaRepository<AllowedUserEntity, String> {
    Optional<AllowedUserEntity> findByNameAndRegNo(String name, String regNo);
}
