package com.sphereex.cases.transaction.case1;

import com.sphereex.cases.BaseCaseImpl;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBInfo;
import com.sphereex.utils.MySQLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

@AutoTest
public class Case1 extends BaseCaseImpl {

    private Connection conn1;

    private Connection conn2;

    private  final Logger logger = LoggerFactory.getLogger(Case1.class);

    @Override
    public void pre() throws ClassNotFoundException, SQLException {
        DBInfo dbInfo = Objects.requireNonNull(getDbInfo());
        conn1 = MySQLUtil.getInstance().getConnnection(dbInfo);
        conn2 = MySQLUtil.getInstance().getConnnection(dbInfo);
    }

    @Override
    public void run() throws SQLException {
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
            return;
        }
        conn1.createStatement().execute("commit;");

        ResultSet result2 = conn2.createStatement().executeQuery("select * from account");

        if (!result2.next()) {
            logger.error("there should be result");
            return;
        }
        getCaseInfo().setStatus(true);
    }

    @Override
    public void end() throws SQLException, ClassNotFoundException {
        Connection conn3 = MySQLUtil.getInstance().getConnnection(getDbInfo());
        conn3.createStatement().execute("delete from account");
        conn3.close();
        conn1.close();
        conn2.close();
    }
    
    @Override
    public void initCaseInfo() {
        String name = "case1";
        String feature = "transaction";
        String tag = "MySQL";
        String message = "this is a test for mysql store" +
                "1. session A ,run set autocommit=0 and insert ,now session B can not see the insert data" +
                "2. session A run commit, then session B can see the insert data";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
