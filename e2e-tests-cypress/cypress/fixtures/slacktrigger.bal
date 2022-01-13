import ballerinax/trigger.slack;
import ballerina/http;
import ballerina/log;

configurable slack:ListenerConfig userInput = {
    verificationToken: "pkKgDNr5vGND364IsHzwGM7O"
}; 

listener http:Listener httpListener = new(8090);
listener slack:Listener Listener = new(userInput, httpListener);

service slack:SlackEventsAppService on Listener {
    
    remote function onAppMention(slack:GenericEventWrapper event) returns error? {
        log:printInfo("App Mentioned : ");
        log:printInfo(event.event?.text.toJsonString());
    }
    remote function onAppRateLimited(slack:GenericEventWrapper event) returns error? {
        return error ("Not Implemented");
    }
    remote function onAppUninstalled(slack:GenericEventWrapper event) returns error? {
        return error ("Not Implemented");
    }
}

service /ignore on httpListener {}