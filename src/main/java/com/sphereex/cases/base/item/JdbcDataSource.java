package com.sphereex.cases.base.item;

import com.sphereex.core.DBType;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public final class JdbcDataSource extends AutoDataSource {
    
    private final Logger logger = LoggerFactory.getLogger(JdbcDataSource.class);
    
    private DataSource dataSource;
    
    public JdbcDataSource(DBType dbType) throws Exception {
        createDatasource(dbType);
    }
    
    public JdbcDataSource(String yamlFile) throws Exception {
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource(yamlFile).getFile()));
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("read {} failed.", yamlFile);
            throw new Exception(String.format("read %s failed.", yamlFile));
        }
    }
    
    private void createDatasource(DBType dbType) throws Exception {
        switch (dbType) {
            case MYSQL:
                createMysqlDatasource();
                break;
            case OPENGAUSS:
                createOpengaussDatasource();
                break;
            case POSTGRESQL:
                createPostgresqlDatasource();
                break;
            default:
                throw new Exception(String.format("this dbtype:%s not support yet", dbType));
        }
    }
    
    
    private void createMysqlDatasource() throws Exception{
        String filePath = "/conf/default/jdbc/mysql/config-sharding.yaml";
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource(filePath).getFile()));
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("read {} failed.", filePath);
            throw new Exception(String.format("read %s failed.", filePath));
        }
    }
    
    private void createOpengaussDatasource() throws Exception{
        String filePath = "/conf/default/jdbc/opengauss/config-sharding.yaml";
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource(filePath).getFile()));
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("read {} failed.", filePath);
            throw new Exception(String.format("read %s failed.", filePath));
        }
    }
    
    private void createPostgresqlDatasource() throws Exception{
        String filePath = "/conf/default/jdbc/postgresql/config-sharding.yaml";
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(this.getClass().getResource(filePath).getFile()));
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("read {} failed.", filePath);
            throw new Exception(String.format("read %s failed.", filePath));
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        Connection result = dataSource.getConnection();
        getConnectionCache().add(result);
        return result;
    }
    
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection result = dataSource.getConnection(username, password);
        getConnectionCache().add(result);
        return result;
    }
    
}
