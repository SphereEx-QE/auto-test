package com.sphereex.cases.transaction.proxy.commitrollback.mysql;

import com.sphereex.cases.transaction.proxy.commitrollback.ShardingCommitRollbackTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import lombok.Getter;

@AutoTest
public final class ShardingSphereMySQLCommitRollbackTest extends ShardingCommitRollbackTest {

    @Getter
    private final String yamlFile = null;

    @Override
    public void init() {
        String name = "ShardingSphereMySQLCommitRollbackTest";
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
        String configPath = "conf/case/transaction/proxy/commitrollback/mysql";
        String clientType = "proxy";
        caseInfo = new CaseInfo(name, feature, tag, message, DBType.MYSQL, clientType, configPath);
    }
}
