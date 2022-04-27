package com.sphereex.utils;

import com.sphereex.cases.base.DBInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class OpenGaussUtil {

    private volatile static OpenGaussUtil instance;

    private OpenGaussUtil() throws ClassNotFoundException {
        Class.forName("org.opengauss.Driver");
    }

    public static OpenGaussUtil getInstance() throws ClassNotFoundException {
        if (null == instance) {
            synchronized (OpenGaussUtil.class) {
                if (null == instance) {
                    instance = new OpenGaussUtil();
                }
            }
        }
        return instance;
    }

    public Connection getConnnection(DBInfo dbInfo) throws SQLException {
        Connection conn = DriverManager.getConnection(String.format("jdbc:opengauss://%s:%d/%s", dbInfo.getIp(), dbInfo.getPort(), dbInfo.getDbName()), dbInfo.getUser(), dbInfo.getPassword());
        return conn;
    }
}
