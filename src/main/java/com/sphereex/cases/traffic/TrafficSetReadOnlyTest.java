package com.sphereex.cases.traffic;

import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@AutoTest
public class TrafficSetReadOnlyTest extends TrafficBaseTest {
    
    private  final Logger logger = LoggerFactory.getLogger(TrafficSetReadOnlyTest.class);
    
    @Override
    public void pre() throws Exception {
        super.pre();
        Connection conn = getDataSource().getConnection();
        Statement stmt;
        Statement stmt1;
        stmt = conn.createStatement();
        stmt.executeUpdate("delete from t_order;");
        stmt.close();
        stmt1 = conn.createStatement();
        stmt1.executeUpdate("INSERT INTO t_order(user_id, content, creation_date, order_id) VALUES(1, 'test1', NOW(), 1), (1, 'test2', NOW(),2);");
        stmt1.close();
        conn.close();
    }
    
    @Override
    public boolean run() throws Exception {
        boolean r1 = step1();
        boolean r2 = step2();
        if (!r1 || !r2) {
            return false;
        }
        return true;
    }
    
    private boolean step1() throws Exception {
        Connection conn = getDataSource().getConnection();
        conn.setReadOnly(true);
        Statement statement1 = conn.createStatement();
        ResultSet rs = statement1.executeQuery("select * from t_order;");
        while (rs.next()) {
            int id = rs.getInt("order_id");
            String content = rs.getString("content");
            if (id == 2) {
                if (!"test2".equals(content)) {
                    logger.error("content is {}, should be test2", content);
                    return false;
                }
            }
            if (id == 1) {
                if (!"test1".equals(content)) {
                    logger.error("content is {}, should be test1", content);
                    return false;
                }
            }
        }
        Statement statement2 = conn.createStatement();
        try {
            statement2.execute("update t_order set content='test3' where order_id=2;");
            logger.error("update run success, should failed");
            return false;
        } catch (SQLException e) {
            logger.info("update failed for expect");
        }
        statement1.close();
        statement2.close();
        conn.close();
        return true;
    }
    
    private boolean step2() throws Exception {
        Connection conn = getDataSource().getConnection();
        Statement statement1 = conn.createStatement();
        ResultSet rs = statement1.executeQuery("select * from t_order");
        while (rs.next()) {
            int id = rs.getInt("order_id");
            String content = rs.getString("content");
            if (id == 2) {
                if (!"test2".equals(content)) {
                    logger.error("content is {}, should be test2", content);
                    return false;
                }
            }
            if (id == 1) {
                if (!"test1".equals(content)) {
                    logger.error("content is {}, should be test1", content);
                    return false;
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
            return false;
        }
        String content = r3.getString("content");
        if (!"test3".equals(content)) {
            logger.error("update run failed, should success");
            return false;
        }
        return true;
    }
    
    @Override
    public void end() throws SQLException {
        Connection conn = getDataSource().getConnection();
        Statement statement = conn.createStatement();
        statement.executeUpdate("delete from t_order;");
        statement.close();
        conn.close();
    }
    
    @Override
    public void initCaseInfo() {
        String name = "TrafficSetReadOnlyTest";
        String feature = "traffic-transaction";
        String tag = "opengauss";
        String message = "this is a traffic test for set transaction" +
                "1. one DB have only one connection" +
                "2. session A run 'set session transaction read only', close session A" +
                "3. session B run 'update' successful";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
