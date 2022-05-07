package com.sphereex.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class CaseInfo {

    private final String name;

    private final String feature;

    private final String tag;

    private final String message;
    
    private final DBType dbType;
    
    private final String clientType;
    
    private final String configPath;
}
