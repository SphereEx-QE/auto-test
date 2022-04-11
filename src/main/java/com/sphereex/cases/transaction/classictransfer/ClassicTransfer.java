package com.sphereex.cases.transaction.classictransfer;

import com.sphereex.cases.BaseCaseImpl;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

@AutoTest
public class ClassicTransfer extends BaseCaseImpl {

    private static final Logger logger = LoggerFactory.getLogger(ClassicTransfer.class);

    private String jUrl;

    @Override
    public void pre() throws Exception {
        jUrl = String.format("jdbc:opengauss://%s:%d/%s", getDbInfo().getIp(), getDbInfo().getPort(), getDbInfo().getDbName());
        Statement stmt;
        Connection conn = DriverManager.getConnection(jUrl, getDbInfo().getUser(), getDbInfo().getPassword());
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists account;create table account(id text , balance float ,transaction_id int);");
        stmt.executeUpdate("insert into account(transaction_id,balance) values (1,0),(2,100);");
        stmt.close();
        conn.close();
    }

    @Override
    public void run() throws Exception {
        List<Thread> tasks = new LinkedList<>();
        for (int i=0; i<20;i++) {
            Thread t = new UpdateTread();
            t.start();
            tasks.add(t);
            Thread.sleep(100);
            int sum = getBalanceSum();
            if (100 != sum) {
                throw new Exception(String.format("balance sum is %d, should be 100", sum));
            }
        }
        Thread.sleep(3000);
        int sum = getBalanceSum();
        if (100 != sum) {
            throw new Exception(String.format("balance sum is %d, should be 100", sum));
        }
        for (Thread task: tasks) {
            task.join();
        }
        getCaseInfo().setStatus(true);
    }
    
    @Override
    public void initCaseInfo() {
        String name = "ClassicTransfer";
        String feature = "transaction";
        String tag = "OpenGauss";
        String message = "this is a test for classic transfer" +
                "1. 20 treads execute transfer operations concurrently" +
                "2. Randomly query the sum of balance to verify whether the data is consistent";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
    
    int getBalanceSum() throws Exception{
        int result = 0;
        Connection connection = DriverManager.getConnection(jUrl, getDbInfo().getUser(), getDbInfo().getPassword());

        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select sum(balance) as a from account where transaction_id in (1,2)");
        if (resultSet.next()) {
            result = resultSet.getInt(1);
        }
        connection.commit();
        statement.close();
        connection.close();
        return result;
    }

    class UpdateTread extends Thread {
        public void run() {
             Connection connection = null;
             Statement statement1 = null;
             Statement statement2 = null;
            try {
                connection = DriverManager.getConnection(jUrl, getDbInfo().getUser(), getDbInfo().getPassword());
                connection.setAutoCommit(false);
                statement1 = connection.createStatement();
                statement1.execute("update account set balance=balance-1 where transaction_id=2;");
                statement2 = connection.createStatement();
                Thread.sleep(1000);
                statement2.execute("update account set balance=balance+1 where transaction_id=1;");
                connection.commit();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    statement1.close();
                    statement2.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


