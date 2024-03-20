package by.clevertec.proxy.util;

import by.clevertec.proxy.cache.Cache;
import by.clevertec.proxy.mapper.ProductMapper;
import by.clevertec.proxy.proxy.CacheableAspect;
import by.clevertec.proxy.repository.ProductRepository;
import java.lang.reflect.Field;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CacheableAspectUtil {

    public static void setCacheableAspectField(CacheableAspect cacheableAspect, Cache<UUID, Object> cache,
                                               ProductRepository productRepository, ProductMapper mapper) throws Exception {
        Field cacheField = CacheableAspect.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        cacheField.set(cacheableAspect, cache);
        Field productRepositoryField = CacheableAspect.class.getDeclaredField("productRepository");
        productRepositoryField.setAccessible(true);
        productRepositoryField.set(cacheableAspect, productRepository);
        Field mapperField = CacheableAspect.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        mapperField.set(cacheableAspect, mapper);
    }
}
