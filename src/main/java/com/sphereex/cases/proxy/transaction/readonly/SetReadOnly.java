package com.sphereex.cases.proxy.transaction.readonly;

import com.sphereex.cases.ProxyBaseTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import com.sphereex.core.Status;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@AutoTest
public final class SetReadOnly extends ProxyBaseTest {
    
    @Getter
    private final DBType dbType = DBType.MYSQL;

    private static final Logger logger = LoggerFactory.getLogger(SetReadOnly.class);
    
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
            Statement stmt2;
            stmt = conn.createStatement();
            stmt.executeUpdate("drop table if exists account;");
            stmt1 = conn.createStatement();
            stmt1.executeUpdate("create table account(id int primary key , balance int not null);");
            stmt2 = conn.createStatement();
            stmt2.executeUpdate("insert into account(id,balance) values (1,0),(2,100);");
        } catch (SQLException e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }

    @Override
    public Status run() {
        try {
            step1();
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        try {
            step2();
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }
    
    private void step1() throws Exception {
        Connection conn = getAutoDataSource().getConnection();
        conn.setReadOnly(true);
        Statement statement1 = conn.createStatement();
        ResultSet rs = statement1.executeQuery("select * from account;");
        while (rs.next()) {
            int id = rs.getInt("id");
            int balance = rs.getInt("balance");
            if (id == 1) {
                if (balance != 0) {
                    logger.error("balance is {}, should be 0", balance);
                    throw new Exception(String.format("balance is {}, should be 0", balance));
                }
            }
            if (id == 2) {
                if (balance != 100) {
                    logger.error("balance is {}, should be 100", balance);
                    throw new Exception(String.format("balance is {}, should be 100", balance));
                }
            }
        }
        Statement statement2 = conn.createStatement();
        try {
            statement2.execute("update account set balance=100 where id=2;");
            logger.error("update run success, should failed");
            throw new Exception("update run success, should failed");
        } catch (SQLException e) {
            logger.info("update failed for expect");
            throw new Exception("update failed for expect");
        }
    }

    private void step2() throws Exception {
        Connection conn = getAutoDataSource().getConnection();
        Statement statement1 = conn.createStatement();
        ResultSet rs = statement1.executeQuery("select * from account");
        while (rs.next()) {
            int id = rs.getInt("id");
            int balance = rs.getInt("balance");
            if (id == 1) {
                if (balance != 0) {
                    logger.error("balance is {}, should be 0", balance);
                    throw new Exception(String.format("balance is {}, should be 0", balance));
                }
            }
            if (id == 2) {
                if (balance != 100) {
                    logger.error("balance is {}, should be 100", balance);
                    throw new Exception(String.format("balance is {}, should be 100", balance));
                }
            }
        }
        Statement statement2 = conn.createStatement();
        statement2.executeUpdate("update account set balance=101 where id=2;");
        statement2.close();
        Statement statement3 = conn.createStatement();
        ResultSet r3 = statement3.executeQuery("select * from account where id=2");
        if (!r3.next()) {
            logger.error("update run failed, should success");
            throw new Exception("update run failed, should success");
        }
        int balanceend = r3.getInt("balance");
        if (balanceend != 101) {
            logger.error("update run failed, should success");
            throw new Exception("update run failed, should success");
        }
    }

    @Override
    public void initCase() {
        String name = "SetReadOnly";
        String feature = "proxy-transaction";
        String tag = "MySQL";
        String message = "this is a test for set transaction" +
                "1. one DB have only one connection" +
                "2. session A run 'set session transaction read only', close session A" +
                "3. session B run 'update' successful";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
        super.initCase();
    }
}


