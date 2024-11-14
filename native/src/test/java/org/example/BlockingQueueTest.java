package org.example;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BlockingQueueTest {

    // ----------------------- NullPointer tests --------------------------------
    @Test
    void checkNullPointerInOffer() {
        BlockingQueue<String> queue = new BlockingQueue<>(5);
        assertThrows(NullPointerException.class, () -> queue.offer(null));
    }

    @Test
    void checkNullPointerInAdd() {
        BlockingQueue<String> queue = new BlockingQueue<>(5);
        assertThrows(NullPointerException.class, () -> queue.add(null));
    }

    @Test
    void checkNullPointerInPut() {
        BlockingQueue<String> queue = new BlockingQueue<>(5);
        assertThrows(NullPointerException.class, () -> queue.put(null));
    }

    // ----------------------- Test Initial State ---------------------
    @Test
    public void testInitialState() {
        BlockingQueue<String> queue = new BlockingQueue<>(5);
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertFalse(queue.isFull());
        assertNull(queue.peek());
    }

    // ----------------------- Test add and remove elements ---------------------
    @Test
    public void testAddAndRemoveElements() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(5);

        queue.put("1");
        queue.put("2");
        queue.put("3");

        assertEquals(3, queue.size());
        assertFalse(queue.isEmpty());
        assertFalse(queue.isFull());

        queue.put("4");
        queue.put("5");

        assertEquals(5, queue.size());
        assertFalse(queue.isEmpty());
        assertTrue(queue.isFull());

        // try to add more elements
        assertFalse(queue.offer("6"));

        assertEquals("1", queue.take());
        assertEquals("2", queue.take());
        assertEquals("3", queue.take());
        assertEquals("4", queue.take());
        assertEquals("5", queue.take());

        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
        assertFalse(queue.isFull());
        assertNull(queue.peek());
    }

    // ----------------------- Blocking Tests ---------------------
    @Test
    public void testBlockingOnFullQueue() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(2);
        queue.put(1);
        queue.put(2);

        Thread putThread = new Thread(() -> {
            try {
                queue.put(3);  // This should block since the queue is full
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        putThread.start();

        // Ensure that the putThread is blocked by checking that it's still alive
        Thread.sleep(500);  // Short sleep to give the thread a chance to block
        assertTrue(putThread.isAlive());

        // Now take an element from the queue to unblock the putThread
        queue.take();
        Thread.sleep(500);  // Give some time for putThread to complete
        assertFalse(putThread.isAlive());
    }

    @Test
    public void testBlockingOnEmptyQueue() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(2);

        Thread takeThread = new Thread(() -> {
            try {
                queue.take();  // This should block since the queue is empty
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        takeThread.start();

        // Ensure that the takeThread is blocked by checking that it's still alive
        Thread.sleep(500);  // Short sleep to give the thread a chance to block
        assertTrue(takeThread.isAlive());

        // Now add an element to the queue to unblock the takeThread
        queue.put(1);
        Thread.sleep(500);  // Give some time for takeThread to complete
        assertFalse(takeThread.isAlive());
    }

    // ----------------------- Test concurrent access ---------------------
    @Test
    public void testConcurrentAccess() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(new Random().nextInt(10));
                    queue.put(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(new Random().nextInt(10));
                    queue.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        assertTrue(queue.isEmpty());
    }

    // ----------------------- Test Timeout ---------------------
    @Test
    public void testOfferWithTimeoutQueueIsFull() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(2);
        queue.put(1);
        queue.put(2);

        long start = System.currentTimeMillis();
        assertFalse(queue.offer(3, 1000));  // This should return false since the queue is full
        long end = System.currentTimeMillis();

        assertTrue(end - start >= 1000); // Should take at least 1 second
        assertTrue(end - start < 1200);  // But not more than 1.2 seconds
    }

    @Test
    public void testOfferWithTimeoutQueueGetsAvailable() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(2);
        queue.put(1);
        queue.put(2);

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(500);
                queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread.start();
        long start = System.currentTimeMillis();
        assertTrue(queue.offer(3, 1000));
        long end = System.currentTimeMillis();

        assertFalse(end - start < 500);  // Should not take less than 0.5 seconds
        assertFalse(end - start > 1000);  // Should not take more than 1 second
    }

    // ----------------------- Test add ---------------------
    @Test
    public void testAdd() {
        BlockingQueue<Integer> queue = new BlockingQueue<>(2);
        assertTrue(queue.add(1));
        assertTrue(queue.add(2));
        assertThrows(IllegalStateException.class, () -> queue.add(3));  // This should throw an exception since the queue is full
    }

    // ----------------------- Test contains ---------------------
    @Test
    public void testContains() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        queue.put(1);
        queue.put(2);
        queue.put(3);

        assertTrue(queue.contains(1));
        assertTrue(queue.contains(2));
        assertTrue(queue.contains(3));
        assertFalse(queue.contains(4));
    }

    // ----------------------- Test offer ---------------------
    @Test
    public void testOffer() {
        BlockingQueue<Integer> queue = new BlockingQueue<>(2);
        assertTrue(queue.offer(1));
        assertTrue(queue.offer(2));
        assertFalse(queue.offer(3));  // This should return false since the queue is full
    }

    // ----------------------- Test peek ---------------------
    @Test
    public void testPeek() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        queue.put(1);
        queue.put(2);
        queue.put(3);

        assertEquals(1, queue.peek());
        assertEquals(1, queue.peek());
        assertEquals(1, queue.take());
        assertEquals(2, queue.peek());
        assertEquals(2, queue.take());
        assertEquals(3, queue.peek());
        assertEquals(3, queue.take());
        assertNull(queue.peek());
    }

    // ----------------------- Test isEmpty ---------------------
    @Test
    public void testIsEmpty() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        assertTrue(queue.isEmpty());
        queue.put(1);
        assertFalse(queue.isEmpty());
        queue.put(2);
        assertFalse(queue.isEmpty());
        queue.take();
        assertFalse(queue.isEmpty());
        queue.take();
        assertTrue(queue.isEmpty());
    }

    // ----------------------- Test isFull ---------------------
    @Test
    public void testIsFull() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(2);
        assertFalse(queue.isFull());
        queue.put(1);
        assertFalse(queue.isFull());
        queue.put(2);
        assertTrue(queue.isFull());
        queue.take();
        assertFalse(queue.isFull());
        queue.take();
        assertFalse(queue.isFull());
    }


//
}