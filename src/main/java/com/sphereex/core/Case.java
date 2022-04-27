package com.sphereex.core;

public interface Case {
    Status pre();

    Status run();

    Status end();

//    Status start();
    
//    void initCase();
    CaseInfo init();
    
//    boolean isValid();

    CaseInfo getCaseInfo();
    
    void setCaseInfo(CaseInfo caseInfo);
}
