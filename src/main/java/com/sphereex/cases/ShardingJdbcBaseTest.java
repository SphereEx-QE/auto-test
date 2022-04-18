package com.sphereex.cases;

import lombok.Getter;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;

public abstract class ShardingJdbcBaseTest extends BaseCaseImpl {
    
    private final Logger logger = LoggerFactory.getLogger(ShardingJdbcBaseTest.class);
    
    @Getter
    private DataSource dataSource;
    
    private final String dbType;
    
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
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("read /conf/JdbcMysqlBase/config-sharding.yaml failed.");
            throw new Exception("read /conf/JdbcMysqlBase/config-sharding.yaml failed.");
        }
    }
    
    private void createOpengaussDatasource() throws Exception{
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource("/conf/JdbcOpengaussBase/config-sharding.yaml").getFile()));
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("read /conf/JdbcOpengaussBase/config-sharding.yaml failed.");
            throw new Exception("read /conf/JdbcOpengaussBase/config-sharding.yaml failed.");
        }
    }
    
    private void createPostgresqlDatasource() throws Exception{
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource("/conf/JdbcPostgresqlBase/config-sharding.yaml").getFile()));
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("read /conf/JdbcPostgresqlBase/config-sharding.yaml failed.");
            throw new Exception("read /conf/JdbcPostgresqlBase/config-sharding.yaml failed.");
        }
    }
}
