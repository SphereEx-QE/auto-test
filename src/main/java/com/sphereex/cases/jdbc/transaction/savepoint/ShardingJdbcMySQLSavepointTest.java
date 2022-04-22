package com.sphereex.cases.jdbc.transaction.savepoint;

import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;

@AutoTest
public final class ShardingJdbcMySQLSavepointTest extends ShardingJdbcSavepointTest {
    
    public ShardingJdbcMySQLSavepointTest() throws Exception {
        super(DBType.MYSQL);
    }
    
    @Override
    public void initCaseInfo() {
        String name = "ShardingJdbcMySQLSavepointTest";
        String feature = "jdbc-transaction";
        String tag = "savepoint";
        String message = "this is a test for savepoint" +
                "1. create a session" +
                "2. begin a transaction" +
                "3. check if row count is 0" +
                "4. insert one row (1,1,1)" +
                "5. savepoint point1" +
                "6. check if row count is 1" +
                "7. insert one row (2,2,2)" +
                "8. check if row count is 2" +
                "9. rollback to savepoint point1" +
                "10. check if row count is 1" +
                "11. commit the transaction" +
                "12. check if row count is 1" +
                "13. test for release savepoint";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
