package com.sphereex.cases.proxy.transaction.savepoint;

import com.sphereex.cases.ProxyBaseTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import org.opengauss.jdbc.PSQLSavepoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.sql.Statement;

@AutoTest
public final class OpengaussSavepointTest extends ProxyBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(OpengaussSavepointTest.class);
    
    public OpengaussSavepointTest() {
        super(DBType.OPENGAUSS);
    }
    
    @Override
    public void pre() throws Exception {
        Connection conn = getAutodataSource().getConnection();
        Statement stmt;
        Statement stmt1;
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists account;");
        stmt.close();
        stmt1 = conn.createStatement();
        stmt1.executeUpdate("create table account(id int, balance float ,transaction_id int);");
        stmt1.close();
        conn.close();
    }
    
    @Override
    public boolean run() throws Exception {
        boolean r1 = case1();
        boolean r2 = case2();
        boolean r3 = case3();
        if (!r1 || !r2 || !r3) {
            return false;
        }
        return true;
    }
    
    @Override
    public void end() throws Exception {
    }
    
    private boolean case1 () throws Exception {
        Connection conn = getAutodataSource().getConnection();
        conn.setAutoCommit(false);
        checkRowCount(conn, 0);
        if (!checkRowCount(conn, 0)) {
            return false;
        }
        Statement statement1 = conn.createStatement();
        statement1.execute("insert into account(id, balance, transaction_id) values(1,1,1);");
        Savepoint point1 = conn.setSavepoint("point1");
        if (!checkRowCount(conn, 1)) {
            return false;
        }
        Statement statement2 = conn.createStatement();
        statement2.execute("insert into account(id, balance, transaction_id) values(2,2,2);");
        if (!checkRowCount(conn, 2)) {
            return false;
        }
        conn.rollback(point1);
        if (!checkRowCount(conn, 1)) {
            return false;
        }
        conn.commit();
        if (!checkRowCount(conn, 1)) {
            return false;
        }
        conn.close();
        return true;
    }
    
    private boolean case2() throws Exception{
        Connection conn = getAutodataSource().getConnection();
        conn.setAutoCommit(false);
        if (!checkRowCount(conn, 1)) {
            return false;
        }
        Statement statement1 = conn.createStatement();
        statement1.execute("insert into account(id, balance, transaction_id) values(2,2,2);");
        Savepoint point2 = conn.setSavepoint("point2");
        if (!checkRowCount(conn, 2)) {
            return false;
        }
        Statement statement2 = conn.createStatement();
        statement2.execute("insert into account(id, balance, transaction_id) values(3,3,3);");
        if (!checkRowCount(conn, 3)) {
            return false;
        }
        conn.releaseSavepoint(point2);
        if (!checkRowCount(conn, 3)) {
            return false;
        }
        conn.commit();
        if (!checkRowCount(conn, 3)) {
            return false;
        }
        conn.close();
        return true;
    }
    
    private boolean checkRowCount(Connection conn, int rowNum) throws Exception {
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("select * from account;");
        int rn = 0;
        while (rs.next()) {
            rn++;
        }
        statement.close();
        if (rn != rowNum) {
            logger.error("recode num assert error, expect:{}, actual:{}.", rowNum, rn);
            return false;
        }
        return true;
    }
    
    private boolean case3() throws Exception{
        Connection conn = getAutodataSource().getConnection();
        try {
            conn.setSavepoint("point");
            logger.error("expect exception, but no exception report");
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            if (!"Cannot establish a savepoint in auto-commit mode.".equals(ex.getMessage())) {
                logger.error("expect exception message error");
                return false;
            }
        }
        try {
            conn.rollback(new PSQLSavepoint("point1"));
            logger.error("expect exception, but no exception report");
            return false;
        } catch (Exception ex) {
            if (!ex.getMessage().endsWith("ERROR: ROLLBACK TO SAVEPOINT can only be used in transaction blocks")) {
                logger.error("expect exception message error");
                return false;
            }
        }

        try {
            conn.releaseSavepoint(new PSQLSavepoint("point1"));
            logger.error("expect exception, but no exception report");
            return false;
        } catch (Exception ex) {
            if (!ex.getMessage().endsWith("ERROR: RELEASE SAVEPOINT can only be used in transaction blocks")) {
                logger.error("expect exception message error");
                return false;
            }
        }
        conn.close();
        return true;
    }
    
    @Override
    public void initCaseInfo() {
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
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
