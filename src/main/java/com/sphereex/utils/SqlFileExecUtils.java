package com.sphereex.utils;

import com.sphereex.cases.base.item.DBInfo;
import com.sphereex.cases.base.item.DBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class SqlFileExecUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(SqlFileExecUtils.class);
    
    /**
     * Execute sql file.
     * @param fileName
     * @param dbInfo
     * @param dbType
     * @return
     * @throws SQLException
     */
    public static boolean executeSqlFile(final String fileName, final DBInfo dbInfo, final DBType dbType) throws SQLException {
        File file = new File(fileName);
        if (!file.isFile() || !file.exists()) {
            logger.error("File not exist or is not a file, file: {}", fileName);
            return false;
        }
        Scanner reader;
        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException e) {
            logger.error("File not exist, file: {}", fileName);
            return false;
        }
        List<String> sqls = new ArrayList<>();
        while (reader.hasNextLine()) {
            String sql = reader.nextLine();
            sql = sql.trim();
            if (0 == sql.length() || sql.startsWith("#") || sql.startsWith("--")) {
                continue;
            }
            sqls.add(sql);
        }
        return execute((ArrayList<String>) sqls, dbInfo, dbType);
    }
    
    private static boolean execute(final ArrayList<String> sqls, final DBInfo dbInfo, final DBType dbType) throws SQLException {
        Connection connection;
        switch (dbType) {
            case MYSQL:
                try {
                    connection = MySQLUtil.getInstance().getConnection(dbInfo);
                    break;
                } catch (Exception e) {
                    return false;
                }
            case OPENGAUSS:
                try {
                    connection = OpenGaussUtil.getInstance().getConnnection(dbInfo);
                    break;
                } catch (Exception e) {
                    return false;
                }
            case POSTGRESQL:
                try {
                    connection = PostgreSQLUtil.getInstance().getConnnection(dbInfo);
                    break;
                } catch (Exception e) {
                    return false;
                }
            default:
                return false;
        }
        for (String each : sqls) {
            logger.info(each);
            Statement stmt = connection.createStatement();
            stmt.execute(each);
        }
        connection.close();
        return true;
    }
}
