package chap07.debit;

import java.time.LocalDateTime;

public class AutoDebitInfo {

    private String userId;
    private String cardNumber;
    private LocalDateTime createAt;

    public AutoDebitInfo(String userId, String cardNumber, LocalDateTime createAt) {
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.createAt = createAt;
    }

    public String getUserId() {
        return userId;
    }

    public void changeCarNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }
}
