package com.sphereex.cases.transaction.proxy.classictransfer.opengauss;

import com.sphereex.cases.base.ShardingSphereProxyBaseTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import com.sphereex.core.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

@AutoTest
public final class ClassicTransfer extends ShardingSphereProxyBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassicTransfer.class);

    @Override
    public Status pre() {
        Status s = super.pre();
        if (!s.isSuccess()) {
            return s;
        }
        try {
            Statement stmt;
            Connection conn = getAutoDataSource().getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("drop table if exists account;create table account(id text , balance float ,transaction_id int);");
            stmt.executeUpdate("insert into account(transaction_id,balance) values (1,0),(2,100);");
        } catch (SQLException e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }

    @Override
    public Status run() {
        try {
            innerRun();
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }
    
    private void innerRun() throws Exception {
        List<Thread> tasks = new LinkedList<>();
        for (int i=0; i<20;i++) {
            Thread t = new UpdateTread();
            t.start();
            tasks.add(t);
            int sum = getBalanceSum();
            if (100 != sum) {
                logger.error("balance sum is {}, should be 100", sum);
                throw new Exception(String.format("balance sum is {}, should be 100", sum));
            }
        }
        Thread.sleep(3000);
        int sum = getBalanceSum();
        if (100 != sum) {
            
            logger.error("balance sum is {}, should be 100", sum);
            throw new Exception(String.format("balance sum is {}, should be 100", sum));
        }
        for (Thread task: tasks) {
            task.join();
        }
    }
    
    @Override
    public void init() {
        String name = getClass().getSimpleName();
        String feature = "transaction";
        String tag = "classictransfer";
        String message = "this is a test for classic transfer" +
                "1. 20 treads execute transfer operations concurrently" +
                "2. Randomly query the sum of balance to verify whether the data is consistent";
        String configPath = "conf/case/transaction/proxy/classictransfer/opengauss";
        String clientType = "proxy";
        caseInfo = new CaseInfo(name, feature, tag, message, DBType.OPENGAUSS, clientType, configPath);
    }
    
    @Override
    public CaseInfo getCaseInfo() {
        if (null == caseInfo) {
           init();
        }
        return caseInfo;
    }
    
    int getBalanceSum() throws Exception{
        int result = 0;
        Connection connection = getAutoDataSource().getConnection();

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
                connection = getAutoDataSource().getConnection();
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
