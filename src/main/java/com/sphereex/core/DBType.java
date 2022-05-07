package com.sphereex.core;

public enum DBType {
    
    MYSQL("MYSQL"),
    
    POSTGRESQL("POSTGRESQL"),
    
    OPENGAUSS("OPENGAUSS"),
    
    SQLSERVER("SQLSERVER");
    
    private final String type;
    
    DBType(String type) {
        this.type = type;
    }
}
