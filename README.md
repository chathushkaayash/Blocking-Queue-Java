
# Blocking Queue in Java

## Introduction
A blocking queue is a queue that supports operations to wait for the queue to become non-empty when retrieving an element and to wait for space to become available in the queue when adding an element. This implementation of a blocking queue is based on a circular buffer with thread-safe access using a `ReentrantLock`.

## ReentrantLock

### Why Use ReentrantLock?

#### Explicit Lock Control and Flexibility
- `ReentrantLock` offers more fine-grained control over locking compared to synchronized blocks. 
- It allows separate lock and unlock calls, making it easier to manage complex logic where a lock might need to be held or released based on specific conditions.

#### Condition Variables
- `ReentrantLock` provides `Condition` objects, which offer greater control over thread signaling than `synchronized`'s `wait` and `notify`.
- Condition objects allow us to create custom signals for when the queue is empty or full. 

#### Fairness Policy
- The `ReentrantLock` can be created with a fairness policy, ensuring that the longest-waiting thread gets the lock next (has an internal FIFO queue). 
- This is crucial for preventing thread starvation.

## Class Structure

### Array Queue Storage (`Object[] queue`)
- Used to store the elements in the queue.

### Head and Tail Pointers (`head` and `tail`)
- Indicate where to read and write elements in the circular array.

### Capacity (`int capacity`)
- Maximum size of the queue.

### Size (`int size`)
- Tracks the current number of elements.

### Lock (`ReentrantLock lock`)
- Ensures mutual exclusion, making the queue thread-safe.

### Conditions (`Condition notFull` and `Condition notEmpty`)
- Used to signal when the queue is full or empty.

## Constructor
The constructor initializes the queue with a specified capacity. The array-based queue is fixed-sized.

```java
public BlockingQueue(int capacity) {
    this.capacity = capacity;
    queue = new Object[capacity];
}
```

## Method Implementations

### `put` Method
The `put` method inserts an item into the queue, waiting if necessary until space is available. This method ensures:
- **Thread Safety:** Uses `lock.lockInterruptibly()` to acquire the lock, allowing interruption while waiting for the lock. 
- **Blocking on Full Queue:** If the queue is full, the method calls `notFull.await()`, releasing the lock and waiting until space becomes available.
- **Signaling After Insertion:** After adding an item, `notEmpty.signal()` is called to wake up any waiting `take` operations.

```java
public static void put(BlockingQueue obj, Object item) throws InterruptedException {
    Objects.requireNonNull(item);
    obj.lock.lockInterruptibly();
    try {
        while (obj.size == obj.capacity) {
            obj.notFull.await();
        }
        obj.queue[obj.tail] = item;
        obj.tail = (obj.tail + 1) % obj.capacity;
        obj.size++;
        obj.notEmpty.signal();
    } finally {
        obj.lock.unlock();
    }
}
```

### `take` Method
The `take` method removes an item from the queue, waiting if necessary until an item becomes available.
- **Blocking on Empty Queue:** If the queue is empty, `notEmpty.await()` is called, blocking the thread until an item is available.
- **Circular Queue Implementation:** Uses `head` to retrieve and update the item in a circular manner.
- **Signaling After Removal:** After removal, `notFull.signal()` is called to allow any blocked `put` operations to proceed.

```java
public static Object take(BlockingQueue obj) throws InterruptedException {
    obj.lock.lockInterruptibly();
    try {
        while (obj.size == 0) {
            obj.notEmpty.await();
        }
        Object item = obj.queue[obj.head];
        obj.head = (obj.head + 1) % obj.capacity;
        obj.size--;
        obj.notFull.signal();
        return item;
    } finally {
        obj.lock.unlock();
    }
}
```

### `add` Method
The `add` method attempts to add an item without blocking; if the queue is full, it throws an `IllegalStateException`.

```java
public static boolean add(BlockingQueue obj, Object item) {
    Objects.requireNonNull(item);
    obj.lock.lock();
    try {
        if (obj.size == obj.capacity) {
            throw new IllegalStateException("Queue is full");
        }
        obj.queue[obj.tail] = item;
        obj.tail = (obj.tail + 1) % obj.capacity;
        obj.size++;
        obj.notEmpty.signal();
        return true;
    } finally {
        obj.lock.unlock();
    }
}
```

### `offer` Method
This method attempts to add an item without blocking, returning `true` if successful and `false` if the queue is full.

```java
public static boolean offer(BlockingQueue obj, Object item) {
    Objects.requireNonNull(item);
    obj.lock.lock();
    try {
        if (obj.size == obj.capacity) {
            return false;
        }
        obj.queue[obj.tail] = item;
        obj.tail = (obj.tail + 1) % obj.capacity;
        obj.size++;
        obj.notEmpty.signal();
        return true;
    } finally {
        obj.lock.unlock();
    }
}
```

### `contains` Method
This method checks if an item exists in the queue.

```java
public static boolean contains(BlockingQueue obj, Object item) {
    Objects.requireNonNull(item);
    obj.lock.lock();
    try {
        for (int i = 0; i < obj.size; i++) {
            if (obj.queue[(obj.head + i) % obj.capacity].equals(item)) {
                return true;
            }
        }
        return false;
    } finally {
        obj.lock.unlock();
    }
}
```

### `peek` Method
The `peek` method retrieves the head of the queue without removing it.

```java
public static Object peek(BlockingQueue obj) {
    obj.lock.lock();
    try {
        if (obj.size == 0) {
            return null;
        }
        return obj.queue[obj.head];
    } finally {
        obj.lock.unlock();
    }
}
```

### Utility Methods: `isEmpty`, `isFull`, and `size`
```java
public static boolean isEmpty(BlockingQueue obj) {
    obj.lock.lock();
    try {
        return obj.size == 0;
    } finally {
        obj.lock.unlock();
    }
}

public static boolean isFull(BlockingQueue obj) {
    obj.lock.lock();
    try {
        return obj.size == obj.capacity;
    } finally {
        obj.lock.unlock();
    }
}

public static int size(BlockingQueue obj) {
    obj.lock.lock();
    try {
        return obj.size;
    } finally {
        obj.lock.unlock();
    }
}
```
