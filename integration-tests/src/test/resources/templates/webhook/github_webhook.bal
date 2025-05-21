import ballerinax/trigger.github;
import ballerina/http;
import ballerina/log;

configurable github:ListenerConfig config = ?;
listener http:Listener httpListener = new (8090);
listener github:Listener webhookListener = new (config, httpListener);

service github:IssuesService on webhookListener {

    remote function onOpened(github:IssuesEvent payload ) returns error? {
      log:printInfo(payload.issue.title);
    }
    remote function onClosed(github:IssuesEvent payload ) returns error? {
      //Not Implemented
    }
    remote function onReopened(github:IssuesEvent payload ) returns error? {
      //Not Implemented
    }
    remote function onAssigned(github:IssuesEvent payload ) returns error? {
      //Not Implemented
    }
    remote function onUnassigned(github:IssuesEvent payload ) returns error? {
      //Not Implemented
    }
    remote function onLabeled(github:IssuesEvent payload ) returns error? {
      //Not Implemented
    }
    remote function onUnlabeled(github:IssuesEvent payload ) returns error? {
      //Not Implemented
    }
}

service /ignore on httpListener {}
