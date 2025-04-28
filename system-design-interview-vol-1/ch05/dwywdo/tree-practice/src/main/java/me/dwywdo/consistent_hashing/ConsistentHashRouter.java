package me.dwywdo.consistent_hashing;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Nullable;

public class ConsistentHashRouter {
    private final TreeMap<Long, Node> hashRing = new TreeMap<>();
    private final HashFunction hashFunction;
    private final int virtualNodeCount;

    public ConsistentHashRouter(HashFunction hashFunction, int virtualNodeCount) {
        this.hashFunction = hashFunction;
        this.virtualNodeCount = virtualNodeCount;
    }

    public void addNode(Node node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            final long hash = hashFunction.hash(node.getId() + '#' + i);
            hashRing.put(hash, node);
        }
    }

    public void removeNode(Node node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            final long hash = hashFunction.hash(node.getId() + '#' + i);
            hashRing.remove(hash);
        }
    }

    @Nullable
    public Node route(String key) {
        if (hashRing.isEmpty()) {
            return null;
        }

        final long hash = hashFunction.hash(key);

        // tailMap() returns all entries above given key (Hash value).
        final SortedMap<Long, Node> tailMap = hashRing.tailMap(hash);
        final Long targetHash = tailMap.isEmpty() ? hashRing.firstKey() : tailMap.firstKey();
        return hashRing.get(targetHash);
    }
}
