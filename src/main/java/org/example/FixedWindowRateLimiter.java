package org.example;


//import java.util.concurrent.atomic;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicInteger;


public class FixedWindowRateLimiter implements RateLimiter {
    //Allowed requests
    private int threshold;
    private volatile long windowstarttime;
    private final long windowunit = 1000L;
    private final AtomicInteger counter;

    public FixedWindowRateLimiter(int threshold) {
        this.threshold = threshold;
        this.windowstarttime = System.currentTimeMillis();
        counter = new AtomicInteger();
    }


    @Override
    public boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        if(currentTime - windowstarttime >= windowunit) {
            if(currentTime - windowstarttime > windowunit) {
                counter.set(0);
                windowstarttime = currentTime;
            }
        }

        return counter.incrementAndGet() <= threshold;
    }
}
