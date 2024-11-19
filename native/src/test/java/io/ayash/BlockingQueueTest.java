package io.ayash;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BlockingQueueTest {

    // ----------------------- NullPointer tests --------------------------------
    @Test
    void checkNullPointerInOffer() {
        BlockingQueue queue = new BlockingQueue(5);
        assertThrows(NullPointerException.class, () -> BlockingQueue.offer(queue, null));
    }

    @Test
    void checkNullPointerInAdd() {
        BlockingQueue queue = new BlockingQueue(5);
        assertThrows(NullPointerException.class, () -> BlockingQueue.add(queue, null));
    }

    @Test
    void checkNullPointerInPut() {
        BlockingQueue queue = new BlockingQueue(5);
        assertThrows(NullPointerException.class, () -> BlockingQueue.put(queue, null));
    }

    // ----------------------- Test Initial State ---------------------
    @Test
    public void testInitialState() {
        BlockingQueue queue = new BlockingQueue(5);
        assertTrue(BlockingQueue.isEmpty(queue));
        assertEquals(0, BlockingQueue.size(queue));
        assertFalse(BlockingQueue.isFull(queue));
        assertNull(BlockingQueue.peek(queue));
    }

    // ----------------------- Test add and remove elements ---------------------
    @Test
    public void testAddAndRemoveElements() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(5);

        BlockingQueue.put(queue, "1");
        BlockingQueue.put(queue, "2");
        BlockingQueue.put(queue, "3");

        assertEquals(3, BlockingQueue.size(queue));
        assertFalse(BlockingQueue.isEmpty(queue));
        assertFalse(BlockingQueue.isFull(queue));

        BlockingQueue.put(queue, "4");
        BlockingQueue.put(queue, "5");

        assertEquals(5, BlockingQueue.size(queue));
        assertFalse(BlockingQueue.isEmpty(queue));
        assertTrue(BlockingQueue.isFull(queue));

        // try to add more elements
        assertFalse(BlockingQueue.offer(queue, "6"));

        assertEquals("1", BlockingQueue.take(queue));
        assertEquals("2", BlockingQueue.take(queue));
        assertEquals("3", BlockingQueue.take(queue));
        assertEquals("4", BlockingQueue.take(queue));
        assertEquals("5", BlockingQueue.take(queue));

        assertEquals(0, BlockingQueue.size(queue));
        assertTrue(BlockingQueue.isEmpty(queue));
        assertFalse(BlockingQueue.isFull(queue));
        assertNull(BlockingQueue.peek(queue));
    }

    // ----------------------- Blocking Tests ---------------------
    @Test
    public void testBlockingOnFullQueue() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(2);
        BlockingQueue.put(queue, 1);
        BlockingQueue.put(queue, 2);

        Thread putThread = new Thread(() -> {
            try {
                BlockingQueue.put(queue, 3);  // This should block since the queue is full
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        putThread.start();

        // Ensure that the putThread is blocked by checking that it's still alive
        Thread.sleep(500);  // Short sleep to give the thread a chance to block
        assertTrue(putThread.isAlive());

        // Now take an element from the queue to unblock the putThread
        BlockingQueue.take(queue);
        Thread.sleep(500);  // Give some time for putThread to complete
        assertFalse(putThread.isAlive());
    }

    @Test
    public void testBlockingOnEmptyQueue() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(2);

        Thread takeThread = new Thread(() -> {
            try {
                BlockingQueue.take(queue);  // This should block since the queue is empty
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        takeThread.start();

        // Ensure that the takeThread is blocked by checking that it's still alive
        Thread.sleep(500);  // Short sleep to give the thread a chance to block
        assertTrue(takeThread.isAlive());

        // Now add an element to the queue to unblock the takeThread
        BlockingQueue.put(queue, 1);
        Thread.sleep(500);  // Give some time for takeThread to complete
        assertFalse(takeThread.isAlive());
    }

    // ----------------------- Test concurrent access ---------------------
    @Test
    public void testConcurrentAccess() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(5);

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(new Random().nextInt(10));
                    BlockingQueue.put(queue, i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(new Random().nextInt(10));
                    BlockingQueue.take(queue);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        assertTrue(BlockingQueue.isEmpty(queue));
    }

    // ----------------------- Test Timeout ---------------------
    @Test
    public void testOfferWithTimeoutQueueIsFull() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(2);
        BlockingQueue.put(queue, 1);
        BlockingQueue.put(queue, 2);

        long start = System.currentTimeMillis();
        assertFalse(BlockingQueue.offer(queue, 3, 1000));  // This should return false since the queue is full
        long end = System.currentTimeMillis();

        assertTrue(end - start >= 1000); // Should take at least 1 second
        assertTrue(end - start < 1200);  // But not more than 1.2 seconds
    }

    @Test
    public void testOfferWithTimeoutQueueGetsAvailable() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(2);
        BlockingQueue.put(queue, 1);
        BlockingQueue.put(queue, 2);

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(500);
                BlockingQueue.take(queue);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread.start();
        long start = System.currentTimeMillis();
        assertTrue(BlockingQueue.offer(queue, 3, 1000));
        long end = System.currentTimeMillis();

        assertFalse(end - start < 500);  // Should not take less than 0.5 seconds
        assertFalse(end - start > 1000);  // Should not take more than 1 second
    }

    // ----------------------- Test add ---------------------
    @Test
    public void testAdd() {
        BlockingQueue queue = new BlockingQueue(2);
        assertTrue(BlockingQueue.add(queue, 1));
        assertTrue(BlockingQueue.add(queue, 2));
        assertThrows(IllegalStateException.class, () -> BlockingQueue.add(queue, 3));  // This should throw an exception since the queue is full
    }

    // ----------------------- Test contains ---------------------
    @Test
    public void testContains() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(5);
        BlockingQueue.put(queue, 1);
        BlockingQueue.put(queue, 2);
        BlockingQueue.put(queue, 3);

        assertTrue(BlockingQueue.contains(queue, 1));
        assertTrue(BlockingQueue.contains(queue, 2));
        assertTrue(BlockingQueue.contains(queue, 3));
        assertFalse(BlockingQueue.contains(queue, 4));
    }

    // ----------------------- Test offer ---------------------
    @Test
    public void testOffer() {
        BlockingQueue queue = new BlockingQueue(2);
        assertTrue(BlockingQueue.offer(queue, 1));
        assertTrue(BlockingQueue.offer(queue, 2));
        assertFalse(BlockingQueue.offer(queue, 3));  // This should return false since the queue is full
    }

    // ----------------------- Test peek ---------------------
    @Test
    public void testPeek() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(5);
        BlockingQueue.put(queue, 1);
        BlockingQueue.put(queue, 2);
        BlockingQueue.put(queue, 3);

        assertEquals(1, BlockingQueue.peek(queue));
        assertEquals(1, BlockingQueue.peek(queue));
        assertEquals(1, BlockingQueue.take(queue));
        assertEquals(2, BlockingQueue.peek(queue));
        assertEquals(2, BlockingQueue.take(queue));
        assertEquals(3, BlockingQueue.peek(queue));
        assertEquals(3, BlockingQueue.take(queue));
        assertNull(BlockingQueue.peek(queue));
    }

    // ----------------------- Test isEmpty ---------------------
    @Test
    public void testIsEmpty() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(5);
        assertTrue(BlockingQueue.isEmpty(queue));
        BlockingQueue.put(queue, 1);
        assertFalse(BlockingQueue.isEmpty(queue));
        BlockingQueue.put(queue, 2);
        assertFalse(BlockingQueue.isEmpty(queue));
        BlockingQueue.take(queue);
        assertFalse(BlockingQueue.isEmpty(queue));
        BlockingQueue.take(queue);
        assertTrue(BlockingQueue.isEmpty(queue));
    }

    // ----------------------- Test isFull ---------------------
    @Test
    public void testIsFull() throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(2);
        assertFalse(BlockingQueue.isFull(queue));
        BlockingQueue.put(queue, 1);
        assertFalse(BlockingQueue.isFull(queue));
        BlockingQueue.put(queue, 2);
        assertTrue(BlockingQueue.isFull(queue));
        BlockingQueue.take(queue);
        assertFalse(BlockingQueue.isFull(queue));
        BlockingQueue.take(queue);
        assertFalse(BlockingQueue.isFull(queue));
    }


//
}