package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.TransactionEntity;
import in.bawvpl.Authify.io.DashboardSummaryResponse;
import in.bawvpl.Authify.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TransactionRepository transactionRepository;

    @Override
    public DashboardSummaryResponse getSummary(String userId) {

        List<TransactionEntity> all = transactionRepository.findByUserId(userId);

        double totalBalance = 0;
        double totalSpent = 0;
        double totalReceived = 0;

        for (TransactionEntity tx : all) {

            if ("CREDIT".equalsIgnoreCase(tx.getType())) {
                totalBalance += tx.getAmount();
                totalReceived += tx.getAmount();
            } else {
                totalBalance -= tx.getAmount();
                totalSpent += tx.getAmount();
            }
        }

        return DashboardSummaryResponse.builder()
                .totalBalance(totalBalance)
                .totalTransactions(all.size())
                .totalSpent(totalSpent)
                .totalReceived(totalReceived)
                .build();
    }

    @Override
    public List<TransactionEntity> getRecentTransactions(String userId, int limit) {
        return transactionRepository.findRecentTransactions(userId, limit);
    }
}
