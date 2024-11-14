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

public class BlockingQueue {
    public static void main(String[] args) {
        System.out.println("Blocking Queue!");
    }

    public final Object[] queue;
    private int head = 0;
    private int tail = 0; // next available slot
    private int size = 0;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        queue = new Object[capacity];
    }

    /**
     * Inserts the specified element into this queue if it is possible to do so
     * immediately without violating capacity restrictions
     * <p>
     * if no space is currently available. return IllegalStateException
     */
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

    /**
     * Inserts the specified element at the tail of this queue
     * if it is possible to do so immediately without exceeding the queue's capacity
     * <p>
     * return true upon success
     * return false if this queue is full.
     */
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

    /**
     * Inserts the specified element at the tail of this queue,
     * waiting up to the specified wait time for space to become available if the queue is full
     * <p>
     * return true if successful
     * false if the specified waiting time elapses before space is available
     * <p>
     * Throw InterruptedException if interrupted while waiting by other threads
     */
    public static boolean offer(BlockingQueue obj, Object item, long timeout) throws InterruptedException {
        Objects.requireNonNull(item);
        // lock.lock() ignores interruptions and keeps trying to acquire the lock.
        // lock.lockInterruptibly If we want to support interruption, we should use lockInterruptibly instead of lock()
        obj.lock.lockInterruptibly();
        try {
            long nanos = timeout * 1_000_000;
            while (obj.size == obj.capacity) {
                if (nanos <= 0) {
                    return false;
                }
                nanos = obj.notFull.awaitNanos(nanos); // return remaining time to wait if waked up by signal
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

    public static void put(BlockingQueue obj, Object item) throws InterruptedException {
        Objects.requireNonNull(item);
        // lock.lock() ignores interruptions and keeps trying to acquire the lock.
        // lock.lockInterruptibly If we want to support interruption, we should use lockInterruptibly instead of lock()
        obj.lock.lockInterruptibly();
        try {
            while (obj.size == obj.capacity) {
                obj.notFull.await(); // This can throw InterruptedException when system interrupt the thread
            }
            obj.queue[obj.tail] = item;
            obj.tail = (obj.tail + 1) % obj.capacity;
            obj.size++;
            obj.notEmpty.signal();
        } finally {
            obj.lock.unlock();
        }
    }

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

}
