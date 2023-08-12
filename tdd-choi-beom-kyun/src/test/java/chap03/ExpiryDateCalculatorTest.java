package chap03;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 매달 비용을 지불해야 사용할 수 있는 유료 서비스
 * - 서비스를 사용하려면 매달 1만 원을 선불로 납부한다. 납부일 기준으로 한 달 뒤가 서비스 만료일이 된다.
 * - 2개월 이상 요금을 납부할 수 있다.
 * - 10만 원을 납부하면 서비스를 1년 제공한다.
 */
public class ExpiryDateCalculatorTest {

    private void assertExpiryDate(PayData payData, LocalDate expectedExpiryDate) {
        ExpiryDateCalculator cal = new ExpiryDateCalculator();
        LocalDate realExpiryDate = cal.calculateExpiryDate(payData);
        assertEquals(expectedExpiryDate, realExpiryDate);
    }

    /**
     * 쉬운 것 부터 구현
     */
    @Test
    void 만원_납부하면_한달_뒤가_만료일이_됨() {
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2019, 3, 1))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2019, 4, 1)
        );

        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2019, 5, 5))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2019, 6, 5)
        );
    }

    /**
     * 예외 사항
     * - 납부일이 2019-01-31이고 납부액이 1만 원이면 만료일은 2019-02-28이다.
     * - 납부일이 2019-05-31이고 납부액이 1만 원이면 만요일은 2019-06-30이다.
     * - 납부일이 2020-01-31이고 납부액이 1만 원이면 만료일은 2020-02-29이다.
     *
     * LocalDate#plusMonths() 메서드가 알아서 한 달 추가 처리를 해 준다.
     */
    @Test
    void 납부일과_한달_뒤_일자가_같지_않음() {
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2019, 1, 31))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2019, 2, 28)
        );
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2019, 5, 31))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2019, 6, 30)
        );
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2020, 1, 31))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2020, 2, 29)
        );
    }

    /**
     * 다시 예외 상황
     * - 첫 납부일이 2019-01-31이고 만료되는 2019-02-28에 1만 원을 납부하면 다음 만료일은 2019-03-31이다.
     * - 첫 납부일이 2019-01-30이고 만료되는 2019-02-28에 1만 원을 납부하면 다음 만료일은 2019-03-30이다.
     * - 첫 납부일이 2019-05-31이고 만료되는 2019-06-30에 1만 원을 납부하면 다음 만료일은 2019-07-31이다.
     */
    @Test
    void 첫_납부일과_만료일의_일자가_같지_않을_때_1만_원_납부하면_첫_납부일_기준으로_다음_만료일_정함() {
        PayData payData = PayData.builder()
                .firstBillingDate(LocalDate.of(2019, 1, 31))
                .billingDate(LocalDate.of(2019, 2, 28))
                .payAmount(10_000)
                .build();

        assertExpiryDate(payData, LocalDate.of(2019, 3, 31));

        PayData payData2 = PayData.builder()
                .firstBillingDate(LocalDate.of(2019, 1, 30))
                .billingDate(LocalDate.of(2019, 2, 28))
                .payAmount(10_000)
                .build();

        assertExpiryDate(payData2, LocalDate.of(2019, 3, 30));

        PayData payData3 = PayData.builder()
                .firstBillingDate(LocalDate.of(2019, 5, 31))
                .billingDate(LocalDate.of(2019, 6, 30))
                .payAmount(10_000)
                .build();

        assertExpiryDate(payData3, LocalDate.of(2019, 7, 31));
    }

    /**
     * 쉬운 테스트
     * - 2만 원을 지불하면 만료일이 두 달 뒤가 된다.
     * - 3만 원을 지불하면 만료일이 석 달 뒤가 된다.
     */
    @Test
    void 이만원_이상_납부하면_비례해서_만료일_계산() {
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2019, 3, 1))
                        .payAmount(20_000)
                        .build(),
                LocalDate.of(2019, 5, 1)
        );
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2019, 3, 1))
                        .payAmount(30_000)
                        .build(),
                LocalDate.of(2019, 6, 1)
        );
    }

    /**
     * 예외 상황 테스트 추가
     * - 첫 납부일이 2019-01-31이고 만료되는 2019-02-28에 2만원을 납부하면 다음 만료일은 2019-04-30이다.
     */
    @Test
    void 첫_납부일과_만료일_일자가_다를때_이만원_이상_납부() {
        assertExpiryDate(
                PayData.builder()
                        .firstBillingDate(LocalDate.of(2019, 1, 31))
                        .billingDate(LocalDate.of(2019, 2, 28))
                        .payAmount(20_000)
                        .build(),
                LocalDate.of(2019, 4, 30)
        );
    }

    @Test
    void 십만원을_납부하면_1년_제공() {
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2019, 1, 28))
                        .payAmount(100_000)
                        .build(),
                LocalDate.of(2020, 1, 28)
        );
    }

    /**
     * 2020년 2월 29일과 같은 윤달 마지막 날에 10만 원을 납부하는 상황
     */
    @Test
    void 윤달_마지막_날에_10만원_납부() {
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2020, 2, 29))
                        .payAmount(100_000)
                        .build(),
                LocalDate.of(2021, 2, 28)
        );
    }

    /**
     * 13만원을 납부하는 사례
     * 13만원을 납부하는 경우 1년 3개월 뒤가 만료일이 되어야 한다.
     */
    @Test
    void 십삼만원_납부하는_경우_1년_3개월_뒤가_만료일() {
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2020, 1, 1))
                        .payAmount(130_000)
                        .build(),
                LocalDate.of(2021, 4, 1)
        );
    }

}
