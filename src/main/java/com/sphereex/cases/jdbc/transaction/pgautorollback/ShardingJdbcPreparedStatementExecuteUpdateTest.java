package com.sphereex.cases.jdbc.transaction.pgautorollback;

import com.sphereex.cases.ShardingJdbcBaseTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import org.apache.shardingsphere.driver.jdbc.core.connection.ShardingSphereConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@AutoTest
public class ShardingJdbcPreparedStatementExecuteUpdateTest extends ShardingJdbcBaseTest {
    
    private  final Logger logger = LoggerFactory.getLogger(ShardingJdbcPreparedStatementExecuteUpdateTest.class);
    
    public ShardingJdbcPreparedStatementExecuteUpdateTest() {
        super("opengauss");
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
    public boolean run() throws SQLException {
        ShardingSphereConnection conn = (ShardingSphereConnection) getDataSource().getConnection();
        conn.setAutoCommit(false);
        if (conn.getConnectionManager().getConnectionTransaction().isRollbackOnly()) {
            logger.error("expect transaction is not rollback only, but transaction rollback only.");
            return false;
        }
        Statement statement2 = conn.createStatement();
        PreparedStatement statement3 = conn.prepareStatement("update account1 set balance=101 where id=?;");
        try {
            statement2.execute("update account set balance=100 where id=1;");
            statement3.setInt(1,1);
            statement3.executeUpdate();
            logger.error("expect report SQLException, but not report");
            return false;
        } catch (SQLException ex) {
            if (!conn.getConnectionManager().getConnectionTransaction().isRollbackOnly()) {
                logger.error("expect transaction rollback only, but not");
                return false;
            }
        }
        conn.commit();
        Statement statement4 = conn.createStatement();
        ResultSet r = statement4.executeQuery("select * from account;");
        if (r.next()) {
            int balance = r.getInt("balance");
            if (1 != balance) {
                logger.error("expect balance is 1, but balance:{}.", balance);
                return false;
            }
        } else {
            logger.error("expect one recode, but not.");
            return false;
        }
        conn.close();
        return true;
    }
    
    @Override
    public void end() throws Exception {
    
    }
    
    @Override
    public void initCaseInfo() {
        String name = "ShardingJdbcPreparedStatementExecuteUpdateTest";
        String feature = "jdbc-transaction";
        String tag = "pg-og-auto-rollback";
        String message = "";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
