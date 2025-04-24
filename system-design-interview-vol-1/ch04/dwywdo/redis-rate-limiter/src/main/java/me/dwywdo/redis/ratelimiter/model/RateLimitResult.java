package me.dwywdo.redis.ratelimiter.model;

public record RateLimitResult(boolean blocked, Long ttl) { }
