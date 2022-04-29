package com.sphereex.cases.traffic.jdbc;

import com.sphereex.core.AutoTest;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.Status;
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
public final class MultiThreadTransactionTest extends TrafficBaseTest {
    
    private final Logger logger = LoggerFactory.getLogger(MultiThreadTransactionTest.class);
    
    private final List<Connection> connections = new LinkedList<>();
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    @Override
    public Status run() {
        try {
            innerRun();
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, e.getMessage());
        }
        return new Status(true, "");
    }
    
    private void innerRun() throws Exception {
        for (int i = 0; i < 10; i++) {
            Connection connection = getAutoDataSource().getConnection();
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
                throw e;
            }
        }
    }
    
    @Override
    public Status end() {
        try {
            for (Connection each : connections) {
                each.close();
            }
        } catch (SQLException e) {
            return new Status(false, e.getMessage());
        }
        executorService.shutdown();
        return new Status(true, "");
    }
    
    @Override
    public void init() {
        String name = getClass().getName();
        String feature = "traffic-transaction";
        String tag = "conf/case/Traffic";
        String message = "Connection in transaction should be traffic to proxy.";
        caseInfo = new CaseInfo(name, feature, tag, message);
    }
    
    @Override
    public CaseInfo getCaseInfo() {
        if (null == caseInfo) {
            init();
        }
        return caseInfo;
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
