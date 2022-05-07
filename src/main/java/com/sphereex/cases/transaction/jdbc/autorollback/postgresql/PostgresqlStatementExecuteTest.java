package com.sphereex.cases.transaction.jdbc.autorollback.postgresql;

import com.sphereex.cases.transaction.jdbc.autorollback.ShardingSphereJdbcStatementExecuteTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;

@AutoTest
public final class PostgresqlStatementExecuteTest extends ShardingSphereJdbcStatementExecuteTest {
    
    @Override
    public void init() {
        String name = getClass().getSimpleName();
        String feature = "transaction";
        String tag = "autorollback";
        String message = "";
        String configPath = "conf/default/jdbc/postgresql";
        String clientType = "jdbc";
        caseInfo = new CaseInfo(name, feature, tag, message, DBType.POSTGRESQL, clientType, configPath);
    }
}