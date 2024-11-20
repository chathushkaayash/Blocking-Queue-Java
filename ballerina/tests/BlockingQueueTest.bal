// import ballerina/lang.runtime;

import ballerina/lang.runtime;
import ballerina/test;

// ----------------------- NullPointer tests --------------------------------

@test:Config {}
function checkNullPointerInOffer() returns error? {
    BlockingQueue queue = new BlockingQueue(5);
    boolean|error offerResult = queue.offer(null);
    test:assertTrue(offerResult is error, msg = "NullPointerException expected");
}

@test:Config {}
function checkNullPointerInAdd() returns error? {
    BlockingQueue queue = new BlockingQueue(5);
    boolean|error addResult = queue.add(null);
    test:assertTrue(addResult is error, msg = "NullPointerException expected");
}

@test:Config {}
function checkNullPointerInPut() returns error? {
    BlockingQueue queue = new BlockingQueue(5);
    error? putResult = queue.put(null);
    test:assertTrue(putResult is error, msg = "NullPointerException expected");

}

// ----------------------- Test Initial State ---------------------

@test:Config {}
function testInitialState() returns error? {
    BlockingQueue queue = new BlockingQueue(5);
    test:assertTrue(queue.isEmpty());
    test:assertEquals(queue.size(), 0);
    test:assertFalse(queue.isFull());
    test:assertEquals(queue.peek(), null);
}

// // ----------------------- Test add and remove elements ---------------------

@test:Config {}
function testAddAndRemoveElements() returns error? {
    BlockingQueue queue = new BlockingQueue(5);
    check queue.put("1");
    check queue.put("2");
    check queue.put("3");

    test:assertEquals(queue.size(), 3, msg = "Queue size should be 3");
    test:assertFalse(queue.isEmpty(), msg = "Queue should not be empty");
    test:assertFalse(queue.isFull(), msg = "Queue should not be full");

    check queue.put("4");
    check queue.put("5");

    test:assertEquals(queue.size(), 5, msg = "Queue size should be 5");
    test:assertTrue(queue.isFull(), msg = "Queue should be full");

    // try to add more elements
    test:assertFalse(check queue.offer("6"), msg = "Offer should return false when queue is full");

    test:assertEquals(queue.take(), "1", msg = "Take should return '1'");
    test:assertEquals(queue.take(), "2", msg = "Take should return '2'");
    test:assertEquals(queue.take(), "3", msg = "Take should return '3'");
    test:assertEquals(queue.take(), "4", msg = "Take should return '4'");
    test:assertEquals(queue.take(), "5", msg = "Take should return '5'");

    test:assertEquals(queue.size(), 0, msg = "Queue size should be 0 after removing all elements");
}

// ----------------------- Blocking Tests ---------------------

@test:Config {}
function testBlockingOnFullQueue() returns error? {
    BlockingQueue queue = new BlockingQueue(2);
    check queue.put(1);
    check queue.put(2);

    _ = start producer(queue, [3]);

    runtime:sleep(1);
    test:assertEquals(queue.take(), 1);
    test:assertEquals(queue.take(), 2);
    test:assertEquals(queue.take(), 3);
}

@test:Config {}
function testBlockingOnEmptyQueue() returns error? {
    BlockingQueue queue = new BlockingQueue(2);

    future<int[]|error?> futureResult = start consumer(queue, 3);

    runtime:sleep(1);
    check queue.put(1);
    check queue.put(2);
    check queue.put(3);

    int[]|error? result = check wait futureResult;
    test:assertEquals(result, [1, 2, 3], msg = "Consumer should receive all values");
}

// ----------------------- Test concurrent access ---------------------

@test:Config {}
function testConcurrentAccess() returns error? {
    BlockingQueue queue = new BlockingQueue(5);

    int[] values = [1, 2, 3, 4, 5];

    _ = start producer(queue, values, 0.300);
    future<int[]|error?> futureResult = start consumer(queue, 5, 0.200);

    int[]|error? result = check wait futureResult;
    test:assertEquals(result, values, msg = "Consumer should receive all values");

    test:assertTrue(queue.isEmpty());
}

// ----------------------- Test Add ---------------------
@test:Config {}
function testAdd() returns error? {
    BlockingQueue queue = new BlockingQueue(2);
    test:assertTrue(check queue.add(1));
    test:assertTrue(check queue.add(2));

    test:assertEquals(queue.size(), 2);
    test:assertTrue(queue.isFull());

    // boolean|error res = queue.add(3);
    // test:assertTrue(res is error); fix this
}

// ----------------------- Test Contains ---------------------
@test:Config {}
function testContains() returns error? {
    BlockingQueue queue = new BlockingQueue(2);
    check queue.put(1);
    check queue.put(2);

    test:assertTrue(queue.contains(1));
    test:assertTrue(queue.contains(2));
    test:assertFalse(queue.contains(3));
}

// ----------------------- Test Offer ---------------------
@test:Config {}
function testOffer() returns error? {
    BlockingQueue queue = new BlockingQueue(2);
    test:assertTrue(check queue.offer(1));
    test:assertTrue(check queue.offer(2));

    test:assertEquals(queue.size(), 2);
    test:assertTrue(queue.isFull());

    test:assertFalse(check queue.offer(3));
}

// ----------------------- Test Peek ---------------------
@test:Config {}
function testPeek() returns error? {
    BlockingQueue queue = new BlockingQueue(2);
    check queue.put(1);
    check queue.put(2);

    test:assertEquals(queue.peek(), 1);
    test:assertEquals(queue.peek(), 1);
}

// ----------------------- Test IsEmpty ---------------------
@test:Config {}
function testIsEmpty() returns error? {
    BlockingQueue queue = new BlockingQueue(2);
    test:assertTrue(queue.isEmpty());

    check queue.put(1);
    test:assertFalse(queue.isEmpty());
}

// ----------------------- Test IsFull ---------------------
@test:Config {}
function testIsFull() returns error? {
    BlockingQueue queue = new BlockingQueue(2);
    test:assertFalse(queue.isFull());

    check queue.put(1);
    check queue.put(2);
    test:assertTrue(queue.isFull());
}

function producer(BlockingQueue queue, int[] values, decimal delay = 0) returns error? {
    foreach int item in values {
        runtime:sleep(delay);
        check queue.put(item);
    }
}

function consumer(BlockingQueue queue, int n = 1, decimal delay = 0) returns int[]|error? {
    int[] values = [];
    foreach int i in 0 ..< n {
        runtime:sleep(delay);
        int item = <int>check queue.take();
        values.push(item);
    }
    return values;
}
