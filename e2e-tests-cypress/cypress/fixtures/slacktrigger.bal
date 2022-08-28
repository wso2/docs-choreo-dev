import ballerinax/trigger.slack;
import ballerina/http;
import ballerina/log;

configurable slack:ListenerConfig config = {
    verificationToken: "pkKgDNr5vGND364IsHzwGM7O"
};

listener http:Listener httpListener = new (8090);
listener slack:Listener webhookListener = new (config, httpListener);

service slack:SlackEventsAppService on webhookListener {

    remote function onAppMention(slack:GenericEventWrapper payload) returns error? {
        log:printInfo("App Mentioned : ");
        log:printInfo(payload.event?.text.toJsonString());
    }
    remote function onAppRateLimited(slack:GenericEventWrapper payload) returns error? {
        return error("Not Implemented");
    }
    remote function onAppUninstalled(slack:GenericEventWrapper payload) returns error? {
        return error("Not Implemented");
    }
}

service /ignore on httpListener {
}

