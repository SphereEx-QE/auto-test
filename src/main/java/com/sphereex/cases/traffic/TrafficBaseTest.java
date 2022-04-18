package com.sphereex.cases.traffic;

import com.sphereex.cases.BaseCaseImpl;
import lombok.Getter;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;

public abstract class TrafficBaseTest extends BaseCaseImpl {
    
    private final Logger logger = LoggerFactory.getLogger(TrafficBaseTest.class);
    
    @Getter
    private DataSource dataSource;
    
    @Override
    public void pre() throws Exception {
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource("/traffic/config-sharding.yaml").getFile()));
        } catch (Exception e) {
            logger.error(" read /traffic/config-sharding.yaml failed.");
            e.printStackTrace();
            throw new Exception("read /traffic/config-sharding.yaml failed.");
        }
    }
}
