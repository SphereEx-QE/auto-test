package com.sphereex.core;

public interface Case {
    Status pre();

    Status run();

    Status end();

    Status start();
    
    void initCase();
    
    boolean isValid();

    CaseInfo getCaseInfo();
}
