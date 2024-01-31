package by.clevertec.proxy.cache.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LfuCacheTest {

    public static final String ONE = "One";
    public static final String TWO = "Two";
    public static final String THREE = "Three";

    @Test
    public void testPut() {
        LfuCache<Integer, String> lfuCache = new LfuCache<>(3);
        lfuCache.put(1, ONE);
        lfuCache.put(2, TWO);
        lfuCache.put(3, THREE);
        assertEquals(ONE, lfuCache.get(1));
        lfuCache.put(4, "Four");
        assertNull(lfuCache.get(2));
    }

    @Test
    public void testGet() {
        LfuCache<Integer, String> lfuCache = new LfuCache<>(3);
        lfuCache.put(1, ONE);
        lfuCache.put(2, TWO);
        lfuCache.put(3, THREE);
        assertEquals(ONE, lfuCache.get(1));
        assertEquals(TWO, lfuCache.get(2));
        assertEquals(THREE, lfuCache.get(3));
    }

    @Test
    public void testRemove() {
        LfuCache<Integer, String> lfuCache = new LfuCache<>(3);
        lfuCache.put(1, ONE);
        lfuCache.put(2, TWO);
        lfuCache.put(3, THREE);
        lfuCache.remove(2);
        assertNull(lfuCache.get(2));
    }
}
