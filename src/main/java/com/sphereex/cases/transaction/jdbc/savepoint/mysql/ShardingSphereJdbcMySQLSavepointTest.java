package com.sphereex.cases.transaction.jdbc.savepoint.mysql;

import com.sphereex.cases.transaction.jdbc.savepoint.ShardingJdbcSavepointTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import lombok.Getter;

@AutoTest
public final class ShardingSphereJdbcMySQLSavepointTest extends ShardingJdbcSavepointTest {
    
    @Getter
    private final String yamlFile = null;
    
    @Override
    public void init() {
        String name = "ShardingJdbcMySQLSavepointTest";
        String feature = "transaction";
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
        String configPath = "conf/default/jdbc/mysql";
        String clientType = "jdbc";
        caseInfo = new CaseInfo(name, feature, tag, message, DBType.MYSQL, clientType, configPath);
    }
}
