package chap07.debit;

public interface AutoDebitInfoRepository {
    void save(AutoDebitInfo info);

    AutoDebitInfo findOne(String userId);
}
