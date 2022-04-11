package com.sphereex.core;

public interface Case {
    void pre() throws Exception;

    void run() throws Exception;

    void end() throws Exception;

    void start() throws Exception;
    
    void initCaseInfo();
    
    boolean caseInfoIsNull();

    CaseInfo getCaseInfo();

    void setDbInfo(DBInfo dbInfo);
}
