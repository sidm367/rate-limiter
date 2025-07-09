
# Rate-Limited HTTP Server
A java HTTP server with fixed-window rate limiting policy.

- Fixed-window rate limiting (10 requests/second by default)
- HTTP 429 responses when limit is exceeded
- Simple API endpoint (`GET /`)

## How to Run
1. Compile:
   ```bash
   javac org/example/*.java
   ```
2. Start server:
   ```bash
   java org.example.MyHttpServer
   ```

## Testing Rate Limits
```bash
# Send 15 rapid requests (10 will pass, 5 will be blocked)
for i in {1..15}; do curl http://localhost:8000; done
```

## Configuration
Edit `FixedWindowRateLimiter.java` to change:
```java
public FixedWindowRateLimiter(10)  // Change 10 to your desired limit
```
