package com.wso2.choreo.integration.common.utils;

import java.util.concurrent.TimeUnit;

public class SleepUtil {

    public static void sleep(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
