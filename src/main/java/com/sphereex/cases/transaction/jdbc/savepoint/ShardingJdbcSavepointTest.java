package com.sphereex.cases.transaction.jdbc.savepoint;

import com.sphereex.cases.base.ShardingSphereJdbcBaseTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

public abstract class ShardingJdbcSavepointTest extends ShardingSphereJdbcBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ShardingJdbcSavepointTest.class);
    
    @Override
    public Status pre(){
        super.pre();
        try {
            Connection conn = getAutoDataSource().getConnection();
            Statement dropTable = conn.createStatement();
            dropTable.execute("drop table if exists account;");
            Statement createTable = conn.createStatement();
            createTable.execute("create table account(id int, balance float ,transaction_id int);");
            conn.close();
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
        return new Status(true, "");
    }
    
    @Override
    public CaseInfo getCaseInfo() {
        if (null == caseInfo) {
            init();
        }
        return caseInfo;
    }
    
    private void case1() throws Exception{
        Connection conn = getAutoDataSource().getConnection();
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
        Connection conn = getAutoDataSource().getConnection();
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
            logger.error("recode num assert error, expect:{}, actual:{}.", rowNum, rn);
            throw new Exception(String.format("recode num assert error, expect:{}, actual:{}.", rowNum, rn));
        }
    }
}
