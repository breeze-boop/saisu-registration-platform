package com.mishi.cache;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import org.springframework.stereotype.Component;

@Component
public class SimpleBloomFilter {
    private static final int SIZE = 1 << 20;
    private final BitSet bits = new BitSet(SIZE);
    private boolean empty = true;

    public void put(String value) {
        bits.set(hash(value, 17));
        bits.set(hash(value, 31));
        bits.set(hash(value, 131));
        empty = false;
    }

    public boolean mightContain(String value) {
        return empty || (bits.get(hash(value, 17)) && bits.get(hash(value, 31)) && bits.get(hash(value, 131)));
    }

    private int hash(String value, int seed) {
        int h = 0;
        for (byte b : value.getBytes(StandardCharsets.UTF_8)) h = h * seed + b;
        return (h & Integer.MAX_VALUE) % SIZE;
    }
}
