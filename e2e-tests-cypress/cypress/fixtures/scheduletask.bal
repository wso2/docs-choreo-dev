import ballerina/http;
import ballerina/io;

public function main() returns error? {
    final http:Client ep = check new ("https://jsonplaceholder.typicode.com/");
    json resp = check ep->get("todos/1");
    io:print(resp.toBalString());
