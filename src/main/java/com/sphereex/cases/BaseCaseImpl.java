package com.sphereex.cases;

import com.sphereex.core.Case;
import com.sphereex.core.CaseInfo;
import com.sphereex.core.Status;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseCaseImpl implements Case {

    private CaseInfo caseInfo;

    @Override
    public Status start() {
        Status preStatus = pre();
        if (!preStatus.isSuccess()) {
            return preStatus;
        }
        Status runStatus = run();
    
        if (!runStatus.isSuccess()) {
            return runStatus;
        }
    
        Status endStatus = end();
    
        if (!endStatus.isSuccess()) {
            return endStatus;
        }
        return new Status(true, "");
    }
    
    @Override
    public boolean isValid() {
        return null != caseInfo;
    }
}
