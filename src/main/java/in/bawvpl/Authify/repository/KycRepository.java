package in.bawvpl.Authify.repository;

import in.bawvpl.Authify.entity.KycEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KycRepository extends JpaRepository<KycEntity, Long> {
    List<KycEntity> findByUserId(String userId);
}
