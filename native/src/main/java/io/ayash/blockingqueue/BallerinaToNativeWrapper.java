package io.ayash.blockingqueue;

import io.ballerina.runtime.api.values.BError;
import io.ballerina.runtime.api.values.BObject;

public class BallerinaToNativeWrapper {
    private static final String NATIVE_QUEUE = "nativeQueue";

    private BallerinaToNativeWrapper() {
    }

    public static void init(BObject bBlockingQueue, int size) {
        BlockingQueue nativeBlockingQueue = new BlockingQueue(size);
        bBlockingQueue.addNativeData(NATIVE_QUEUE, nativeBlockingQueue);
    }

    public static Object put(BObject bBlockingQueue, Object item) {
        BlockingQueue nativeBlockingQueue = (BlockingQueue) bBlockingQueue.getNativeData(NATIVE_QUEUE);
        try {
            nativeBlockingQueue.put(item);
        } catch (Exception e) {

            String errorMsg = String.format("Error occurred while adding item to the queue: %s",
                    e.getMessage());

            return CommonUtils.createError(errorMsg, e);
        }
        return null;
    }

    public static Object take(BObject bBlockingQueue) {
        BlockingQueue nativeBlockingQueue = (BlockingQueue) bBlockingQueue.getNativeData(NATIVE_QUEUE);
        try {
            return nativeBlockingQueue.take();
        } catch (Exception e) {
            String errorMsg = String.format("Error occurred while taking item from the queue: %s",
                    e.getMessage());
            return CommonUtils.createError(errorMsg, e);
        }
    }

    public static Object offer(BObject bBlockingQueue, Object item) {
        BlockingQueue nativeBlockingQueue = (BlockingQueue) bBlockingQueue.getNativeData(NATIVE_QUEUE);
        try {
            return nativeBlockingQueue.offer(item);
        } catch (Exception e) {
            String errorMsg = String.format("Error occurred while offering item to the queue: %s",
                    e.getMessage());
            return CommonUtils.createError(errorMsg, e);
        }
    }

    public static Object add(BObject bBlockingQueue, Object item) {
        BlockingQueue nativeBlockingQueue = (BlockingQueue) bBlockingQueue.getNativeData(NATIVE_QUEUE);
        try {
            return nativeBlockingQueue.add(item);
        } catch (Exception e) {
            String errorMsg = String.format("Error occurred while adding item to the queue: %s",
                    e.getMessage());
            return CommonUtils.createError(errorMsg, e);
        }
    }

    public static boolean contains(BObject bBlockingQueue, Object item) {
        BlockingQueue nativeBlockingQueue = (BlockingQueue) bBlockingQueue.getNativeData(NATIVE_QUEUE);
        return nativeBlockingQueue.contains(item);
    }

    public static boolean isEmpty(BObject bBlockingQueue) {
        BlockingQueue nativeBlockingQueue = (BlockingQueue) bBlockingQueue.getNativeData(NATIVE_QUEUE);
        return nativeBlockingQueue.isEmpty();
    }

    public static int size(BObject bBlockingQueue) {
        BlockingQueue nativeBlockingQueue = (BlockingQueue) bBlockingQueue.getNativeData(NATIVE_QUEUE);
        return nativeBlockingQueue.size();
    }

    public static boolean isFull(BObject bBlockingQueue) {
        BlockingQueue nativeBlockingQueue = (BlockingQueue) bBlockingQueue.getNativeData(NATIVE_QUEUE);
        return nativeBlockingQueue.isFull();
    }

    public static Object peek(BObject bBlockingQueue) {
        BlockingQueue nativeBlockingQueue = (BlockingQueue) bBlockingQueue.getNativeData(NATIVE_QUEUE);
        return nativeBlockingQueue.peek();
    }

}
