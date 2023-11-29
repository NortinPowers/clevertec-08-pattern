package by.clevertec.proxy.cache.impl;

import by.clevertec.proxy.cache.Cache;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;

@Getter
public class LfuCache<K, V> implements Cache<K, V> {

    private final int maxSize;
    private final Map<K, V> cacheMap;
    private final Map<K, Integer> keyFrequency;
    private final Map<Integer, Set<K>> frequencyKeys;
    private int minFrequency;

    public LfuCache(int maxSize) {
        this.maxSize = maxSize;
        this.cacheMap = new HashMap<>();
        this.keyFrequency = new HashMap<>();
        this.frequencyKeys = new HashMap<>();
        this.minFrequency = 0;
        this.frequencyKeys.put(1, new LinkedHashSet<>());
    }

    /**
     * Кладет в Map кэша объект. При необходимости вызывает метод удаления избыточного объекта из Map кэша.
     *
     * @param key   - ключ объекта для Map кэша.
     * @param value - объект по ключу в Map кэша.
     */
    @Override
    public void put(K key, V value) {
        if (maxSize > 0) {
            if (cacheMap.containsKey(key)) {
                cacheMap.put(key, value);
            } else {
                if (cacheMap.size() >= maxSize) {
                    evict();
                }
                cacheMap.put(key, value);
                keyFrequency.put(key, 1);
                frequencyKeys.get(1).add(key);
                minFrequency = 1;
            }
        }
    }

    /**
     * Возвращает объект по ключу из Map кэша. Обновляет значение частоты использования для возвращенного объекта. Обновляет Map частот использования.
     *
     * @param key - ключ объекта для Map кэша.
     * @return объект по переданному в Map ключу.
     */
    @Override
    public V get(K key) {
        if (!cacheMap.containsKey(key)) {
            return null;
        }
        int frequency = keyFrequency.get(key);
        keyFrequency.put(key, frequency + 1);
        frequencyKeys.get(frequency).remove(key);
        if (frequency == minFrequency && frequencyKeys.get(frequency).isEmpty()) {
            minFrequency++;
        }
        if (!frequencyKeys.containsKey(frequency + 1)) {
            frequencyKeys.put(frequency + 1, new LinkedHashSet<>());
        }
        frequencyKeys.get(frequency + 1).add(key);
        return cacheMap.get(key);
    }

    /**
     * Удаляет ключ-объект из Map кэша и и обновляет информацию о частоте использования ключей.
     *
     * @param key - ключ удаляемый из кэша.
     */
    @Override
    public void remove(K key) {
        if (cacheMap.containsKey(key)) {
            int frequency = keyFrequency.get(key);
            keyFrequency.remove(key);
            frequencyKeys.get(frequency).remove(key);
            cacheMap.remove(key);
            if (frequency == minFrequency && frequencyKeys.get(frequency).isEmpty()) {
                minFrequency++;
            }
        }
    }

    /**
     * Удаляет ключ-объект с наименьшей частотой использования из кеша.
     */
    private void evict() {
        K keyToRemove = frequencyKeys.get(minFrequency).iterator().next();
        frequencyKeys.get(minFrequency).remove(keyToRemove);
        cacheMap.remove(keyToRemove);
        keyFrequency.remove(keyToRemove);
    }
}
