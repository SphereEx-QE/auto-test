package com.sphereex.cases;

import com.sphereex.cases.base.ShardingSphereProxyBaseTest;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import com.sphereex.core.Status;
import com.sphereex.utils.SqlFileExecUtils;
import lombok.Getter;

import java.sql.SQLException;

@AutoTest
public final class Example extends ShardingSphereProxyBaseTest {
    
    @Getter
    private final DBType dbType = DBType.MYSQL;
    
    @Override
    public Status pre() {
        Status s = super.pre();
        if (!s.isSuccess()) {
            return s;
        }
        try {
            SqlFileExecUtils.executeSqlFile(Example.class.getResource("/sql/example.sql").getFile(), getDbInfo(), dbType);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }
    
    @Override
    public Status run(){
        return new Status(true, "");
    }
    
    @Override
    public void init() {
        String name = "Example";
        String feature = "Example";
        String tag = "Example";
        String message = "Example";
        String configPath = "";
        String clientType = "Example";
        caseInfo = new CaseInfo(name, feature, tag, message, DBType.MYSQL, clientType, configPath);
    }
    
    @Override
    public CaseInfo getCaseInfo() {
        if (null == caseInfo) {
            init();
        }
        return caseInfo;
    }
}
