package com.sphereex.cases.traffic;

import com.sphereex.cases.ShardingJdbcBaseTest;

public abstract class TrafficBaseTest extends ShardingJdbcBaseTest {
    
    public TrafficBaseTest() throws Exception {
        super("/traffic/config-sharding.yaml");
    }
}
