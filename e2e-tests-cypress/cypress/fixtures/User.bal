import ballerina/log;
import ballerina/http;

service /users on new http:Listener(8080) {
    resource function get users() returns json|error? {
        string dora = "dora";
        log:printInfo(dora);