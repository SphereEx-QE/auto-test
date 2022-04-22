package com.sphereex.utils;

import com.sphereex.core.DBInfo;
import com.sphereex.core.DBType;

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
                case OPENGAUSS:
                    connection = OpenGaussUtil.getInstance().getConnnection(dbInfo);
                case POSTGRESQL:
                    connection = PostgreSQLUtil.getInstance().getConnnection(dbInfo);
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
