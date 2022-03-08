package com.sphereex.cases.transaction.shardingjdbc;

import com.sphereex.cases.ShardingJdbcBaseTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import org.apache.shardingsphere.driver.jdbc.core.connection.ShardingSphereConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@AutoTest
public class ShardingJdbcPreparedStatementExecuteQueryTest extends ShardingJdbcBaseTest {
    
    public ShardingJdbcPreparedStatementExecuteQueryTest() {
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setName("ShardingJdbcPreparedStatementExecuteQueryTest");
        caseInfo.setFeature("transaction");
        caseInfo.setTag("jdbc");
        caseInfo.setStatus(false);
        setCaseInfo(caseInfo);
    }
    
    @Override
    public void pre() throws Exception {
        super.pre();
        Connection conn = getDataSource().getConnection();
        Statement statement = conn.createStatement();
        statement.execute("delete from account;");
        statement.execute("insert into account(id, balance, transaction_id) values(1,1,1);");
        conn.close();
    }
    
    @Override
    public void run() throws SQLException {
        ShardingSphereConnection conn = (ShardingSphereConnection) getDataSource().getConnection();
        conn.setAutoCommit(false);
        assertFalse(conn.getConnectionManager().getConnectionTransaction().isRollbackOnly());
        Statement statement2 = conn.createStatement();
        PreparedStatement statement3 = conn.prepareStatement("select * from account1 where id=?");
        try {
            statement2.execute("update account set balance=100 where id=1;");
            statement3.setInt(1,1);
            statement3.executeQuery();
            throw new SQLException("expect report SQLException, but not report");
        } catch (SQLException ex) {
            assertTrue(conn.getConnectionManager().getConnectionTransaction().isRollbackOnly());
        }
        conn.commit();
        Statement statement4 = conn.createStatement();
        ResultSet r = statement4.executeQuery("select * from account;");
        if (r.next()) {
            int balance = r.getInt("balance");
            assertTrue(balance == 1);
        } else {
            throw new SQLException("expect one recode, but not.");
        }
    }
}
