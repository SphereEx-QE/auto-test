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
import com.sphereex.utils.MySQLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@AutoTest
public class SchemaStopWritingTest extends BaseCaseImpl {
    
    private static final Logger logger = LoggerFactory.getLogger(SchemaStopWritingTest.class);
    
    private Map<String, Connection> connections = new LinkedHashMap<>();
    
    private int token;
    
    @Override
    public void pre() throws Exception {
        connections.put("0", MySQLUtil.getInstance().getConnection(new DBInfo("127.0.0.1", 3307, "root", "root", "scaling_db")));
        connections.put("1", MySQLUtil.getInstance().getConnection(new DBInfo("127.0.0.1", 3308, "root", "root", "scaling_db")));
        
        Statement stmt3307 = connections.get("0").createStatement();
        Statement stmt3308 = connections.get("1").createStatement();
        
        // add resource
        stmt3307.execute("ADD RESOURCE ds_0 (\n" +
                "    URL=\"jdbc:mysql://127.0.0.1:13306/scaling_ds_0?serverTimezone=UTC&useSSL=false\",\n" +
                "    USER=root,\n" +
                "    PASSWORD=root,\n" +
                "    PROPERTIES(\"maximumPoolSize\"=50,\"idleTimeout\"=\"60000\")\n" +
                "), ds_1 (\n" +
                "    URL=\"jdbc:mysql://127.0.0.1:13306/scaling_ds_1?serverTimezone=UTC&useSSL=false\",\n" +
                "    USER=root,\n" +
                "    PASSWORD=root,\n" +
                "    PROPERTIES(\"maximumPoolSize\"=50,\"idleTimeout\"=\"60000\")\n" +
                ")");
        
        // add rule
        stmt3307.execute("CREATE SHARDING TABLE RULE t_order(\n" +
                "RESOURCES(ds_0,ds_1),\n" +
                "SHARDING_COLUMN=order_id,\n" +
                "TYPE(NAME=hash_mod,PROPERTIES(\"sharding-count\"=4)),\n" +
                "KEY_GENERATE_STRATEGY(COLUMN=order_id,TYPE(NAME=snowflake))\n" +
                "), t_order_item(\n" +
                "RESOURCES(ds_0,ds_1),\n" +
                "SHARDING_COLUMN=order_id,\n" +
                "TYPE(NAME=hash_mod,PROPERTIES(\"sharding-count\"=4)),\n" +
                "KEY_GENERATE_STRATEGY(COLUMN=order_id,TYPE(NAME=snowflake))\n" +
                ")");
    
        // create table
        stmt3307.execute("CREATE TABLE t_order (order_id INT NOT NULL, user_id INT NOT NULL, status VARCHAR(45) NULL, PRIMARY KEY (order_id))");
        stmt3307.execute("CREATE TABLE t_order_item (item_id INT NOT NULL, order_id INT NOT NULL, user_id INT NOT NULL, status VARCHAR(45) NULL, creation_date DATE, PRIMARY KEY (item_id))");
        
        // add data
        stmt3307.execute("insert into t_order (order_id, user_id, status) values (1,2,'ok'),(2,4,'ok'),(3,6,'ok'),(4,1,'ok'),(5,3,'ok'),(6,5,'ok')");
        stmt3307.execute("insert into t_order_item (item_id, order_id, user_id, status) values (1,1,2,'ok'),(2,2,4,'ok'),(3,3,6,'ok'),(4,4,1,'ok'),(5,5,3,'ok'),(6,6,5,'ok')");
        
        stmt3308.execute("ADD RESOURCE ds_2 (\n" +
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
    
        stmt3307.execute("CREATE SHARDING SCALING RULE scaling_manual1 (DATA_CONSISTENCY_CHECKER(TYPE(NAME=DATA_MATCH, PROPERTIES(\"chunk-size\"=1000))))");
        
        stmt3307.execute("ALTER SHARDING TABLE RULE t_order(\n" +
                "RESOURCES(ds_2,ds_3,ds_4),\n" +
                "SHARDING_COLUMN=order_id,\n" +
                "TYPE(NAME=hash_mod,PROPERTIES(\"sharding-count\"=6)),\n" +
                "KEY_GENERATE_STRATEGY(COLUMN=order_id,TYPE(NAME=snowflake))\n" +
                ")");
        
    }
    
    @Override
    public void run() throws Exception {
        Statement stmt3307 = connections.get("proxy-3307").createStatement();
        ResultSet resultSet = stmt3307.executeQuery("show scaling list");
        String jobId = "";
        while (resultSet.next()) {
            jobId = resultSet.getString("id");
        }
        handleTest(10, 100, jobId);
    }
    
    private void handleTest(final int threadSize, final int loopSize, final String jobId) {
        this.token = threadSize * loopSize;
        List<Thread> threadList = new LinkedList<>();
        Thread eachThread;
        for (int i = 0; i < threadSize; i++) {
            eachThread = new Thread(String.valueOf((Callable<String>) () -> {
                for (int j = 0; j < 1000; j++) {
                    checkLockStatus(jobId);
                }
                return "S";
            }));
            threadList.add(eachThread);
        }
        threadList.forEach(Thread::start);
    }
    
    private void checkLockStatus(final String jobId) throws SQLException {
        try {
            lock(randomConnection(), jobId);
            sellToken(randomConnection());
        } finally {
            unlock(randomConnection(), jobId);
        }
    }
    
    private Connection randomConnection() {
        String key = (int) (Math.random() * connections.size()) + "";
        return connections.get(key);
    }
    
    private void sellToken(final Connection connection) throws SQLException {
        if (isLocked(connection)) {
            this.token--;
            return;
        }
        throw new RuntimeException("lock error");
    }
    
    private boolean isLocked(Connection connection) throws SQLException {
        String sql = "update t_order set status = ? where order_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "ok1");
        preparedStatement.setInt(2, 4);
        try {
            preparedStatement.executeUpdate();
        } catch (final Exception ex) {
            return true;
        }
        return false;
    }
    
    private void lock(final Connection connection, final String jobId) throws SQLException {
        boolean isLocKed;
        do {
            String sql = "STOP SCALING SOURCE WRITING " + jobId;
            isLocKed = connection.createStatement().execute(sql);
        } while (!isLocKed);
    }
    
    private void unlock(final Connection connection, final String jobId) throws SQLException {
        boolean isReleased;
        do {
            String sql = "restore scaling source writing " + jobId;
            isReleased = connection.createStatement().execute(sql);
        } while (!isReleased);
    }
    
    @Override
    public void initCaseInfo() {
        String name = "stop writing for schema";
        String feature = "proxy-lock";
        String tag = "MySQL";
        String message = "stop writing for schema";
        CaseInfo caseInfo = new CaseInfo(name, feature, tag, message);
        setCaseInfo(caseInfo);
    }
}
