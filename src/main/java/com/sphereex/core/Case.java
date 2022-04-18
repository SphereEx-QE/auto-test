package com.sphereex.core;

public interface Case {
    void pre() throws Exception;

    boolean run() throws Exception;

    void end() throws Exception;

    boolean start() throws Exception;
    
    void initCaseInfo();
    
    boolean caseInfoIsNull();

    CaseInfo getCaseInfo();

    void setDbInfo(DBInfo dbInfo);
}
