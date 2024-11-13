package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Available methods:
// - add(Object item): boolean
// - contains(Object item): boolean
// - offer(Object item): boolean
// - offer(Object item, long timeout ms): boolean
// - put(Object item): void
// - take(): Object

// - peek(): Object
// - isEmpty(): boolean
// - isFull(): boolean

public class BlockingQueue {
    public final Object[] queue;
    private int head = 0;
    private int tail = 0; // next available slot
    private int size = 0;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        queue = (Object[]) new Object[capacity];
    }

    public boolean add(Object item) {
        lock.lock();
        try {
            if (size == capacity) {
                throw new IllegalStateException("Queue is full");
            }
            queue[tail] = item;
            tail = (tail + 1) % capacity;
            size++;
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(Object item) {
        lock.lock();
        try {
            if (size == capacity) {
                return false;
            }
            queue[tail] = item;
            tail = (tail + 1) % capacity;
            size++;
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(Object item, long timeout) throws InterruptedException {
        lock.lock();
        try {
            long nanos = timeout * 1_000_000;
            while (size == capacity) {
                if (nanos <= 0) {
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            queue[tail] = item;
            tail = (tail + 1) % capacity;
            size++;
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void put(Object item) throws InterruptedException {
        lock.lock();
        try {
            while (size == capacity) {
                notFull.await();
            }
            queue[tail] = item;
            tail = (tail + 1) % capacity;
            size++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Object take() throws InterruptedException {
        lock.lock();
        try {
            while (size == 0) {
                notEmpty.await();
            }
            Object item = queue[head];
            head = (head + 1) % capacity;
            size--;
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(Object item) {
        lock.lock();
        try {
            for (int i = 0; i < size; i++) {
                if (queue[(head + i) % capacity].equals(item)) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public Object peek() {
        lock.lock();
        try {
            if (size == 0) {
                return null;
            }
            return queue[head];
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return size == 0;
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        lock.lock();
        try {
            return size == capacity;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return size;
        } finally {
            lock.unlock();
        }
    }

}
