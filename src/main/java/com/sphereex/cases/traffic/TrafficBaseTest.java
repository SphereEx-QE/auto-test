package com.sphereex.cases.traffic;

import com.sphereex.cases.BaseCaseImpl;
import lombok.Getter;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

public abstract class TrafficBaseTest extends BaseCaseImpl {
    
    private final Logger logger = LoggerFactory.getLogger(TrafficBaseTest.class);
    
    @Getter
    private DataSource dataSource;
    
    @Override
    public void pre() throws Exception {
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource("/traffic/config-sharding.yaml").getFile()));
        } catch (IOException exception) {
            logger.error("/traffic/config-sharding.yaml not exist.");
            throw new Exception("/traffic/config-sharding.yaml not exist.");
        }
    }
}
