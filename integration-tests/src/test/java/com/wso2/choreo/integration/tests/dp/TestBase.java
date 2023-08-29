package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.OptionalConfigDefinition;
import org.testng.ITest;
import org.testng.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestBase extends TestNGCitrusSpringSupport implements ITest {
    private final ThreadLocal<String> testName = new ThreadLocal<>();
    private final List<DataProviderWrapper> dps = new ArrayList<>();


    @BeforeClass
    public Object[][] setUp() {
        if (dps.isEmpty()) {
            Optional<String> regionValue = Configuration.getOptionalConfig(OptionalConfigDefinition.REGIONS);

            if (regionValue.isPresent()) {
                String[] regions = regionValue.get().split(",");
                for (String region : regions) {
                    DataProviderWrapper dp = DataProviderWrapper.builder().region(region).build();
                    dps.add(dp);
                }
            } else {
                DataProviderWrapper dp = DataProviderWrapper.builder().region("").build();
                dps.add(dp);
            }
        }
        return DataProviderWrapper.convertToDataProvider(dps);
    }


    @AfterClass
    public void clean(){
        if(!dps.isEmpty()){
           dps.clear();
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
