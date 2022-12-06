service / on new http:Listener(9090) {

    resource function get getJwt(http:Headers header) returns jwt1:Payload|error {

        string|http:HeaderNotFoundError jwt = header.getHeader("x-jwt-assertion");

        if jwt is string {
            [jwt1:Header, jwt1:Payload]|error decodedJwt = jwt1:decode(jwt);

            if decodedJwt !is error {
                return decodedJwt[1];
            } else {
                return error("Failed to decode jwt");
            }

        }

        return error("Invalid JWT");

    }
}
