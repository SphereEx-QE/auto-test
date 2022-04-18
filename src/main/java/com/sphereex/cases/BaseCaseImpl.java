package com.sphereex.cases;

import com.sphereex.core.Case;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.DBInfo;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseCaseImpl implements Case {

    private CaseInfo caseInfo;

    private DBInfo dbInfo;
  
    @Override
    public void start() throws Exception {
        pre();
        run();
        end();
    }
    
    @Override
    public boolean caseInfoIsNull() {
        return null == caseInfo;
    }
}
