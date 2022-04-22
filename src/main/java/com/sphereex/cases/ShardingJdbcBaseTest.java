package com.sphereex.cases;

import com.sphereex.core.DBType;
import com.sphereex.utils.AutoDataSource;
import com.sphereex.utils.JdbcDataSource;
import lombok.Getter;

public abstract class ShardingJdbcBaseTest extends BaseCaseImpl {
    
    @Getter
    private AutoDataSource autoDataSource;
    
    public ShardingJdbcBaseTest(DBType dbType) throws Exception {
        this.autoDataSource = new JdbcDataSource(dbType);
    }
    
    public ShardingJdbcBaseTest(String yamlFile) throws Exception {
        this.autoDataSource = new JdbcDataSource(yamlFile);
    }
}
