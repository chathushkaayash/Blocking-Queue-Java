import ballerina/jballerina.java;

public class BlockingQueue {

    # Initializes the queue with the given size.
    #
    # ```ballerina
    # BlockingQueue queue = new BlockingQueue(10);
    # ```
    # + size - The size of the queue.
    public function init(int size) returns Error? {
        check self.externInit(size);
    }

    isolated function externInit(int size) returns Error? = @java:Method {
        name: "init",
        'class: "io.ayash.blockingqueue.BallerinaToNativeWrapper"
    } external;

    # Inserts the specified element into the queue, waiting if necessary for space to become available.
    #
    # ```ballerina
    # queue.put(10);
    # ```
    #
    # + item - The element to add to the queue.
    # + return - Returns an error if an issue occurs while putting the item.
    isolated function put(anydata item) returns Error? = @java:Method {
        'class: "io.ayash.blockingqueue.BallerinaToNativeWrapper"
    } external;

    # Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.
    #
    # ```ballerina
    # queue.take();
    # ```
    #
    # + return - Returns the head of the queue.
    # + return - Returns an error if an issue occurs while taking the item.
    isolated function take() returns anydata|Error = @java:Method {
        'class: "io.ayash.blockingqueue.BallerinaToNativeWrapper"
    } external;

    # Inserts the specified element into this queue, waiting up to the specified wait time if necessary for space to become available.
    #
    # ```ballerina
    # queue.offer(10);
    # ```
    # + item - The element to add to the queue.
    # + timeout - The time to wait for the operation to complete.
    #
    # + return - Returns true if the element was added to this queue, else false.
    isolated function offer(anydata item, int timeout) returns boolean|Error = @java:Method {
        'class: "io.ayash.blockingqueue.BallerinaToNativeWrapper"
    } external;

    # Returns true if this queue contains the specified element. 
    #
    # ```ballerina
    # queue.contains(10);
    # ```
    # + item - The element to check in the queue.
    # + return - Returns true if the element is in the queue, else false.
    isolated function contains(anydata item) returns boolean = @java:Method {
        'class: "io.ayash.blockingqueue.BallerinaToNativeWrapper"
    } external;

    # Inserts the specified element into this queue if it is possible to do so immediately without violating capacity restrictions.
    #
    # ```ballerina
    # queue.add(10);
    # ```
    # + item - The element to add to the queue.
    # + return - returns true upon success and returns an error if no space is available.
    isolated function add(anydata item) returns true|Error = @java:Method {
        'class: "io.ayash.blockingqueue.BallerinaToNativeWrapper"
    } external;

    # Retrieves, but does not remove, the head of this queue.
    # ```ballerina
    # queue.peek();
    # ```
    # + return - Returns the head of the queue. Returns null if the queue is empty.
    isolated function peek() returns anydata|Error = @java:Method {
        'class: "io.ayash.blockingqueue.BallerinaToNativeWrapper"
    } external;

    # Returns the number of elements in this queue.
    #
    # ```ballerina
    # queue.size();
    # ```
    # + return - Returns the number of elements in the queue.
    isolated function size() returns int = @java:Method {
        'class: "io.ayash.blockingqueue.BallerinaToNativeWrapper"
    } external;

    # Returns true if this queue contains no elements.
    #
    # ```ballerina
    # queue.isEmpty();
    # ```
    # + return - Returns true if the queue is empty, else false.
    isolated function isEmpty() returns boolean = @java:Method {
        'class: "io.ayash.blockingqueue.BallerinaToNativeWrapper"
    } external;

    # Returns true if this queue is full.
    #
    # ```ballerina
    # queue.isFull();
    # ```
    # + return - Returns true if the queue is full, else false.
    isolated function isFull() returns boolean = @java:Method {
        'class: "io.ayash.blockingqueue.BallerinaToNativeWrapper"
    } external;

}

