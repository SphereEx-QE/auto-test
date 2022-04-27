package com.sphereex.cases.base;

import com.sphereex.core.Case;
import com.sphereex.core.CaseInfo;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseCaseImpl implements Case {

    private CaseInfo caseInfo;
}
