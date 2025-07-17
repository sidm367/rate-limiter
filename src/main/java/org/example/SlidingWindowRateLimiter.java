package org.example;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SlidingWindowRateLimiter implements RateLimiter{
    // Maximum number of requests allowed within the window duration.
    final int threshold;
    // Window duration in milliseconds. Here, it's set to 1 second.
    final long windowUnit = 1000L;
    // A queue to hold the timestamps of the recent requests within the window.
    final Queue<Long> log = new ConcurrentLinkedQueue<>();

    /**
     * Constructs a SlidingWindowRateLimiter with the specified threshold.
     *
     * @param threshold the maximum number of requests allowed per window.
     */
    public SlidingWindowRateLimiter(int threshold) {
        this.threshold = threshold;
    }

    /**
     * Tries to acquire permission for a request based on the rate limit.
     *
     * @return true if the request is within the rate limit; false otherwise.
     */
    @Override
    public boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        // Evict expired timestamps from the head of the queue.
        while (!log.isEmpty() && (currentTime - log.peek() > windowUnit)) {
            log.poll();
        }

        // If the queue size is below the threshold, the request is allowed.
        if (log.size() < threshold) {
            log.offer(currentTime);  // Record the timestamp of the allowed request.
            return true;
        }
        // If the queue is full, the request is rejected.
        return false;
    }
}