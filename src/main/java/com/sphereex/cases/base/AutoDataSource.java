package com.sphereex.cases.base;

import lombok.Getter;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public abstract class AutoDataSource implements DataSource {
    
    @Getter
    private final Set<Connection> connectionCache = new HashSet<Connection>();
    
    public void close() throws SQLException {
        Iterator iterator = connectionCache.iterator();
        while (iterator.hasNext()) {
            Connection connection = (Connection) iterator.next();
            if (!connection.isClosed()) {
                connection.close();
            }
        }
        connectionCache.clear();
    }
    
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }
    
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
    
    }
    
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
