package chap07.debit;

import chap07.debit.AutoDebitInfo;
import chap07.debit.AutoDebitInfoRepository;

public class JpaAutoDebitgInfoRepository implements AutoDebitInfoRepository {
    @Override
    public void save(AutoDebitInfo info) {

    }

    @Override
    public AutoDebitInfo findOne(String userId) {
        return null;
    }
}
