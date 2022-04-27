package com.sphereex.utils;

import com.sphereex.cases.base.DBInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class MySQLUtil {

    private volatile static MySQLUtil instance;

    private MySQLUtil() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    public static MySQLUtil getInstance() throws ClassNotFoundException {
        if (null == instance) {
            synchronized (MySQLUtil.class) {
                if (null == instance) {
                    instance = new MySQLUtil();
                }
            }
        }
        return instance;
    }

    public Connection getConnection(DBInfo dbInfo) throws SQLException {
        return DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s?serverTimezone=UTC&useSSL=false", dbInfo.getIp(), dbInfo.getPort(), dbInfo.getDbName()), dbInfo.getUser(), dbInfo.getPassword());
    }
}
