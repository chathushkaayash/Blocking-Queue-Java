package io.ayash.blockingqueue;

import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue {
    public final Object[] queue;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private int head = 0;
    private int tail = 0; // next available slot
    private int size = 0;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        queue = new Object[capacity];
    }

    public void put(Object item) throws InterruptedException {
        Objects.requireNonNull(item);
        lock.lockInterruptibly();
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

    public boolean add(Object item) {
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

    public boolean offer(Object item) {
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

    public boolean offer(Object item, long timeout) throws InterruptedException {
        Objects.requireNonNull(item);
        lock.lockInterruptibly();
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
