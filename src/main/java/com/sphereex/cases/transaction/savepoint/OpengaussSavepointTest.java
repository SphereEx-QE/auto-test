package com.sphereex.cases.transaction.savepoint;

import com.sphereex.cases.BaseCaseImpl;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBInfo;
import com.sphereex.utils.OpenGaussUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Objects;

@AutoTest
public class OpengaussSavepointTest extends BaseCaseImpl {
    
    public OpengaussSavepointTest() {
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setName("OpengaussSavepointTest");
        caseInfo.setFeature("transaction");
        caseInfo.setTag("opengauss-savepoint");
        caseInfo.setStatus(false);
        caseInfo.setMessage("this is a test for savepoint" +
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
                "12. check if row count is 1");
        setCaseInfo(caseInfo);
    }
    
    @Override
    public void pre() throws Exception {
        super.pre();
        DBInfo dbInfo = Objects.requireNonNull(getDbInfo());
        Connection conn = OpenGaussUtil.getInstance().getConnnection(dbInfo);
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
    public void run() throws Exception {
        super.run();
        case1();
        case2();
    }
    
    private void case1 () throws Exception {
        DBInfo dbInfo = Objects.requireNonNull(getDbInfo());
        Connection conn = OpenGaussUtil.getInstance().getConnnection(dbInfo);
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
        conn.close();
    }
    
    private void case2() throws Exception{
        DBInfo dbInfo = Objects.requireNonNull(getDbInfo());
        Connection conn = OpenGaussUtil.getInstance().getConnnection(dbInfo);
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
        conn.close();
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
            throw new Exception(String.format("recode num assert error, expect:%d, actual:%d.", rowNum, rn));
        }
    }
    
    @Override
    public void end() throws Exception {
        super.end();
        getCaseInfo().setStatus(true);
    }
}
