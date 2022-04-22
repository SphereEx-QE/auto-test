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
public final class TrafficOpengaussAutocommit extends TrafficBaseTest {

    private  final Logger logger = LoggerFactory.getLogger(TrafficOpengaussAutocommit.class);
    
    public TrafficOpengaussAutocommit() throws Exception {
        super();
    }
    
    @Override
    public void pre() throws Exception {
        Connection conn = getAutoDataSource().getConnection();
        Statement stmt;
        stmt = conn.createStatement();
        stmt.executeUpdate("delete from t_order;");
        stmt.close();
        conn.close();
    }

    @Override
    public boolean run() throws SQLException {
        Connection conn1 = getAutoDataSource().getConnection();
        Connection conn2 = getAutoDataSource().getConnection();
        
        Statement stmt1 = conn1.createStatement();
        stmt1.execute("set transaction isolation level read committed;");

        Statement stmt2 = conn2.createStatement();
        stmt2.execute("set transaction isolation level read committed;");

        conn1.setAutoCommit(false);

        conn2.setAutoCommit(false);

        conn1.createStatement().execute("INSERT INTO t_order(user_id, content, creation_date, order_id) VALUES(1, 'test1', NOW(), 1)");

        ResultSet result1 = conn2.createStatement().executeQuery("select * from t_order;");

        if (result1.next()) {
            logger.error("there should not be result");
            return false;
        }
        try {
            conn1.createStatement().execute("begin;");
        } catch (Exception e) {
            logger.info("report exception for expect.");
        }
        conn1.commit();
    
        ResultSet result2 = conn2.createStatement().executeQuery("select * from t_order");

        if (!result2.next()) {
            logger.error("there should be result");
            return false;
        }
        conn1.close();
        conn2.close();
        return true;
    }

    @Override
    public void end() throws SQLException {
        Connection conn = getAutoDataSource().getConnection();
        conn.createStatement().execute("delete from t_order");
        conn.close();
    }
    
    @Override
    public void initCaseInfo() {
        String name = "TrafficOpengaussAutocommit";
        String feature = "traffic-transaction";
        String tag = "autocommit";
        String message = "this is a traffic test for set transaction" +
                "1. session A ,run set autocommit=0 and insert ,now session B can not see the insert data" +
                "2. session A run begin, then session B can see the insert data";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
