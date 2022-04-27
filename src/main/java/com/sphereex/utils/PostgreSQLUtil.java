package com.sphereex.utils;

import com.sphereex.cases.base.DBInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class PostgreSQLUtil {

    private volatile static PostgreSQLUtil instance;

    private PostgreSQLUtil() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }

    public static PostgreSQLUtil getInstance() throws ClassNotFoundException {
        if (null == instance) {
            synchronized (PostgreSQLUtil.class) {
                if (null == instance) {
                    instance = new PostgreSQLUtil();
                }
            }
        }
        return instance;
    }

    public Connection getConnnection(DBInfo dbInfo) throws SQLException {
        Connection conn = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%d/%s", dbInfo.getIp(), dbInfo.getPort(), dbInfo.getDbName()), dbInfo.getUser(), dbInfo.getPassword());
        return conn;
    }
}
