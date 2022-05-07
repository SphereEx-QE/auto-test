package com.sphereex.cases.traffic.jdbc;

import com.sphereex.cases.base.ShardingSphereJdbcBaseTest;
import lombok.Getter;

public abstract class TrafficBaseTest extends ShardingSphereJdbcBaseTest {
    
    @Getter
    private final String yamlFile = "conf/case/traffic/jdbc/config-sharding.yaml";
}
