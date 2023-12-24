package io.github.a5h73y.parkour.utility.cache;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic Cache.
 * Simply holds reference to a value using a Key for a set amount of time.
 * Found at: https://medium.com/@marcellogpassos/creating-a-simple-and-generic-cache-manager-in-java-e62e4204a10e
 * @param <K> key type
 * @param <V> value type
 */
public class GenericCache<K, V> implements IGenericCache<K, V> {

    public static final Long DEFAULT_CACHE_TIMEOUT = 15L;

    protected ConcurrentMap<K, CacheValue<V>> cacheMap;
    protected final Long cacheTimeout;

    public GenericCache() {
        this(DEFAULT_CACHE_TIMEOUT);
    }

    public GenericCache(Long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
        this.clear();
    }

    @Override
    public void clean() {
        for (K key: this.getExpiredKeys()) {
            this.remove(key);
        }
    }

    @Override
    public boolean containsKey(K key) {
        return this.cacheMap.containsKey(key);
    }

    protected Set<K> getExpiredKeys() {
        return this.cacheMap.keySet()
                .parallelStream()
                .filter(Objects::nonNull)
                .filter(this::isExpired)
                .collect(Collectors.toSet());
    }

    protected boolean isExpired(K key) {
        boolean result = true;

        CacheValue<V> cacheValue = this.cacheMap.get(key);
        if (cacheValue != null) {
            LocalDateTime expirationDateTime = cacheValue.getCreatedAt().plusSeconds(this.cacheTimeout);
            result = LocalDateTime.now().isAfter(expirationDateTime);
        }
        return result;
    }

    @Override
    public void clear() {
        this.cacheMap = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<V> get(K key) {
        this.clean();
        return Optional.ofNullable(this.cacheMap.get(key)).map(CacheValue::getValue);
    }

    @Override
    public void put(K key, V value) {
        this.cacheMap.put(key, this.createCacheValue(value));
    }

    public V computeIfAbsent(K key, Function<K, CacheValue<V>> boop) {
        return this.cacheMap.computeIfAbsent(key, boop).getValue();
    }

    public CacheValue<V> createCacheValue(V value) {
        LocalDateTime now = LocalDateTime.now();
        return new CacheValue<>() {
            @Override
            public V getValue() {
                return value;
            }

            @Override
            public LocalDateTime getCreatedAt() {
                return now;
            }
        };
    }

    @Override
    public void remove(K key) {
        this.cacheMap.remove(key);
    }

    public ConcurrentMap<K, CacheValue<V>> getCacheMap() {
        return cacheMap;
    }

}
