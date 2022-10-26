import ballerinax/googleapis.sheets;
import ballerina/http;
import ballerinax/salesforce as sfdc;
import ballerina/log;

type SalesforceOAuth2Config record {
    string clientId;
    string clientSecret;
    string refreshToken;
    string refreshUrl = "https://login.salesforce.com/services/oauth2/token";
};

type GsheetOAuth2Config record {
    string clientId;
    string clientSecret;
    string refreshToken;
    string refreshUrl ;
    string sheetID;
    string sheetName;
    
};

// Salesforce configuration parameters
configurable SalesforceOAuth2Config salesforceOAuthConfig = ?;
configurable string salesforceBaseUrl = ?;

service /createLead on new http:Listener(9090) {
    resource function get .() returns error? {

        // Get the column headings
        (int|string|decimal)[] headings = ["Lead Title", "Lead description", "Customer name", "Contact details"];
        (int|string|decimal)[] LeadData = ["Lead Test", "Sample Lead description", "Customer name", "Contact details"];

        // Construct the new lead json record
        map<json> newLead = {};
        foreach int index in 0 ..< headings.length() {
            newLead[headings[index].toString()] = LeadData[index];
        }

        sfdc:Client sfdcClient = check new ({
            baseUrl: salesforceBaseUrl,
            clientConfig: {
                clientId: salesforceOAuthConfig.clientId,
                clientSecret: salesforceOAuthConfig.clientSecret,
                refreshToken: salesforceOAuthConfig.refreshToken,
                refreshUrl: salesforceOAuthConfig.refreshUrl
            }
        });
        string createLeadResponse = check sfdcClient->createLead(newLead);
        log:printInfo(string `Lead created successfully!. Lead ID : ${createLeadResponse}`);

    }
}

// Salesforce configuration parameters
configurable GsheetOAuth2Config gsheetOAuth2Config = ?;
configurable string GsheetBaseUrl = ?;

service /getLead on new http:Listener(9090) {
    resource function get .() returns error? {

    sheets:Client sheetsEp = check new (spreadsheetConfig = {
        auth: {
            token: ""
        }
    });
    sheets:Row getRowResponse = check sheetsEp->getRow(spreadsheetId = gsheetOAuth2Config.sheetID, sheetName = gsheetOAuth2Config.sheetName, row = 0);
}
}