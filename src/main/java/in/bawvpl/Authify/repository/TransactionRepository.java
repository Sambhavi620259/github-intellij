package in.bawvpl.Authify.repository;

import in.bawvpl.Authify.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByUserId(String userId);

    @Query(
            value = "SELECT * FROM transactions WHERE user_id = ?1 ORDER BY created_at DESC LIMIT ?2",
            nativeQuery = true
    )
    List<TransactionEntity> findRecentTransactions(String userId, int limit);
}
