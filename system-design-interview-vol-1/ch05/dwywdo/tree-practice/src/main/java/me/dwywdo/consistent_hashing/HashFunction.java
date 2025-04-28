package me.dwywdo.consistent_hashing;

public interface HashFunction {
    long hash(String key);
}
