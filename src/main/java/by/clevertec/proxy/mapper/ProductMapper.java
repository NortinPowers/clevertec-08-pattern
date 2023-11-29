package by.clevertec.proxy.mapper;

import by.clevertec.proxy.data.InfoProductDto;
import by.clevertec.proxy.data.ProductDto;
import by.clevertec.proxy.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    /**
     * Маппит DTO в продукт без UUID.
     *
     * @param productDto - DTO для маппинга
     * @return новый продукт
     */
    Product toProduct(ProductDto productDto);

    /**
     * Маппит текущий продукт в DTO без даты.
     *
     * @param product - существующий продукт
     * @return DTO с идентификатором
     */
    InfoProductDto toInfoProductDto(Product product);

    /**
     * Сливает существующий продукт с информацией из DTO
     * не меняет дату создания и идентификатор.
     *
     * @param product    существующий продукт
     * @param productDto информация для обновления
     * @return обновлённый продукт
     */
    Product merge(@MappingTarget Product product, ProductDto productDto);
}
