import ballerina/jballerina.java;

public class BlockingQueue {
    private handle jObj;

    # Initializes the queue with the given size.
    #
    # ```ballerina
    # BlockingQueue queue = new BlockingQueue(10);
    # ```
    # + size - The size of the queue.
    public isolated function init(int size) {
        self.jObj = BlockingQueueConstructor(size);
    }

    # Inserts the specified element into the queue, waiting if necessary for space to become available.
    #
    # ```ballerina
    # queue.put(10);
    # ```
    #
    # + item - The element to add to the queue.
    # + return - Returns an error if an issue occurs while adding the item.
    public isolated function put(anydata item) returns error? {
        if item is () {
            return error("NullPointerException");
        }
        return put(self.jObj, item);
    }

    # Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.
    #
    # ```ballerina
    # queue.take();
    # ```
    #
    # + return - Returns the head of the queue.
    # + return - Returns an error if an issue occurs while taking the item.
    public isolated function take() returns anydata|error? {
        return take(self.jObj);
    }

    # Inserts the specified element into this queue if it is possible to do so immediately without violating capacity restrictions.
    #
    # ```ballerina
    # queue.offer(10);
    # ```
    # + item - The element to add to the queue.
    #
    # + return - Returns true if the element was added to this queue, else false.
    public isolated function offer(anydata item) returns boolean|error {
        if (item == null) {
            return error("NullPointerException");
        }
        return offer(self.jObj, item);
    }

    # Returns true if this queue contains the specified element. 
    #
    # ```ballerina
    # queue.contains(10);
    # ```
    # + item - The element to check in the queue.
    # + return - Returns true if the element is in the queue, else false.
    public isolated function contains(anydata item) returns boolean {
        return contains(self.jObj, item);
    }

    # Inserts the specified element into this queue if it is possible to do so immediately without violating capacity restrictions.
    #
    # ```ballerina
    # queue.add(10);
    # ```
    # + item - The element to add to the queue.
    # + return - returns true upon success and throwing an IllegalStateException if no space is currently available
    public isolated function add(anydata item) returns boolean|error {
        if (item == null) {
            return error("NullPointerException");
        }
        
        do {
            return add(self.jObj, item);
        } on fail var e {
            return error(e.message());
        }
    }

    # Retrieves, but does not remove, the head of this queue.
    # ```ballerina
    # queue.peek();
    # ```
    # + return - Returns the head of the queue. Returns null if the queue is empty.
    public isolated function peek() returns anydata|error {
        return peek(self.jObj);
    }

    # Returns the number of elements in this queue.
    #
    # ```ballerina
    # queue.size();
    # ```
    # + return - Returns the number of elements in the queue.
    # + return - Returns an error if an issue occurs while getting the size.
    public isolated function size() returns int {
        return size(self.jObj);
    }

    # Returns true if this queue contains no elements.
    #
    # ```ballerina
    # queue.isEmpty();
    # ```
    # + return - Returns true if the queue is empty, else false.
    public isolated function isEmpty() returns boolean {
        return isEmpty(self.jObj);
    }

    # Returns true if this queue is full.
    #
    # ```ballerina
    # queue.isFull();
    # ```
    # + return - Returns true if the queue is full, else false.
    public isolated function isFull() returns boolean {
        return isFull(self.jObj);
    }

}

isolated function BlockingQueueConstructor(int size) returns handle = @java:Constructor {
    paramTypes: ["int"],
    'class: "io.ayash.BlockingQueue"
} external;

isolated function put(handle jObj, anydata item) returns error? = @java:Method {
    paramTypes: ["io.ayash.BlockingQueue", "java.lang.Object"],
    name: "put",
    'class: "io.ayash.BlockingQueue"
} external;

isolated function take(handle jObj) returns anydata|error = @java:Method {
    paramTypes: ["io.ayash.BlockingQueue"],
    name: "take",
    'class: "io.ayash.BlockingQueue"
} external;

isolated function add(handle jObj, anydata item) returns boolean = @java:Method {
    paramTypes: ["io.ayash.BlockingQueue", "java.lang.Object"],
    name: "add",
    'class: "io.ayash.BlockingQueue"
} external;

isolated function offer(handle jObj, anydata item) returns boolean = @java:Method {
    paramTypes: ["io.ayash.BlockingQueue", "java.lang.Object"],
    name: "offer",
    'class: "io.ayash.BlockingQueue"
} external;

isolated function contains(handle jObj, anydata item) returns boolean = @java:Method {
    paramTypes: ["io.ayash.BlockingQueue", "java.lang.Object"],
    name: "contains",
    'class: "io.ayash.BlockingQueue"
} external;

isolated function peek(handle jObj) returns anydata|error = @java:Method {
    paramTypes: ["io.ayash.BlockingQueue"],
    name: "peek",
    'class: "io.ayash.BlockingQueue"
} external;

isolated function isEmpty(handle jObj) returns boolean = @java:Method {
    paramTypes: ["io.ayash.BlockingQueue"],
    name: "isEmpty",
    'class: "io.ayash.BlockingQueue"
} external;

isolated function isFull(handle jObj) returns boolean = @java:Method {
    paramTypes: ["io.ayash.BlockingQueue"],
    name: "isFull",
    'class: "io.ayash.BlockingQueue"
} external;

isolated function size(handle jObj) returns int = @java:Method {
    'class: "io.ayash.BlockingQueue"
} external;

