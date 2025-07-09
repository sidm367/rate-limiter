package org.example;

public interface RateLimiter {
    public boolean tryAcquire();
}
