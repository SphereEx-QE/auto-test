package com.sphereex.cases.transaction.traffic;

import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@AutoTest
public class JdbcTest extends TrafficBaseTest{
    private  final Logger logger = LoggerFactory.getLogger(TrafficSetTransactionIsolationLevelTest.class);
    
    @Override
    public void run() throws SQLException {
        Connection conn1 = getDataSource().getConnection();
        conn1.setAutoCommit(false);
        PreparedStatement preparedStatement = conn1.prepareStatement("select * from account where id=?");
        preparedStatement.setInt(1,1);
        preparedStatement.execute();
        conn1.commit();
    }
    
    @Override
    public void initCaseInfo() {
        String name = "JdbcTest";
        String feature = "transaction";
        String tag = "conf/Traffic";
        String message = "";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
