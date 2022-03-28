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
    
    private String dbType;
    
    public ShardingJdbcBaseTest(String dbType) {
        this.dbType = dbType;
    }
    
    @Override
    public void pre() throws Exception {
        switch (dbType) {
            case "mysql":
                createMysqlDatasource();
                break;
            case "opengauss":
                createOpengaussDatasource();
                break;
            case "postgresql":
                createPostgresqlDatasource();
                break;
            default:
                throw new Exception(String.format("this dbtype:%s not support yet", dbType));
        }
    }
    
    private void createMysqlDatasource() throws Exception{
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource("/conf/JdbcMysqlBase/config-sharding.yaml").getFile()));
        } catch (IOException exception) {
            logger.error("/conf/JdbcMysqlBase/config-sharding.yaml not exist.");
            throw new Exception("/conf/JdbcMysqlBase/config-sharding.yaml not exist.");
        }
    }
    
    private void createOpengaussDatasource() throws Exception{
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource("/conf/JdbcOpengaussBase/config-sharding.yaml").getFile()));
        } catch (IOException exception) {
            logger.error("/conf/JdbcOpengaussBase/config-sharding.yaml not exist.");
            throw new Exception("/conf/JdbcOpengaussBase/config-sharding.yaml not exist.");
        }
    }
    
    private void createPostgresqlDatasource() throws Exception{
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource("/conf/JdbcPostgresqlBase/config-sharding.yaml").getFile()));
        } catch (IOException exception) {
            logger.error("/conf/JdbcPostgresqlBase/config-sharding.yaml not exist.");
            throw new Exception("/conf/JdbcPostgresqlBase/config-sharding.yaml not exist.");
        }
    }
    
    @Override
    public void end() throws SQLException {
        getCaseInfo().setStatus(true);
    }
}
