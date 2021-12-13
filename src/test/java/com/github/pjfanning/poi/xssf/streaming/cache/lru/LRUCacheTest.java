package com.github.pjfanning.poi.xssf.streaming.cache.lru;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LRUCacheTest {

    @Test
    public void getNonExistingElement() {
        LRUCache lruCache = new LRUCache<Integer, String>(2);
        assertNull(lruCache.get(1));
    }

    @Test
    public void testEvictElement() {
        LRUCache lruCache = new LRUCache<Integer, String>(2);
        lruCache.put(1, "text 1");
        lruCache.put(2, "text 2");
        lruCache.put(5, "text 5");

        assertNull(lruCache.get(1));
        assertEquals("text 2", lruCache.get(2));
        assertEquals("text 5", lruCache.get(5));
    }

    @Test
    public void testEvictLeastRecentlyUsed() {
        LRUCache lruCache = new LRUCache<Integer, String>(2);
        lruCache.put(1, "text 1");
        lruCache.put(2, "text 2");
        lruCache.get(1);
        lruCache.put(5, "text 5");

        assertNull(lruCache.get(2));
        assertEquals("text 1", lruCache.get(1));
        assertEquals("text 5", lruCache.get(5));
    }
}
