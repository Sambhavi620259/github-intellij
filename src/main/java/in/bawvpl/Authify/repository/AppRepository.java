
package in.bawvpl.Authify.repository;

import in.bawvpl.Authify.entity.AppEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRepository extends JpaRepository<AppEntity, Long> {
    Optional<AppEntity> findByAppId(String appId);
}

