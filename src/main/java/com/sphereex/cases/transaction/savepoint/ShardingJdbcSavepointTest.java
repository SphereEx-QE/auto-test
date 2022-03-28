package com.sphereex.cases.transaction.savepoint;

import com.sphereex.cases.ShardingJdbcBaseTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.sql.Statement;

@AutoTest
public class ShardingJdbcSavepointTest extends ShardingJdbcBaseTest {
    
    public ShardingJdbcSavepointTest() {
//        super("mysql");
//        super("opengauss");
        super("postgresql");
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setName("ShardingJdbcSavepointTest");
        caseInfo.setFeature("transaction-jdbc");
        caseInfo.setTag("savepoint");
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
                "12. check if row count is 1" +
                "13. test for release savepoint");
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
        conn.close();
    }
    
    @Override
    public void run() throws Exception {
        super.run();
        case1();
        case2();
    }
    
    private void case1() throws Exception{
        Connection conn = getDataSource().getConnection();
        conn.setAutoCommit(false);
        Statement std1 = conn.createStatement();
        std1.execute("insert into account(id, balance, transaction_id) values(1,1,1);");
        checkRowCount(conn, 1);
        Savepoint point1 = conn.setSavepoint("point1");
        Statement std2 = conn.createStatement();
        std2.execute("insert into account(id, balance, transaction_id) values(2,2,2);");
        checkRowCount(conn, 2);
        conn.rollback(point1);
        checkRowCount(conn, 1);
        conn.commit();
        checkRowCount(conn, 1);
    }

    private void case2() throws Exception{
        Connection conn = getDataSource().getConnection();
        conn.setAutoCommit(false);
        Statement std1 = conn.createStatement();
        std1.execute("insert into account(id, balance, transaction_id) values(2,2,2);");
        checkRowCount(conn, 2);
        Savepoint point1 = conn.setSavepoint("point1");
        Statement std2 = conn.createStatement();
        std2.execute("insert into account(id, balance, transaction_id) values(3,3,3);");
        checkRowCount(conn, 3);
        conn.rollback(point1);
        checkRowCount(conn, 2);
        conn.rollback();
        checkRowCount(conn, 1);
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
}
