package by.clevertec.proxy.cache.impl;

import by.clevertec.proxy.cache.Cache;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class LruCache<K, V> implements Cache<K, V> {

    private final Map<K, V> cacheMap;
    private final int capacity;

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.cacheMap = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    /**
     * Кладет в Map кэша объект.
     *
     * @param key   - ключ объекта для Map кэша.
     * @param value - объект по ключу в Map кэша.
     */
    @Override
    public void put(K key, V value) {
        cacheMap.put(key, value);
    }

    /**
     * Возвращает объект по ключу из Map кэша.
     *
     * @param key - ключ объекта для Map кэша.
     * @return объект по переданному в Map ключу.
     */
    @Override
    public V get(K key) {
        return cacheMap.get(key);
    }

    /**
     * Удаляет объект по ключу из Map кэша.
     *
     * @param key - ключ удаляемого объекта для Map.
     */
    @Override
    public void remove(K key) {
        cacheMap.remove(key);
    }
}
