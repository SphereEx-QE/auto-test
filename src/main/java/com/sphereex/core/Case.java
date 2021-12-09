package com.sphereex.core;

import java.sql.SQLException;

public interface Case {
    void pre() throws ClassNotFoundException, SQLException;

    void run() throws SQLException;

    void end() throws SQLException, ClassNotFoundException;

    void start() throws SQLException, ClassNotFoundException;

    CaseInfo getCaseInfo();

    void setDbInfo(DBInfo dbInfo);
}
