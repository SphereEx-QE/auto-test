package com.sphereex.cases.base.item;

import com.sphereex.core.DBType;
import com.sphereex.utils.MySQLUtil;
import com.sphereex.utils.OpenGaussUtil;
import com.sphereex.utils.PostgreSQLUtil;

import java.sql.Connection;
import java.sql.SQLException;

public final class ProxyDataSource extends AutoDataSource {
    
    private DBInfo dbInfo;
    
    private final DBType dbType;
    
    public ProxyDataSource(DBInfo dbInfo, DBType dbType) {
        this.dbInfo = dbInfo;
        this.dbType = dbType;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return createConnection();
    }
    
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        dbInfo = new DBInfo(dbInfo.getIp(), dbInfo.getPort(), username, password,dbInfo.getDbName());
        return createConnection();
    }
    
    private Connection createConnection() throws SQLException {
        Connection connection = null;
        try {
            switch (dbType) {
                case MYSQL:
                    connection = MySQLUtil.getInstance().getConnection(dbInfo);
                    break;
                case OPENGAUSS:
                    connection = OpenGaussUtil.getInstance().getConnnection(dbInfo);
                    break;
                case POSTGRESQL:
                    connection = PostgreSQLUtil.getInstance().getConnnection(dbInfo);
                    break;
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("DB driver class not found.");
        }
        if (null == connection) {
            throw new SQLException("Get connection failed.");
        }
        getConnectionCache().add(connection);
        return connection;
    }
}
