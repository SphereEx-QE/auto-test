package com.sphereex.cases.transaction.traffic;

import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@AutoTest
public class MultiThreadTransactionTest extends TrafficBaseTest {
    private final Logger logger = LoggerFactory.getLogger(MultiThreadTransactionTest.class);
    
    private final List<Connection> connections = new LinkedList<>();
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    public MultiThreadTransactionTest() {
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setName("MultiThreadTransactionTest");
        caseInfo.setFeature("transaction");
        caseInfo.setTag("conf/Traffic");
        caseInfo.setStatus(false);
        caseInfo.setMessage("Connection in transaction should be traffic to proxy.");
        setCaseInfo(caseInfo);
    }
    
    @Override
    public void run() throws Exception {
        for (int i = 0; i < 10; i++) {
            Connection connection = getDataSource().getConnection();
            connection.setAutoCommit(false);
            connections.add(connection);
        }
        List<Future<?>> futures = new LinkedList<>();
        Iterator<Connection> iterator = connections.iterator();
        for (int i = 0; i < 10; i++) {
            futures.add(executorService.submit(() -> execute(iterator)));
        }
        for (Future<?> each : futures) {
            try {
                each.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("execute error", e);
                throw new Exception("execute error");
            }
        }
    }
    
    private void execute(final Iterator<Connection> iterator) {
        Connection connection = iterator.next();
        try {
            connection.createStatement().execute("select 1");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
