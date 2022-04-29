package com.sphereex.cases.traffic.jdbc;

import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@AutoTest
public final class TrafficSetReadOnlyTest extends TrafficBaseTest {
    
    private  final Logger logger = LoggerFactory.getLogger(TrafficSetReadOnlyTest.class);
    
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
            stmt.executeUpdate("delete from t_order;");
            stmt.close();
            stmt1 = conn.createStatement();
            stmt1.executeUpdate("INSERT INTO t_order(user_id, content, creation_date, order_id) VALUES(1, 'test1', NOW(), 1), (1, 'test2', NOW(),2);");
            stmt1.close();
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
        ResultSet rs = statement1.executeQuery("select * from t_order;");
        while (rs.next()) {
            int id = rs.getInt("order_id");
            String content = rs.getString("content");
            if (id == 2) {
                if (!"test2".equals(content)) {
                    logger.error("content is {}, should be test2", content);
                    throw new Exception(String.format("content is {}, should be test2", content));
                }
            }
            if (id == 1) {
                if (!"test1".equals(content)) {
                    logger.error("content is {}, should be test1", content);
                    throw new Exception(String.format("content is {}, should be test1", content));
                }
            }
        }
        Statement statement2 = conn.createStatement();
        try {
            statement2.execute("update t_order set content='test3' where order_id=2;");
            logger.error("update run success, should failed");
            throw new Exception("update run success, should failed");
        } catch (SQLException e) {
            logger.info("update failed for expect");
        }
        statement1.close();
        statement2.close();
        conn.close();
    }
    
    private void step2() throws Exception {
        Connection conn = getAutoDataSource().getConnection();
        Statement statement1 = conn.createStatement();
        ResultSet rs = statement1.executeQuery("select * from t_order");
        while (rs.next()) {
            int id = rs.getInt("order_id");
            String content = rs.getString("content");
            if (id == 2) {
                if (!"test2".equals(content)) {
                    logger.error("content is {}, should be test2", content);
                    throw new Exception(String.format("content is {}, should be test2", content));
                }
            }
            if (id == 1) {
                if (!"test1".equals(content)) {
                    logger.error("content is {}, should be test1", content);
                    throw new Exception(String.format("content is {}, should be test1", content));
                }
            }
        }
        Statement statement2 = conn.createStatement();
        statement2.executeUpdate("update t_order set content='test3' where order_id=2;");
        statement2.close();
        Statement statement3 = conn.createStatement();
        ResultSet r3 = statement3.executeQuery("select * from t_order where order_id=2");
        if (!r3.next()) {
            logger.error("update run failed, should success");
            throw new Exception("update run failed, should success");
        }
        String content = r3.getString("content");
        if (!"test3".equals(content)) {
            logger.error("update run failed, should success");
            throw new Exception("update run failed, should success");
        }
    }
    
    @Override
    public Status end() {
        try {
            Connection conn = getAutoDataSource().getConnection();
            Statement statement = conn.createStatement();
            statement.executeUpdate("delete from t_order;");
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }
    
    @Override
    public void init() {
        String name = getClass().getName();
        String feature = "traffic-transaction";
        String tag = "opengauss";
        String message = "this is a traffic test for set transaction" +
                "1. one DB have only one connection" +
                "2. session A run 'set session transaction read only', close session A" +
                "3. session B run 'update' successful";
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
