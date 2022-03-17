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
public class ShardingJdbcPreparedStatementExecuteUpdateTest extends ShardingJdbcBaseTest {
    
    public ShardingJdbcPreparedStatementExecuteUpdateTest() {
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setName("ShardingJdbcPreparedStatementExecuteUpdateTest");
        caseInfo.setFeature("transaction");
        caseInfo.setTag("jdbc-pg-og-auto-rollback");
        caseInfo.setStatus(false);
        setCaseInfo(caseInfo);
    }
    
    @Override
    public void pre() throws Exception {
        super.pre();
        Connection conn = getDataSource().getConnection();
        Statement dropTable = conn.createStatement();
        dropTable.execute("drop table if exists account;");
        Statement createTable = conn.createStatement();
        createTable.execute("create table account(id int, balance float ,transaction_id int);");
        Statement statement = conn.createStatement();
        statement.execute("insert into account(id, balance, transaction_id) values(1,1,1);");
        conn.close();
    }
    
    @Override
    public void run() throws SQLException {
        ShardingSphereConnection conn = (ShardingSphereConnection) getDataSource().getConnection();
        conn.setAutoCommit(false);
        assertFalse(conn.getConnectionManager().getConnectionTransaction().isRollbackOnly());
        Statement statement2 = conn.createStatement();
        PreparedStatement statement3 = conn.prepareStatement("update account1 set balance=101 where id=?;");
        try {
            statement2.execute("update account set balance=100 where id=1;");
            statement3.setInt(1,1);
            statement3.executeUpdate();
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
        conn.close();
    }
}
