package com.wso2.choreo.integration.common.PlatformServices;

import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.platformServices.PlatformServices;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.models.platformServices.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlatformServicesUtils {
    public static CreatedDatabaseServer createReusableDatabaseServer(TestNGCitrusSpringSupport runner, HttpClient client, String dbServerName,
                                                                     String servicePlanId, String orgUUID, String accessToken) {
        List<CreatedDatabaseServer> dbServersList = PlatformServices.getDatabaseServers(runner, client, orgUUID,accessToken);
        for (CreatedDatabaseServer dbServer : dbServersList) {
            if (dbServer.getName().equals(dbServerName)) {
                return dbServer;
            }
        }
        CreatedDatabaseServer createdDbServer = PlatformServices.createDatabaseServer(runner, client, dbServerName, servicePlanId, accessToken);
        return createdDbServer;
    }

    public static void createReusableDatabase(TestNGCitrusSpringSupport runner, HttpClient client, String databaseServerId,
                                                            List<String> dbNames, String orgUUID, String accessToken) throws IOException {
        Boolean isValidPowerState = validateDbServerPowerStatus(runner,client,databaseServerId,orgUUID,accessToken);
        if (!isValidPowerState) {
            throw  new ValidationException("Database server is not in active state");
        }
        List<Database> databasesList = PlatformServices.getDatabases(runner, client, databaseServerId, accessToken);
        List<String> availableDbs = new ArrayList<>();
        for (Database database : databasesList) {
            availableDbs.add(database.getName());
        }
        for (String dbName : dbNames) {
            if (!availableDbs.contains(dbName)) {
                PlatformServices.createDatabase(runner,client,databaseServerId,dbName,accessToken);
            }
        }
    }

    public static Boolean validateDbServerPowerStatus(TestNGCitrusSpringSupport runner, HttpClient client, String databaseServerId,
                                                  String orgUUID, String accessToken) throws IOException {
        DatabaseServer dbServer = PlatformServices.getDatabaseServer(runner,client,databaseServerId,orgUUID,accessToken);
        if (dbServer.getStatus().equals(ServerStatus.ACTIVE)) {
            return true;
        }else if (dbServer.getStatus().equals(ServerStatus.RESUMING) || dbServer.getStatus().equals(ServerStatus.CREATING) ) {
            // It takes ~4 minutes for the db server to be active
            SleepUtil.sleep(360);
            return PlatformServices.validateDatabaseServerPowerStatus(runner,client,databaseServerId, ServerStatus.ACTIVE,accessToken);
        }else{
            PlatformServices.UpdateDatabaseServerPowerStatus(runner,client,databaseServerId,DBServerPowerAction.ON,accessToken);
            // It takes ~4 minutes for the db server to be active
            SleepUtil.sleep(360);
            return PlatformServices.validateDatabaseServerPowerStatus(runner,client,databaseServerId, ServerStatus.ACTIVE,accessToken);
        }
    }


}
