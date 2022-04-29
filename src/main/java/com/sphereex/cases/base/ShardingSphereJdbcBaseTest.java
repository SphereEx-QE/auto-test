package com.sphereex.cases.base;

import com.sphereex.cases.base.item.AutoDataSource;
import com.sphereex.cases.base.item.DBType;
import com.sphereex.cases.base.item.JdbcDataSource;
import com.sphereex.core.Status;
import lombok.Getter;

public abstract class ShardingSphereJdbcBaseTest extends BaseCaseImpl {
    
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
