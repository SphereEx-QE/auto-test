package com.sphereex.cases.traffic;

import com.sphereex.cases.ShardingJdbcBaseTest;
import com.sphereex.core.DBType;
import lombok.Getter;

public abstract class TrafficBaseTest extends ShardingJdbcBaseTest {
    
    @Getter
    private final DBType dbType = DBType.MYSQL;
    
    @Getter
    private final String yamlFile = "/traffic/config-sharding.yaml";
}
