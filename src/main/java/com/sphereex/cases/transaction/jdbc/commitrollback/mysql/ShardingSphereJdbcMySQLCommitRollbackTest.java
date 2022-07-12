package com.sphereex.cases.transaction.jdbc.commitrollback.mysql;

import com.sphereex.cases.transaction.jdbc.commitrollback.ShardingJdbcCommitRollbackTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import lombok.Getter;

@AutoTest
public final class ShardingSphereJdbcMySQLCommitRollbackTest extends ShardingJdbcCommitRollbackTest {

    @Getter
    private final String yamlFile = null;

    @Override
    public void init() {
        String name = "ShardingSphereJdbcMySQLCommitRollbackTest";
        String feature = "transaction";
        String tag = "commitRollback";
        String message =
                "test for rollback:\n" +
                        "1. create a session\n" +
                        "2. begin a transaction\n" +
                        "3. check if row count is 0\n" +
                        "4. insert one row (1,1,1)\n" +
                        "5. check if row count is 1\n" +
                        "6. rollback\n" +
                        "7. check if row count is 0"

                        + "test for commit:\n" +
                        "1. create a session\n" +
                        "2. begin a transaction\n" +
                        "3. check if row count is 0\n" +
                        "4. insert one row (1,1,1)\n" +
                        "5. check if row count is 1\n" +
                        "6. commit the transaction\n" +
                        "7. check if row count is 1";
        String configPath = "conf/default/jdbc/mysql";
        String clientType = "jdbc";
        caseInfo = new CaseInfo(name, feature, tag, message, DBType.MYSQL, clientType, configPath);
    }
}
