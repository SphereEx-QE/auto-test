package com.sphereex.cases;

import com.sphereex.core.DBInfo;
import com.sphereex.core.DBType;
import com.sphereex.core.Status;
import com.sphereex.utils.AutoDataSource;
import com.sphereex.utils.ProxyDataSource;
import lombok.Getter;

@Getter
public abstract class ProxyBaseTest extends BaseCaseImpl {
    
    private AutoDataSource autoDataSource;
    
    private DBInfo dbInfo;
    
    protected abstract DBType getDbType();
    
    @Override
    public Status pre(){
        autoDataSource = new ProxyDataSource(getDbInfo(), getDbType());
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
    
    @Override
    public void initCase() {
        String ip = System.getProperty("ip");
        String port = System.getProperty("port");
        String dbName = System.getProperty("dbname");
        String user = System.getProperty("user");
        String password = System.getProperty("password");
        if (null != ip && null != port && null != dbName && null != user && null != password) {
            dbInfo = new DBInfo(ip, Integer.parseInt(port), user, password, dbName);
        }
    }
}
