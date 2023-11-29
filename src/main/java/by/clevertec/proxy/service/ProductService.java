package by.clevertec.proxy.service;

import by.clevertec.proxy.data.InfoProductDto;
import by.clevertec.proxy.data.ProductDto;
import by.clevertec.proxy.exception.ProductNotFoundException;
import java.util.List;
import java.util.UUID;

public interface ProductService {

    /**
     * Ищет в памяти продукт по идентификатору.
     *
     * @param uuid - идентификатор продукта.
     * @return найденный продукт.
     * @throws ProductNotFoundException если не найден.
     */
    InfoProductDto get(UUID uuid);

    /**
     * Возвращает все существующий продукты.
     *
     * @return лист с информацией о продуктах.
     */
    List<InfoProductDto> getAll();

    /**
     * Создаёт новый продукт из DTO.
     *
     * @param productDto - DTO с информацией о создании.
     * @return идентификатор созданного продукта.
     */
    UUID create(ProductDto productDto);

    /**
     * Обновляет уже существующий продукт из информации полученной в DTO.
     *
     * @param uuid       - идентификатор продукта для обновления.
     * @param productDto - DTO с информацией об обновлении.
     */
    void update(UUID uuid, ProductDto productDto);

    /**
     * Удаляет существующий продукт.
     *
     * @param uuid - идентификатор продукта для удаления.
     */
    void delete(UUID uuid);
}
