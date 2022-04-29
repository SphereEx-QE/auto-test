package com.sphereex.cases.traffic.jdbc;

import com.sphereex.cases.base.ShardingSphereJdbcBaseTest;
import com.sphereex.cases.base.item.DBType;
import lombok.Getter;

public abstract class TrafficBaseTest extends ShardingSphereJdbcBaseTest {
    
    @Getter
    private final DBType dbType = DBType.MYSQL;
    
    @Getter
    private final String yamlFile = "conf/case/traffic/jdbc/config-sharding.yaml";
}
