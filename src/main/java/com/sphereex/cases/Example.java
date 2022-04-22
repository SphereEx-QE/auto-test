package com.sphereex.cases;

import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBType;
import com.sphereex.utils.SqlFileExecUtils;

@AutoTest
public final class Example extends BaseCaseImpl{
    @Override
    public void pre() throws Exception {
        SqlFileExecUtils.executeSqlFile(Example.class.getResource("/example.sql").getFile(), getDbInfo(), DBType.MYSQL);
    }
    
    @Override
    public boolean run() throws Exception {
        return true;
    }
    
    @Override
    public void end() throws Exception {
    }
    
    @Override
    public void initCaseInfo() {
        String name = "Example";
        String feature = "Example";
        String tag = "Example";
        String message = "Example";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
