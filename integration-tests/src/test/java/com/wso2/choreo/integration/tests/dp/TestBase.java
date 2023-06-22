package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import org.testng.ITest;
import org.testng.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestBase extends TestNGCitrusSpringSupport implements ITest {
    private final ThreadLocal<String> testName = new ThreadLocal<>();
    private  List<DataProviderWrapper> dps = new ArrayList<>();


    @BeforeClass
    public Object[][] setUp() {
        if (dps.size() == 0) {
            String[] regions = Configuration.getConfig(ConfigDefinition.REGIONS).split(",");
            for (String region : regions) {
                DataProviderWrapper dp = DataProviderWrapper.builder().region(region).build();
                dps.add(dp);
            }
        }
        return DataProviderWrapper.convertToDataProvider(dps);
    }


    @AfterClass
    public void clean(){
        if(dps.size()>0){
           dps=new ArrayList<>();
        }
    }

    @BeforeMethod
    public void BeforeMethod(Method method, Object[] testData) throws NoSuchFieldException, IllegalAccessException {
        Field f = testData[0].getClass().getDeclaredField("region");
        f.setAccessible(true);
        String regionName = f.get(testData[0]).toString();
        testName.set(method.getName() + "_" + regionName);
    }

    @Override
    public String getTestName() {
        return testName.get();
    }
}
