import ballerina/http;

service /numbers on new http:Listener(9090) {

    resource function get root(int number) returns int|error {
        return number * number;
    }

    resource function get isOdd(int number) returns boolean|error {
        if (number % 2 == 0) {
            return false;
        }

        return true;

    }
}
