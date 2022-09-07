import ballerina/graphql;

# A service representing a network-accessible GraphQL API
service / on new graphql:Listener(8090) {

    # A resource for generating greetings
    # + name - the input string name
    # + return - string name with greeting message or error
    resource function get greeting(string name) returns string|error {
        // Send a response back to the caller.
        if name is "" {
            return error("name should not be empty!");
        }
        return "Hello, " + name;
    }

    remote function createUser(string name) returns string|error {
        if name is "" {
            return error("name should not be empty!");
        }
        return "User created with name: " + name;
    }
}
