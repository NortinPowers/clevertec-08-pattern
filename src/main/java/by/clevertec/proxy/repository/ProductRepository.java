package by.clevertec.proxy.repository;

import by.clevertec.proxy.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    /**
     * Ищет в памяти продукт по идентификатору.
     *
     * @param uuid - идентификатор продукта.
     * @return Optional<Product> если найден, иначе Optional.empty().
     */
    Optional<Product> findById(UUID uuid);

    /**
     * Ищет все продукты в памяти.
     *
     * @return список найденных продуктов.
     */
    List<Product> findAll();

    /**
     * Сохраняет или обновляет продукт в памяти.
     *
     * @param product - сохраняемый продукт.
     * @return сохранённый продукт.
     * @throws IllegalArgumentException если переданный продукт null.
     */
    Product save(Product product);

    /**
     * Удаляет продукт из памяти по идентификатору.
     *
     * @param uuid - идентификатор продукта.
     */
    void delete(UUID uuid);

    /**
     * Обновляет поля продукта по идентификатору.
     *
     * @param uuid    - идентификатор продукта.
     * @param product - объект содержащий поля для обновления.
     */
    void update(UUID uuid, Product product);
}
