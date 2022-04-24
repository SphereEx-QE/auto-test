/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sphereex.cases.lock.proxy.stopwrite;

import com.sphereex.cases.BaseCaseImpl;
import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBInfo;
import com.sphereex.core.Status;
import com.sphereex.utils.MySQLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

@AutoTest
public class SchemaStopWritingTest extends BaseCaseImpl {

    private static final Logger logger = LoggerFactory.getLogger(SchemaStopWritingTest.class);

    private static final Map<String, Connection> CONNECTIONS = new LinkedHashMap<>();

    static {
        try {
            CONNECTIONS.put("13306", MySQLUtil.getInstance().getConnection(new DBInfo("127.0.0.1", 13306, "root", "root", "")));
            CONNECTIONS.put("3307", MySQLUtil.getInstance().getConnection(new DBInfo("127.0.0.1", 3307, "root", "root", "scaling_db")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Status pre() {
        try {
            sourcePreProxy(CONNECTIONS.get("3307"));
            targetPreMySQL(CONNECTIONS.get("13306"));
            targetPreProxy(CONNECTIONS.get("3307"));
        } catch (SQLException e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
        
    }

    private void sourcePreProxy(final Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        // add rule
        statement.execute("CREATE SHARDING TABLE RULE t_order(RESOURCES(ds_0,ds_1),SHARDING_COLUMN=order_id,TYPE(NAME=hash_mod,PROPERTIES(\"sharding-count\"=4)),KEY_GENERATE_STRATEGY(COLUMN=order_id,TYPE(NAME=snowflake))), " +
                "t_order_item(RESOURCES(ds_0,ds_1),SHARDING_COLUMN=order_id,TYPE(NAME=hash_mod,PROPERTIES(\"sharding-count\"=4)),KEY_GENERATE_STRATEGY(COLUMN=order_id,TYPE(NAME=snowflake)))");
        // create table
        statement.execute("CREATE TABLE t_order (order_id INT NOT NULL, user_id INT NOT NULL, status VARCHAR(45) NULL, PRIMARY KEY (order_id))");
        statement.execute("CREATE TABLE t_order_item (item_id INT NOT NULL, order_id INT NOT NULL, user_id INT NOT NULL, status VARCHAR(45) NULL, creation_date DATE, PRIMARY KEY (item_id))");
        // add data
        statement.execute("insert into t_order (order_id, user_id, status) values (1,2,'ok'),(2,4,'ok'),(3,6,'ok'),(4,1,'ok'),(5,3,'ok'),(6,5,'ok')");
        statement.execute("insert into t_order_item (item_id, order_id, user_id, status) values (1,1,2,'ok'),(2,2,4,'ok'),(3,3,6,'ok'),(4,4,1,'ok'),(5,5,3,'ok'),(6,6,5,'ok')");
    }

    private void targetPreMySQL(final Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("drop database if exists scaling_ds_2");
        statement.execute("create database scaling_ds_2 default charset utf8");
        statement.execute("drop database if exists scaling_ds_3");
        statement.execute("create database scaling_ds_3 default charset utf8");
        statement.execute("drop database if exists scaling_ds_4");
        statement.execute("create database scaling_ds_4 default charset utf8");
    }

    private void targetPreProxy(final Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("ADD RESOURCE ds_2 (\n" +
                "    URL=\"jdbc:mysql://127.0.0.1:13306/scaling_ds_2?serverTimezone=UTC&useSSL=false\",\n" +
                "    USER=root,\n" +
                "    PASSWORD= root,\n" +
                "    PROPERTIES(\"maximumPoolSize\"=50,\"idleTimeout\"=\"60000\")\n" +
                "), ds_3 (\n" +
                "    URL=\"jdbc:mysql://127.0.0.1:13306/scaling_ds_3?serverTimezone=UTC&useSSL=false\",\n" +
                "    USER=root,\n" +
                "    PASSWORD= root,\n" +
                "    PROPERTIES(\"maximumPoolSize\"=50,\"idleTimeout\"=\"60000\")\n" +
                "), ds_4 (\n" +
                "    URL=\"jdbc:mysql://127.0.0.1:13306/scaling_ds_4?serverTimezone=UTC&useSSL=false\",\n" +
                "    USER=root,\n" +
                "    PASSWORD= root,\n" +
                "    PROPERTIES(\"maximumPoolSize\"=50,\"idleTimeout\"=\"60000\")\n" +
                ")");
        statement.execute("CREATE SHARDING SCALING RULE scaling_manual1 (DATA_CONSISTENCY_CHECKER(TYPE(NAME=DATA_MATCH, PROPERTIES(\"chunk-size\"=1000))))");
        statement.execute("ALTER SHARDING TABLE RULE t_order(RESOURCES(ds_2,ds_3,ds_4),SHARDING_COLUMN=order_id,TYPE(NAME=hash_mod,PROPERTIES(\"sharding-count\"=6)),KEY_GENERATE_STRATEGY(COLUMN=order_id,TYPE(NAME=snowflake)))");
    }

    @Override
    public Status run() {
        try {
            Statement statement = CONNECTIONS.get("3307").createStatement();
            ResultSet resultSet = statement.executeQuery("show scaling list");
            String jobId = "";
            while (resultSet.next()) {
                jobId = resultSet.getString("id");
            }
            boolean r =  handleLockTest(jobId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }

    private boolean handleLockTest(final String jobId) {
        tryLock(jobId);
        if (!checkLock()) {
            return false;
        }
        unlock(jobId);
        return !checkLock();
    }

    private boolean checkLock() {
        try {
            Connection connection = CONNECTIONS.get("3307");
            String sql = "update t_order set status = ? where order_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "ok1");
            preparedStatement.setInt(2, 4);
            preparedStatement.executeUpdate();
        } catch (final Exception ex) {
            return true;
        }
        return false;
    }

    private void tryLock(String jobId) {
        boolean isLocked;
        do {
            isLocked = lock(jobId);
        } while (!isLocked);
    }

    private boolean lock(String jobId) {
        try {
            Connection connection = CONNECTIONS.get("3307");
            String sql = "STOP SCALING SOURCE WRITING " + jobId;
            connection.createStatement().executeQuery(sql);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private void unlock(final String jobId) {
        try {
            String sql = "restore scaling source writing " + jobId;
            CONNECTIONS.get("3307").createStatement().execute(sql);
        } catch (Exception ex) {
        }
    }

    @Override
    public Status end() {
        try {
            for (Connection each : CONNECTIONS.values()) {
                each.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
        
    }

    @Override
    public void initCase() {
        String name = "stop-writing-for-schema";
        String feature = "proxy-lock";
        String tag = "MySQL";
        String message = "stop-writing-for-schema";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
