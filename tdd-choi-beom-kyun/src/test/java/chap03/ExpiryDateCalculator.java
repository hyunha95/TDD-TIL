package chap03;

import java.time.LocalDate;
import java.time.YearMonth;

public class ExpiryDateCalculator {

    public LocalDate calculateExpiryDate(PayData payData) {
        int addedMonths = getAddedMonths(payData.getPayAmount());
        if (payData.getFirstBillingDate() != null) {
            return expiryDateUsingFirstBillingDate(payData, addedMonths);
        } else {
            return payData.getBillingDate().plusMonths(addedMonths);
        }
    }

    private int getAddedMonths(int payAmount) {
        if (payAmount >= 100_000) {
            return (12 * (payAmount / 100_000)) + ((payAmount % 100_000) / 10_000);
        } else {
            return payAmount / 10_000;
        }
    }

    private LocalDate expiryDateUsingFirstBillingDate(PayData payData, int addedMonths) {
        LocalDate candidateExp = payData.getBillingDate().plusMonths(addedMonths); // 후보 만요일 구함
        final int dayOfFirstBilling = payData.getFirstBillingDate().getDayOfMonth();

        // 첫 납부일의 일자와 후보 만료일의 일자가 다르면 첫 납부일의 일자를 후보 만료일의 일자로 사용
        if (!isSameDayOfMonth(dayOfFirstBilling, candidateExp.getDayOfMonth())) {
            final int dayLenOfCandiMon = lastDayOfMonth(candidateExp);
            // 후보 만요일이 포함된 달의 마지막 날 < 첫 납부일의 일자
            if(dayLenOfCandiMon < payData.getFirstBillingDate().getDayOfMonth()) {
                return candidateExp.withDayOfMonth(dayLenOfCandiMon);
            }
            return candidateExp.withDayOfMonth(dayOfFirstBilling);
        } else {
            return candidateExp;
        }
    }

    private boolean isSameDayOfMonth(int dayOfFirstBilling, int dayOfCandidateExp) {
        return dayOfFirstBilling == dayOfCandidateExp;
    }

    private int lastDayOfMonth(LocalDate localDate) {
        return YearMonth.from(localDate).lengthOfMonth();
    }
}
