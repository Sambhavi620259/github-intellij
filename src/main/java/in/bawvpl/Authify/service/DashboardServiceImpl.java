package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.KycEntity;
import in.bawvpl.Authify.entity.TransactionEntity;
import in.bawvpl.Authify.entity.UserSubscription;
import in.bawvpl.Authify.io.DashboardSummaryResponse;
import in.bawvpl.Authify.repository.KycRepository;
import in.bawvpl.Authify.repository.TransactionRepository;
import in.bawvpl.Authify.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TransactionRepository transactionRepo;
    private final UserSubscriptionRepository subscriptionRepo;   // FIXED
    private final KycRepository kycRepo;

    @Override
    public DashboardSummaryResponse getSummary(String userId) {

        if (userId == null) {
            return new DashboardSummaryResponse();
        }

        // 1. Transactions
        List<TransactionEntity> transactions =
                safeList(transactionRepo.findByUserId(userId));

        long successCount = transactions.stream()
                .filter(t -> t != null && "SUCCESS".equalsIgnoreCase(t.getStatus()))
                .count();

        long failedCount = transactions.stream()
                .filter(t -> t != null && "FAILED".equalsIgnoreCase(t.getStatus()))
                .count();

        // 2. Subscriptions
        List<UserSubscription> userSubscriptions =
                safeList(subscriptionRepo.findByUserId(userId));

        boolean hasActiveSubscription = userSubscriptions.stream()
                .filter(Objects::nonNull)
                .anyMatch(s -> s.getEndDate() != null && s.getEndDate().isAfter(Instant.now()));

        // 3. KYC
        List<KycEntity> kycDocs =
                safeList(kycRepo.findByUserId(userId));

        boolean isKycCompleted = kycDocs.stream()
                .anyMatch(k -> Boolean.TRUE.equals(k.getCompleted()));

        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.setTotalTransactions(transactions.size());
        response.setSuccessfulPayments(successCount);
        response.setFailedPayments(failedCount);
        response.setActiveSubscription(hasActiveSubscription);
        response.setKycCompleted(isKycCompleted);

        double totalAmount = transactions.stream()
                .filter(t -> t != null && t.getAmount() != null)
                .mapToDouble(TransactionEntity::getAmount)
                .sum();

        response.setTotalAmount(totalAmount);
        return response;
    }

    @Override
    public List<TransactionEntity> getRecentTransactions(String userId, int limit) {
        if (userId == null) return Collections.emptyList();

        List<TransactionEntity> list =
                safeList(transactionRepo.findTopNByUserIdOrderByTimestampDesc(userId, limit));

        if (!list.isEmpty()) return list;

        // fallback
        List<TransactionEntity> all = safeList(transactionRepo.findByUserId(userId));

        return all.stream()
                .filter(Objects::nonNull)
                .sorted((a, b) -> {
                    Instant ta = a.getTimestamp();
                    Instant tb = b.getTimestamp();
                    if (ta == null && tb == null) return 0;
                    if (ta == null) return 1;
                    if (tb == null) return -1;
                    return tb.compareTo(ta);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
