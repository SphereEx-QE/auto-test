package com.sphereex.cases.proxy.transaction.autocommit;

import com.sphereex.cases.ProxyBaseTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import com.sphereex.utils.MySQLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@AutoTest
public final class AutoCommit extends ProxyBaseTest {
    
    private Connection conn1;

    private Connection conn2;

    private  final Logger logger = LoggerFactory.getLogger(AutoCommit.class);
    
    public AutoCommit() {
        super(DBType.MYSQL);
    }
    
    @Override
    public void pre() throws ClassNotFoundException, SQLException {
        conn1 = getAutodataSource().getConnection();
        conn2 = getAutodataSource().getConnection();
    }

    @Override
    public boolean run() throws SQLException {
        Statement stmt1 = conn1.createStatement();
        stmt1.execute("set session transaction isolation level read committed;");

        Statement stmt2 = conn2.createStatement();
        stmt2.execute("set session transaction isolation level read committed;");

        conn1.createStatement().execute("set autocommit=0");

        conn2.createStatement().execute("begin;");

        conn1.createStatement().execute("insert into account values(1,'lu',100)");

        ResultSet result1 = conn2.createStatement().executeQuery("select * from account;");

        if (result1.next()) {
            logger.error("there should not be result");
            return false;
        }
        conn1.createStatement().execute("commit;");

        ResultSet result2 = conn2.createStatement().executeQuery("select * from account");

        if (!result2.next()) {
            logger.error("there should be result");
            return false;
        }
        return true;
    }

    @Override
    public void end() throws SQLException, ClassNotFoundException {
        Connection conn3 = MySQLUtil.getInstance().getConnection(getDbInfo());
        conn3.createStatement().execute("delete from account");
        conn3.close();
        conn1.close();
        conn2.close();
    }
    
    @Override
    public void initCaseInfo() {
        String name = "AutoCommit";
        String feature = "proxy-transaction";
        String tag = "MySQL";
        String message = "this is a test for mysql store" +
                "1. session A ,run set autocommit=0 and insert ,now session B can not see the insert data" +
                "2. session A run commit, then session B can see the insert data";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
