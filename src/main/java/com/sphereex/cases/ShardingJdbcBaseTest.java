package com.sphereex.cases;

import lombok.Getter;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ShardingJdbcBaseTest extends BaseCaseImpl {
    
    private final Logger logger = LoggerFactory.getLogger(ShardingJdbcBaseTest.class);
    
    @Getter
    private DataSource dataSource;
    
    @Override
    public void pre() throws Exception {
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource("/JdbcOpengaussBase/config-sharding.yaml").getFile()));
        } catch (IOException exception) {
            logger.error("/JdbcOpengaussBase/config-sharding.yaml not exist.");
            throw new Exception("/JdbcOpengaussBase/config-sharding.yaml not exist.");
        }
    }
    
    @Override
    public void end() throws SQLException {
        getCaseInfo().setStatus(true);
    }
}
