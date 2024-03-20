package by.clevertec.proxy.proxy;

import by.clevertec.proxy.cache.Cache;
import by.clevertec.proxy.cache.impl.LfuCache;
import by.clevertec.proxy.cache.impl.LruCache;
import by.clevertec.proxy.config.AppConfig;
import by.clevertec.proxy.data.InfoProductDto;
import by.clevertec.proxy.data.ProductDto;
import by.clevertec.proxy.entity.Product;
import by.clevertec.proxy.exception.ProductNotFoundException;
import by.clevertec.proxy.mapper.ProductMapper;
import by.clevertec.proxy.mapper.ProductMapperImpl;
import by.clevertec.proxy.repository.ProductRepository;
import by.clevertec.proxy.repository.impl.ProductRepositoryImpl;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@NoArgsConstructor
public class CacheableAspect {

    private Cache<UUID, Object> cache = configureCache();
    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final ProductMapper mapper = new ProductMapperImpl();

    @SuppressWarnings("checkstyle:IllegalCatch")
    @Around("@annotation(by.clevertec.proxy.proxy.Cacheable) && execution(* by.clevertec.proxy.service.impl.ProductServiceImpl.get(..))")
    public Object cacheableGet(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID uuid = (UUID) args[0];
        if (cache.get(uuid) != null) {
            return cache.get(uuid);
        }
        InfoProductDto result;
        try {
            result = (InfoProductDto) joinPoint.proceed();
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(uuid);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        cache.put(uuid, result);
        return result;
    }

    @AfterReturning(pointcut = "@annotation(by.clevertec.proxy.proxy.Cacheable) && execution(* by.clevertec.proxy.service.ProductService.create(..))", returning = "uuid")
    public void cacheableCreate(UUID uuid) {
        Optional<Product> optionalProduct = productRepository.findById(uuid);
        optionalProduct.ifPresent(product -> cache.put(uuid, mapper.toInfoProductDto(product)));
    }

    @AfterReturning(pointcut = "@annotation(by.clevertec.proxy.proxy.Cacheable) && execution(* by.clevertec.proxy.service.ProductService.delete(..)) && args(uuid)", argNames = "uuid")
    public void cacheableDelete(UUID uuid) {
        cache.remove(uuid);
    }

    @AfterReturning(pointcut = "@annotation(by.clevertec.proxy.proxy.Cacheable) && execution(* by.clevertec.proxy.service.ProductService.update(..)) && args(uuid, productDto)", argNames = "uuid, productDto")
    public void cacheableUpdate(UUID uuid, ProductDto productDto) {
        Product product = productRepository.findById(uuid).orElseThrow(() -> new ProductNotFoundException(uuid));
        cache.put(uuid, mapper.toInfoProductDto(product));
    }

    private Cache<UUID, Object> configureCache() {
        if (cache == null) {
            synchronized (this) {
                AppConfig appConfig = new AppConfig();
                Map<String, Object> databaseProperties = appConfig.getProperty("Cache");
                String algorithm = (String) databaseProperties.get("algorithm");
                Integer maxCollectionSize = (Integer) databaseProperties.get("maxCollectionSize");
                if ("LFU".equals(algorithm)) {
                    cache = new LfuCache<>(maxCollectionSize);
                } else {
                    cache = new LruCache<>(maxCollectionSize);
                }
            }
        }
        return cache;
    }
}
