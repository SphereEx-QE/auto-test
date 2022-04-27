package com.sphereex.core;

public interface Case {
    Status pre();

    Status run();

    Status end();

    CaseInfo init();
    
    CaseInfo getCaseInfo();
    
    void setCaseInfo(CaseInfo caseInfo);
}
