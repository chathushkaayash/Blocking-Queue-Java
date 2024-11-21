import ballerina/jballerina.java;

function init() {
    setModule();
}

function setModule() = @java:Method {
    'class: "io.ayash.blockingqueue.ModuleUtils"
} external;
