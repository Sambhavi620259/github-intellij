package in.bawvpl.Authify.repository;

import in.bawvpl.Authify.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByUserId(String userId);

    // Optional helpful method — Spring Data supports `findTopNBy...` only with a numeric keyword:
    // for a dynamic limit use a custom query or Spring Data Pageable. Here are two options:

    // 1) Method using Pageable (recommended for production):
    // List<TransactionEntity> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);

    // 2) Convenience method with native query or @Query and limit:
    @Query(value = "SELECT t.* FROM transactions t WHERE t.user_id = :userId ORDER BY t.timestamp DESC LIMIT :limit", nativeQuery = true)
    List<TransactionEntity> findTopNByUserIdOrderByTimestampDesc(@Param("userId") String userId, @Param("limit") int limit);
}
