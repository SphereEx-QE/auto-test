package com.sphereex.cases;

import com.sphereex.core.DBType;
import com.sphereex.utils.AutoDataSource;
import com.sphereex.utils.ProxyDataSource;
import lombok.Getter;

public abstract class ProxyBaseTest extends BaseCaseImpl {
    
    @Getter
    private final AutoDataSource autodataSource;
    
    public ProxyBaseTest(DBType dbType) {
        this.autodataSource = new ProxyDataSource(getDbInfo(), dbType);
    }
    
    @Override
    public void end() throws Exception {
        autodataSource.close();
    }
}
