package by.clevertec.proxy.repository.impl;

import static by.clevertec.proxy.repository.util.TestDataSource.closeDataSource;
import static by.clevertec.proxy.repository.util.TestDataSource.getDataSource;
import static by.clevertec.proxy.util.DbUtil.SCRIPT_FILE_FILL;
import static by.clevertec.proxy.util.DbUtil.SCRIPT_FILE_INIT;
import static by.clevertec.proxy.util.DbUtil.executeScript;
import static by.clevertec.proxy.util.TestConstant.PRODUCT_UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import by.clevertec.proxy.entity.Product;
import by.clevertec.proxy.util.ProductTestBuilder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductRepositoryImplTest {

    private static ProductRepositoryImpl productRepository;

    @BeforeAll
    public static void init() {
        executeScript(SCRIPT_FILE_INIT);
        productRepository = new ProductRepositoryImpl();
        productRepository.setDataSource(getDataSource());
    }

    @BeforeEach
    public void initEach() {
        executeScript(SCRIPT_FILE_FILL);
    }

    @AfterAll
    public static void cleanUp() {
        closeDataSource();
    }

    @Test
    void findByIdShouldReturnNotEmptyOptional_whenCorrectUuid() {
        Optional<Product> actual = productRepository.findById(PRODUCT_UUID);

        assertThat(actual)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void findAllShouldReturnList_whenCalled() {
        List<Product> actual = productRepository.findAll();

        assertEquals(2, actual.size());
    }

    @Nested
    class SaveTest {

        @Test
        void saveShouldReturnProductWithUuidAndCreated_whenCalled() {
            Product expected = ProductTestBuilder.builder()
                    .withUuid(null)
                    .build()
                    .buildProduct();

            Product actual = productRepository.save(expected);

            assertThat(actual)
                    .hasNoNullFieldsOrPropertiesExcept(Product.Fields.uuid)
                    .hasFieldOrPropertyWithValue(Product.Fields.name, expected.getName())
                    .hasFieldOrPropertyWithValue(Product.Fields.description, expected.getDescription())
                    .hasFieldOrPropertyWithValue(Product.Fields.price, expected.getPrice())
                    .hasNoNullFieldsOrPropertiesExcept(Product.Fields.created);

        }

        @Test
        void saveShouldReturnProductWithNewUuid_whenUpdatedProductHasUuid() {
            Product product = ProductTestBuilder.builder()
                    .build()
                    .buildProduct();

            Product actual = productRepository.save(product);

            assertThat(actual)
                    .hasNoNullFieldsOrPropertiesExcept(Product.Fields.uuid)
                    .hasFieldOrPropertyWithValue(Product.Fields.name, product.getName())
                    .hasFieldOrPropertyWithValue(Product.Fields.description, product.getDescription())
                    .hasFieldOrPropertyWithValue(Product.Fields.price, product.getPrice())
                    .hasFieldOrPropertyWithValue(Product.Fields.created, product.getCreated());
            assertNotEquals(product.getUuid(), actual.getUuid());
        }
    }

    @Nested
    class DeleteTest {

        @Test
        void deleteShouldNotReturnError_whenCalled() {
            assertDoesNotThrow(() -> productRepository.delete(PRODUCT_UUID));
        }

        @Test
        void deleteShouldDelete_whenCalled() {
            UUID uuid = PRODUCT_UUID;

            productRepository.delete(uuid);

            assertEquals(productRepository.findById(uuid), Optional.empty());
        }
    }
}
