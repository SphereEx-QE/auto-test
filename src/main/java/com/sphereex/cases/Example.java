package com.sphereex.cases;

import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import com.sphereex.core.Status;
import com.sphereex.utils.SqlFileExecUtils;
import lombok.Getter;

import java.sql.SQLException;

@AutoTest
public final class Example extends ProxyBaseTest{
    
    @Getter
    private final DBType dbType = DBType.MYSQL;
    
    @Override
    public Status pre() {
        Status s = super.pre();
        if (!s.isSuccess()) {
            return s;
        }
        try {
            SqlFileExecUtils.executeSqlFile(Example.class.getResource("/example.sql").getFile(), getDbInfo(), dbType);
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
    public void initCase() {
        String name = "Example";
        String feature = "Example";
        String tag = "Example";
        String message = "Example";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
        super.initCase();
    }
}
