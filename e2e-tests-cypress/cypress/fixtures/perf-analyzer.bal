import ballerina/http;

service / on new http:Listener(9095) {
    resource function post .(http:Request req) returns json|error? {
        http:Client httpEndpoint = check new ("https://postman-echo.com/post");
        json getResponse = check httpEndpoint->forward("/", req);
        json getResponse2 = check httpEndpoint->forward("/", req);

        return "ok";
    }
}
