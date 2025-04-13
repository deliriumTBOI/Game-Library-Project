package com.gamelib.gamelib.cache;

import jakarta.annotation.PreDestroy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LruCache<K, V> {
    private final Map<K, V> cache;
    private final ScheduledExecutorService executor;
    private final long maxAgeInMillis;
    private final int maxSize;
    private final String cacheName;

    private long hits = 0;
    private long misses = 0;
    private long puts = 0;
    private long removals = 0;

    public LruCache() {
        this(60000, 1000);
    }

    public LruCache(long maxAgeInMillis, int maxSize) {
        this(maxAgeInMillis, maxSize, "DefaultCache");
    }

    public LruCache(long maxAgeInMillis, int maxSize, String cacheName) {
        this.maxAgeInMillis = maxAgeInMillis;
        this.maxSize = maxSize;
        this.cacheName = cacheName;
        this.executor = Executors.newScheduledThreadPool(1);

        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                boolean shouldRemove = size() > maxSize;
                if (shouldRemove) {
                    log.debug("[{}] Removing eldest entry due to capacity limit: "
                                    + "{} (Cache size: {})",
                            cacheName, keyToString(eldest.getKey()), size());
                    removals++;
                }
                return shouldRemove;
            }
        };

        log.info("[{}] Cache initialized with TTL: {} ms, max size: {}",
                cacheName, maxAgeInMillis, maxSize);

        executor.scheduleAtFixedRate(this::logCacheStats, 1, 5, TimeUnit.MINUTES);
    }

    public synchronized void put(K key, V value) {
        cache.put(key, value);
        puts++;
        log.debug("[{}] Added entry: {} (Cache size: {})",
                cacheName, keyToString(key), cache.size());

        executor.schedule(() -> {
            synchronized (this) {
                if (cache.containsKey(key)) {
                    V removed = cache.remove(key);
                    if (removed != null) {
                        removals++;
                        log.debug("[{}] Auto-removed entry: {} after TTL expired (Cache size: {})",
                                cacheName, keyToString(key), cache.size());
                    }
                }
            }
        }, maxAgeInMillis, TimeUnit.MILLISECONDS);
    }

    public synchronized V get(K key) {
        V value = cache.get(key);

        if (value != null) {
            hits++;
            log.debug("[{}] Cache HIT: {} (Cache size: {})",
                    cacheName, keyToString(key), cache.size());
        } else {
            misses++;
            log.debug("[{}] Cache MISS: {} (Cache size: {})",
                    cacheName, keyToString(key), cache.size());
        }

        return value;
    }

    public synchronized void remove(K key) {
        V value = cache.remove(key);

        if (value != null) {
            removals++;
            log.debug("[{}] Manually removed entry: {} (Cache size: {})",
                    cacheName, keyToString(key), cache.size());
        }
    }

    public synchronized void clear() {
        int size = cache.size();
        cache.clear();
        removals += size;
        log.info("[{}] Cache cleared: {} entries removed", cacheName, size);
    }

    public synchronized int size() {
        int size = cache.size();
        log.debug("[{}] Current cache size: {}", cacheName, size);
        return size;
    }

    public synchronized boolean containsKey(K key) {
        boolean contains = cache.containsKey(key);
        log.debug("[{}] Checked for key {} in cache: {} (Cache size: {})",
                cacheName, keyToString(key), contains, cache.size());
        return contains;
    }

    public synchronized void logCacheStats() {
        log.info("[{}] Cache stats: size={}/{}, hits={}, misses={}, "
                        + "puts={}, removals={}, hit ratio={}%",
                cacheName, cache.size(), maxSize, hits, misses, puts, removals,
                calculateHitRatio());
    }

    @PreDestroy
    public void shutdown() {
        log.info("[{}] Shutting down cache executor...", cacheName);

        logCacheStats();

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private double calculateHitRatio() {
        long totalRequests = hits + misses;
        if (totalRequests == 0) {
            return 0.0;
        }
        return Math.round(((double) hits / totalRequests) * 100.0);
    }

    private String keyToString(K key) {
        if (key == null) {
            return "null";
        }
        String str = key.toString();
        return truncateIfNeeded(str);
    }

    private String truncateIfNeeded(String str) {
        int maxLength = 100;
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "... [truncated, total length: " + str.length() + "]";
    }
}