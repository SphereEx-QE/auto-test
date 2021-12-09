package com.sphereex.utils;

import com.sphereex.core.DBInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLUtil {

    private volatile static MySQLUtil instance;

    private MySQLUtil() throws ClassNotFoundException {
//        Class.forName("com.mysql.jdbc.Driver");
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

    public Connection getConnnection(DBInfo dbInfo) throws SQLException {
        Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true", dbInfo.getIp(), dbInfo.getPort(), dbInfo.getDbName()), dbInfo.getUser(), dbInfo.getPassword());
        return conn;
    }
}
