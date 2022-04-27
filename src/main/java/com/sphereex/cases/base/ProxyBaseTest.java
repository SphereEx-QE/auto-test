package com.sphereex.cases.base;

import com.sphereex.core.Status;
import lombok.Getter;

@Getter
public abstract class ProxyBaseTest extends BaseCaseImpl {
    
    private AutoDataSource autoDataSource;
    
    private DBInfo dbInfo;
    
    protected abstract DBType getDbType();
    
    public ProxyBaseTest() {
        String ip = System.getProperty("ip");
        String port = System.getProperty("port");
        String dbName = System.getProperty("dbname");
        String user = System.getProperty("user");
        String password = System.getProperty("password");
        if (null != ip && null != port && null != dbName && null != user && null != password) {
            dbInfo = new DBInfo(ip, Integer.parseInt(port), user, password, dbName);
        }
    }
    
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
}
