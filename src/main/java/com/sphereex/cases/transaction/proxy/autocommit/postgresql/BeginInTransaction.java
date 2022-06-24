package com.sphereex.cases.transaction.proxy.autocommit.postgresql;

import com.sphereex.cases.base.ShardingSphereProxyBaseTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import com.sphereex.core.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@AutoTest
public final class BeginInTransaction extends ShardingSphereProxyBaseTest {
    
    private Connection conn1;

    private  final Logger logger = LoggerFactory.getLogger(BeginInTransaction.class);
    
    @Override
    public Status pre() {
        Status s = super.pre();
        if (!s.isSuccess()) {
            return s;
        }
        try {
            conn1 = getAutoDataSource().getConnection();
            conn1.createStatement().execute("delete from account;");
        } catch (SQLException e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true,"");
    }

    @Override
    public Status run() {
        try {
            innerRun();
            return new Status(false, "there should report exception.");
        } catch (Exception e) {
            if (!"WARNING: There is already a transaction in progress.".equals(e.getMessage())) {
                return new Status(false, e.getMessage());
            }
        } finally {
            try {
                conn1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new Status(true, "");
    }
    
    private void innerRun() throws SQLException {
        conn1.setAutoCommit(false);
        Statement stmt1 = conn1.createStatement();
        stmt1.execute("select * from account;");
        Statement stmt2 = conn1.createStatement();
        stmt2.execute("begin;");
    }

    @Override
    public Status end() {
        try {
            Connection conn = getAutoDataSource().getConnection();
            conn.createStatement().execute("delete from account");
        } catch (SQLException e) {
            e.printStackTrace();;
            return new Status(false, e.getMessage());
        }
        return super.end();
    }
    
    @Override
    public void init() {
        String name = getClass().getSimpleName();
        String feature = "transaction";
        String tag = "BeginInTransaction";
        String message = "";
        String configPath = "conf/case/transaction/proxy/savepoint/postgresql";
        String clientType = "proxy";
        caseInfo = new CaseInfo(name, feature, tag, message, DBType.POSTGRESQL, clientType, configPath);
    }
    
    @Override
    public CaseInfo getCaseInfo() {
        if (null == caseInfo) {
            init();
        }
        return caseInfo;
    }
}
