package com.sphereex.cases;

import com.sphereex.core.Case;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBInfo;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;

@Setter
@Getter
public abstract class BaseCaseImpl implements Case {

    private CaseInfo caseInfo;

    private DBInfo dbInfo;

    public abstract void pre() throws ClassNotFoundException, SQLException;

    public abstract void run() throws SQLException;

    public abstract void end() throws SQLException, ClassNotFoundException;

    @Override
    public void start() throws SQLException, ClassNotFoundException {
        pre();
        run();
        end();
    }
}
