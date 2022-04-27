package com.sphereex.cases.traffic;

import com.sphereex.cases.base.ShardingJdbcBaseTest;
import com.sphereex.cases.base.DBType;
import lombok.Getter;

public abstract class TrafficBaseTest extends ShardingJdbcBaseTest {
    
    @Getter
    private final DBType dbType = DBType.MYSQL;
    
    @Getter
    private final String yamlFile = "conf/case/traffic/config-sharding.yaml";
}
