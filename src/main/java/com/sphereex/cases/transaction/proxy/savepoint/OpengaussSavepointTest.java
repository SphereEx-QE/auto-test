package com.sphereex.cases.transaction.proxy.savepoint;

import com.sphereex.cases.base.ShardingSphereProxyBaseTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.cases.base.item.DBType;
import com.sphereex.core.Status;
import lombok.Getter;
import org.opengauss.jdbc.PSQLSavepoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

@AutoTest
public final class OpengaussSavepointTest extends ShardingSphereProxyBaseTest {
    
    @Getter
    private final DBType dbType = DBType.OPENGAUSS;
    
    private static final Logger logger = LoggerFactory.getLogger(OpengaussSavepointTest.class);
    
    @Override
    public Status pre() {
        Status s = super.pre();
        if (!s.isSuccess()) {
            return s;
        }
        try {
            Connection conn = getAutoDataSource().getConnection();
            Statement stmt;
            Statement stmt1;
            stmt = conn.createStatement();
            stmt.executeUpdate("drop table if exists account;");
            stmt1 = conn.createStatement();
            stmt1.executeUpdate("create table account(id int, balance float ,transaction_id int);");
        } catch (SQLException e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }
    
    @Override
    public Status run() {
        try {
            case1();
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
    
        try {
            case2();
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
    
        try {
            case3();
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }
    
    private void case1 () throws Exception {
        Connection conn = getAutoDataSource().getConnection();
        conn.setAutoCommit(false);
        checkRowCount(conn, 0);
        Statement statement1 = conn.createStatement();
        statement1.execute("insert into account(id, balance, transaction_id) values(1,1,1);");
        Savepoint point1 = conn.setSavepoint("point1");
        checkRowCount(conn, 1);
        Statement statement2 = conn.createStatement();
        statement2.execute("insert into account(id, balance, transaction_id) values(2,2,2);");
        checkRowCount(conn, 2);
        conn.rollback(point1);
        checkRowCount(conn, 1);
        conn.commit();
        checkRowCount(conn, 1);
    }
    
    private void case2() throws Exception{
        Connection conn = getAutoDataSource().getConnection();
        conn.setAutoCommit(false);
        checkRowCount(conn, 1);
        Statement statement1 = conn.createStatement();
        statement1.execute("insert into account(id, balance, transaction_id) values(2,2,2);");
        Savepoint point2 = conn.setSavepoint("point2");
        checkRowCount(conn, 2);
        Statement statement2 = conn.createStatement();
        statement2.execute("insert into account(id, balance, transaction_id) values(3,3,3);");
        checkRowCount(conn, 3);
        conn.releaseSavepoint(point2);
        checkRowCount(conn, 3);
        conn.commit();
        checkRowCount(conn, 3);
    }
    
    private void checkRowCount(Connection conn, int rowNum) throws Exception {
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("select * from account;");
        int rn = 0;
        while (rs.next()) {
            rn++;
        }
        statement.close();
        if (rn != rowNum) {
            logger.error("recode num assert error, expect:{}, actual:{}.", rowNum, rn);
            throw new Exception(String.format("recode num assert error, expect:{}, actual:{}.", rowNum, rn));
        }
    }
    
    private void case3() throws Exception{
        Connection conn = getAutoDataSource().getConnection();
        try {
            conn.setSavepoint("point");
            logger.error("expect exception, but no exception report");
            throw new Exception("expect exception, but no exception report");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            if (!"Cannot establish a savepoint in auto-commit mode.".equals(ex.getMessage())) {
                logger.error("expect exception message error");
                throw new Exception("expect exception message error");
            }
        }
        try {
            conn.rollback(new PSQLSavepoint("point1"));
            logger.error("expect exception, but no exception report");
            throw new Exception("expect exception, but no exception report");
        } catch (Exception ex) {
            if (!ex.getMessage().endsWith("ERROR: ROLLBACK TO SAVEPOINT can only be used in transaction blocks")) {
                logger.error("expect exception message error");
                throw new Exception("expect exception message error");
            }
        }

        try {
            conn.releaseSavepoint(new PSQLSavepoint("point1"));
            logger.error("expect exception, but no exception report");
            throw new Exception("expect exception, but no exception report");
        } catch (Exception ex) {
            if (!ex.getMessage().endsWith("ERROR: RELEASE SAVEPOINT can only be used in transaction blocks")) {
                logger.error("expect exception message error");
                throw new Exception("expect exception message error");
            }
        }
    }
    
    @Override
    public void init() {
        String name = "OpengaussSavepointTest";
        String feature = "proxy-transaction";
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
        caseInfo = new CaseInfo(name, feature, tag, message);
    }
    
    @Override
    public CaseInfo getCaseInfo() {
        if (null == caseInfo) {
            init();
        }
        return caseInfo;
    }
}
