package org.example;

import java.util.Objects;
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

public class BlockingQueue<T> {
    public final T[] queue;
    private int head = 0;
    private int tail = 0; // next available slot
    private int size = 0;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    @SuppressWarnings("unchecked")
    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        queue = (T[]) new Object[capacity];
    }

    /**
     * Inserts the specified element into this queue if it is possible to do so
     * immediately without violating capacity restrictions
     * <p>
     * if no space is currently available. return IllegalStateException
     */
    public boolean add(T item) {
        Objects.requireNonNull(item);
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

    /**
     * Inserts the specified element at the tail of this queue
     * if it is possible to do so immediately without exceeding the queue's capacity
     * <p>
     * return true upon success
     * return false if this queue is full.
     */
    public boolean offer(T item) {
        Objects.requireNonNull(item);
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

    /**
     * Inserts the specified element at the tail of this queue,
     * waiting up to the specified wait time for space to become available if the queue is full
     * <p>
     * return true if successful
     * false if the specified waiting time elapses before space is available
     * <p>
     * Throw InterruptedException if interrupted while waiting by other threads
     */
    public boolean offer(T item, long timeout) throws InterruptedException {
        Objects.requireNonNull(item);
        // lock.lock() ignores interruptions and keeps trying to acquire the lock.
        // lock.lockInterruptibly If we want to support interruption, we should use lockInterruptibly instead of lock()
        lock.lockInterruptibly();
        try {
            long nanos = timeout * 1_000_000;
            while (size == capacity) {
                if (nanos <= 0) {
                    return false;
                }
                nanos = notFull.awaitNanos(nanos); // return remaining time to wait if waked up by signal
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

    public void put(T item) throws InterruptedException {
        Objects.requireNonNull(item);
        // lock.lock() ignores interruptions and keeps trying to acquire the lock.
        // lock.lockInterruptibly If we want to support interruption, we should use lockInterruptibly instead of lock()
        lock.lockInterruptibly();
        try {
            while (size == capacity) {
                notFull.await(); // This can throw InterruptedException when system interrupt the thread
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
        lock.lockInterruptibly();
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
        Objects.requireNonNull(item);
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
