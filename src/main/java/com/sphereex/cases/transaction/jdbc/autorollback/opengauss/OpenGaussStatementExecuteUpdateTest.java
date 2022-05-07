package com.sphereex.cases.transaction.jdbc.autorollback.opengauss;

import com.sphereex.cases.transaction.jdbc.autorollback.ShardingSphereJdbcStatementExecuteUpdateTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;

@AutoTest
public final class OpenGaussStatementExecuteUpdateTest extends ShardingSphereJdbcStatementExecuteUpdateTest {
    
    @Override
    public void init() {
        String name = getClass().getSimpleName();
        String feature = "transaction";
        String tag = "autorollback";
        String message = "";
        String configPath = "conf/default/jdbc/opengauss";
        String clientType = "jdbc";
        caseInfo = new CaseInfo(name, feature, tag, message, DBType.OPENGAUSS, clientType, configPath);
    }
}
