package me.dwywdo.consistent_hashing;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash implements HashFunction {
    @Override
    public long hash(String key) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, digest).longValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
