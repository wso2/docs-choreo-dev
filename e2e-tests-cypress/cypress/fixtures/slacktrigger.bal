import ballerinax/trigger.slack;
import ballerina/http;
import ballerina/log;

configurable slack:ListenerConfig config = {
    verificationToken: "pkKgDNr5vGND364IsHzwGM7O"
{downarrow}
;
{enter}
listener http:Listener httpListener = new (8090);
listener slack:Listener webhookListener = new (config, httpListener);
{enter}
service slack:SlackEventsAppService on webhookListener {

remote function onAppMention(slack:GenericEventWrapper payload) returns error? {
    log:printInfo("App Mentioned : ");
    log:printInfo(payload.event?.text.toJsonString());
    {downarrow}
remote function onAppRateLimited(slack:GenericEventWrapper payload) returns error? {
    return error("Not Implemented");
 {downarrow}
remote function onAppUninstalled(slack:GenericEventWrapper payload) returns error? {
    return error("Not Implemented");
  {downarrow}
 {downarrow}
  {downarrow}

service /ignore on httpListener {

