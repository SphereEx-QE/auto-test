package com.sphereex.core;

public interface Case {
    
    Status pre();

    Status run();

    Status end();

    void init();
    
    CaseInfo getCaseInfo();
}
