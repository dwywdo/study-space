package me.dwywdo.consistent_hashing;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConsistentHashingTest {
    public static void main(String[] args) {
        final HashFunction md5 = new MD5Hash();
        final ConsistentHashRouter router = new ConsistentHashRouter(md5, 10_000); // 가상 노드 1000개

        router.addNode(new Node("Server-A"));
        router.addNode(new Node("Server-B"));
        router.addNode(new Node("Server-C"));

        // 1. 초기 분포 측정
        final Map<String, Integer> dist = measureDistribution(router, 10_000);
        System.out.println("초기 분포: " + dist);

        // 2. 노드 추가 후 분포 변화
        router.addNode(new Node("Server-D"));
        final Map<String, Integer> newDist = measureDistribution(router, 10_000);
        System.out.println("노드 추가 후: " + newDist);

        // 3. 노드 제거 시 분포
        router.removeNode(new Node("Server-B"));
        final Map<String, Integer> removedDist = measureDistribution(router, 10_000);
        System.out.println("노드 제거 후: " + removedDist);
    }

    private static Map<String, Integer> measureDistribution(ConsistentHashRouter router, int samples) {
        final Map<String, Integer> dist = new HashMap<>();
        for (int i = 0; i < samples; i++) {
            final String key = "key-" + UUID.randomUUID();
            final Node node = router.route(key);
            dist.put(node.getId(), dist.getOrDefault(node.getId(), 0) + 1);
        }
        return dist;
    }
}
