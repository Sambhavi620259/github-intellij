package in.bawvpl.Authify.io;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DashboardSummaryResponse {
    private int totalTransactions = 0;
    private long successfulPayments = 0;
    private long failedPayments = 0;
    private boolean activeSubscription = false;
    private boolean kycCompleted = false;
    private double totalAmount = 0.0;
}
