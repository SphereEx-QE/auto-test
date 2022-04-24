package com.sphereex.cases;

import com.sphereex.core.DBType;
import com.sphereex.core.Status;
import com.sphereex.utils.AutoDataSource;
import com.sphereex.utils.JdbcDataSource;
import lombok.Getter;

public abstract class ShardingJdbcBaseTest extends BaseCaseImpl {
    
    protected abstract DBType getDbType();
    
    protected abstract String getYamlFile();
    
    @Getter
    private AutoDataSource autoDataSource;
    
    @Override
    public Status pre() {
        try {
            if (null != getYamlFile()) {
                autoDataSource = new JdbcDataSource(getYamlFile());
            } else if (null != getDbType()) {
                autoDataSource = new JdbcDataSource(getDbType());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }
    
    @Override
    public Status end() {
        try {
            autoDataSource.close();
        } catch (Exception e) {
            return new Status(false, "connection close failed");
        }
        return new Status(true, "");
    }
}
