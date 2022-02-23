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
    
    public JdbcTest() {
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setName("JdbcTest");
        caseInfo.setFeature("transaction");
        caseInfo.setTag("Traffic");
        caseInfo.setStatus(false);
        setCaseInfo(caseInfo);
    }
    
    @Override
    public void run() throws SQLException {
        Connection conn1 = getDataSource().getConnection();
        conn1.setAutoCommit(false);
        PreparedStatement preparedStatement = conn1.prepareStatement("select * from account where id=?");
        preparedStatement.setInt(1,1);
        preparedStatement.execute();
        conn1.commit();
    }
}
