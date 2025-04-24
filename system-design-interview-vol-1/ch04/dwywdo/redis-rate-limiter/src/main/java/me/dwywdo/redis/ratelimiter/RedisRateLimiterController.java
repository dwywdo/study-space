package me.dwywdo.redis.ratelimiter;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.dwywdo.redis.ratelimiter.model.RateLimitResult;

@RestController
@RequiredArgsConstructor
public class RedisRateLimiterController {
    private final RedisScript<List> rateLimiterScript;
    private final RedisTemplate<String, String> stringRedisTemplate;

    private static final Clock clock = Clock.systemDefaultZone();
    private static final AtomicLong KEY_GENERATOR = new AtomicLong(clock.millis());
    private static final Integer BLOCK_PERIOD_IN_SECONDS = 5;
    private static final Integer REQUEST_LIMIT = 3;
    private static final Integer WINDOW_SIZE = 2;

    @GetMapping("/api")
    public ResponseEntity<String> api(HttpServletRequest request) throws JsonProcessingException {
        final String counterIpKey = request.getRemoteAddr();
        final Long now = clock.millis();
        final List<Long> result = stringRedisTemplate.execute(
                rateLimiterScript,
                List.of(counterIpKey),
                String.valueOf(BLOCK_PERIOD_IN_SECONDS),
                String.valueOf(now),
                String.valueOf(KEY_GENERATOR.incrementAndGet()),
                String.valueOf(REQUEST_LIMIT),
                String.valueOf(WINDOW_SIZE)
        );

        final RateLimitResult rateLimitResult = new RateLimitResult(
                result.get(0) == 1L, result.get(1)
        );

        if (rateLimitResult.blocked()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API Call Blocked. Try after " + rateLimitResult.ttl() + " seconds");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("API Call Allowed");
        }
    }
}
