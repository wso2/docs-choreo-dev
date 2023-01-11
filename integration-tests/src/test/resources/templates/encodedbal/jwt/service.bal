import ballerina/jwt as jwt;
import ballerina/http;
service / on new http:Listener(9090) {

    resource function get getJwt(http:Headers header) returns jwt:Payload|error {

        string|http:HeaderNotFoundError jwt1 = header.getHeader("x-jwt-assertion");

        if jwt1 is string {
            [jwt:Header, jwt:Payload]|error decodedJwt = jwt:decode(jwt1);

            if decodedJwt !is error {
                return decodedJwt[1];
            } else {
                return error("Failed to decode jwt");
            }

        }

        return error("Invalid JWT");

    }
}
