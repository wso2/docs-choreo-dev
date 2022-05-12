package com.wso2.choreo.integration.tests.anomalyDetector;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.exceptions.GetApiTestTokenStatusCheckException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing methods to invoke APIs for anomaly detection tests
 */
public class InvokeApi {

    private final static Logger log = LoggerFactory.getLogger(InvokeApi.class);

    /**
     * Invokes the passthrough component from a pool of threads concurrently 
     * 
     * @param invokeAccessTokenHandler
     * @param clientId
     * @param clientSecret
     * @param invokeUrl
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws GetApiTestTokenStatusCheckException
     */
    public static void invokePassthroughComponentConcurrently(TokenHandler invokeAccessTokenHandler, String clientId, String clientSecret, String invokeUrl) throws IOException, InterruptedException, ExecutionException, GetApiTestTokenStatusCheckException {
      ExecutorService executor = Executors.newFixedThreadPool(20);
      String authorizationBearerToken = invokeAccessTokenHandler.getApiTestToken(clientId, clientSecret);
      log.info("Starting to send requests to the passthrough component...");
      for (int i = 0; i < 500; i++) {
        Runnable worker = new InvokePassthroughComponent(invokeUrl + "/", authorizationBearerToken, "helloword");
        executor.execute(worker);
      }
      executor.shutdown();
      executor.awaitTermination(10, TimeUnit.MINUTES);
      log.info("Finished sending requests to the passthrough component");
    }  
}
