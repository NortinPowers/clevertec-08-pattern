package by.clevertec.proxy.service.impl;

import static by.clevertec.proxy.util.CacheableAspectUtil.setCacheableAspectField;
import static by.clevertec.proxy.util.TestConstant.PRODUCT_INCORRECT_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.clevertec.proxy.cache.Cache;
import by.clevertec.proxy.data.InfoProductDto;
import by.clevertec.proxy.data.ProductDto;
import by.clevertec.proxy.entity.Product;
import by.clevertec.proxy.exception.ProductNotFoundException;
import by.clevertec.proxy.exception.ValidationException;
import by.clevertec.proxy.mapper.ProductMapper;
import by.clevertec.proxy.proxy.CacheableAspect;
import by.clevertec.proxy.repository.ProductRepository;
import by.clevertec.proxy.util.ProductTestBuilder;
import by.clevertec.proxy.validator.ProductDtoValidator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper mapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Cache<UUID, Object> cache;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private ProductDtoValidator productDtoValidator;

    @InjectMocks
    private CacheableAspect cacheableAspect;

    @InjectMocks
    private ProductServiceImpl productService;

    @Captor
    private ArgumentCaptor<Product> captor;

    @Nested
    class GetTest {

        @Test
        void getShouldReturnInfoProductDtoFromCache_whenInfoProductDtoWithCurrentUuidIsInCache() throws Throwable {
            InfoProductDto expected = ProductTestBuilder.builder()
                    .build()
                    .buildInfoProductDto();
            Product product = ProductTestBuilder.builder()
                    .build()
                    .buildProduct();

            when(joinPoint.getArgs())
                    .thenReturn(new Object[]{product.getUuid()});
            when(cache.get(product.getUuid()))
                    .thenReturn(expected);

            InfoProductDto actual = (InfoProductDto) cacheableAspect.cacheableGet(joinPoint);

            assertThat(actual)
                    .hasFieldOrPropertyWithValue(Product.Fields.uuid, expected.uuid())
                    .hasFieldOrPropertyWithValue(Product.Fields.name, expected.name())
                    .hasFieldOrPropertyWithValue(Product.Fields.description, expected.description())
                    .hasFieldOrPropertyWithValue(Product.Fields.price, expected.price());
            verify(joinPoint, never()).proceed();
            verify(cache, never()).put(eq(product.getUuid()), any(InfoProductDto.class));
        }

        @Test
        void getShouldReturnInfoProductDtoFromAop_whenInfoProductDtoWithCurrentUuidIsNotInCacheButIsInRepository() throws Throwable {
            InfoProductDto expected = ProductTestBuilder.builder()
                    .build()
                    .buildInfoProductDto();
            Product product = ProductTestBuilder.builder()
                    .build()
                    .buildProduct();

            when(joinPoint.getArgs())
                    .thenReturn(new Object[]{product.getUuid()});
            when(cache.get(product.getUuid()))
                    .thenReturn(null);
            when(joinPoint.proceed())
                    .thenReturn(expected);

            InfoProductDto actual = (InfoProductDto) cacheableAspect.cacheableGet(joinPoint);

            assertThat(actual)
                    .hasFieldOrPropertyWithValue(Product.Fields.uuid, expected.uuid())
                    .hasFieldOrPropertyWithValue(Product.Fields.name, expected.name())
                    .hasFieldOrPropertyWithValue(Product.Fields.description, expected.description())
                    .hasFieldOrPropertyWithValue(Product.Fields.price, expected.price());
            verify(cache).put(product.getUuid(), expected);
        }

        @Test
        void getShouldReturnInfoProductDtoFromRepository_whenProductWithCurrentUuidExist() {
            InfoProductDto expected = ProductTestBuilder.builder()
                    .build()
                    .buildInfoProductDto();
            Product product = ProductTestBuilder.builder()
                    .build()
                    .buildProduct();

            when(productRepository.findById(product.getUuid()))
                    .thenReturn(Optional.of(product));
            when(mapper.toInfoProductDto(product))
                    .thenReturn(expected);

            InfoProductDto actual = productService.get(product.getUuid());

            assertThat(actual)
                    .hasFieldOrPropertyWithValue(Product.Fields.uuid, expected.uuid())
                    .hasFieldOrPropertyWithValue(Product.Fields.name, expected.name())
                    .hasFieldOrPropertyWithValue(Product.Fields.description, expected.description())
                    .hasFieldOrPropertyWithValue(Product.Fields.price, expected.price());
        }

        @Test
        void getShouldThrowNotProductFoundExceptionFromAop_whenInfoProductDtoWithCurrentUuidIsNotInCacheAndRepository() throws Throwable {
            ProductNotFoundException expectedException = new ProductNotFoundException(PRODUCT_INCORRECT_UUID);

            when(joinPoint.getArgs())
                    .thenReturn(new Object[]{PRODUCT_INCORRECT_UUID});
            when(cache.get(PRODUCT_INCORRECT_UUID))
                    .thenReturn(null);
            when(joinPoint.proceed())
                    .thenThrow(expectedException);

            ProductNotFoundException actualException = assertThrows(ProductNotFoundException.class, () -> cacheableAspect.cacheableGet(joinPoint));

            verify(cache, never()).put(eq(PRODUCT_INCORRECT_UUID), any(InfoProductDto.class));
            assertEquals(expectedException.getMessage(), actualException.getMessage());
        }

        @Test
        void getShouldThrowProductNotFoundExceptionFromRepository__whenProductWithCurrentUuidIsNotExist() {
            ProductNotFoundException expectedException = new ProductNotFoundException(PRODUCT_INCORRECT_UUID);

            when(productRepository.findById(PRODUCT_INCORRECT_UUID))
                    .thenReturn(Optional.empty());

            ProductNotFoundException actualException = assertThrows(ProductNotFoundException.class, () -> productService.get(PRODUCT_INCORRECT_UUID));

            assertEquals(expectedException.getMessage(), actualException.getMessage());
        }
    }

    @Nested
    class GetAllTest {

        @Test
        void getAllShouldReturnInfoProductDtoList_whenProductsListIsNotEmpty() {
            InfoProductDto infoProductDto = ProductTestBuilder.builder()
                    .build()
                    .buildInfoProductDto();
            List<InfoProductDto> expected = List.of(infoProductDto);
            Product product = ProductTestBuilder.builder()
                    .build()
                    .buildProduct();
            List<Product> products = List.of(product);

            when(productRepository.findAll())
                    .thenReturn(products);
            when(mapper.toInfoProductDto(product))
                    .thenReturn(infoProductDto);

            List<InfoProductDto> actual = productService.getAll();

            assertThat(actual)
                    .hasSize(expected.size())
                    .isEqualTo(expected);
        }

        @Test
        void getAllShouldReturnEmptyList_whenProductsListIsEmpty() {
            List<InfoProductDto> expected = List.of();
            List<Product> products = List.of();

            when(productRepository.findAll())
                    .thenReturn(products);

            List<InfoProductDto> actual = productService.getAll();

            assertThat(actual)
                    .hasSize(expected.size())
                    .containsAll(expected);
            verify(mapper, never()).toInfoProductDto(any(Product.class));
        }
    }

    @Nested
    class CreateTest {

        @Test
        void createShouldReturnUuid_whenProductDtoIsCorrect() {
            ProductDto productDto = ProductTestBuilder.builder()
                    .build()
                    .buildProductDto();
            Product product = ProductTestBuilder.builder()
                    .withUuid(null).build()
                    .buildProduct();
            UUID generetedUuid = UUID.fromString("d3d75177-f087-4c70-ae30-05d947733c4e");
            Product createdProduct = ProductTestBuilder.builder()
                    .withUuid(generetedUuid).build()
                    .buildProduct();

            doNothing()
                    .when(productDtoValidator).validate(productDto);
            when(mapper.toProduct(productDto))
                    .thenReturn(product);
            when(productRepository.save(product))
                    .thenReturn(createdProduct);

            UUID actual = productService.create(productDto);

            assertEquals(createdProduct.getUuid(), actual);
        }

        @Test
        void createShouldReturnUuidAndAddInfoProductDtoInCache_whenProductDtoIsCorrect() throws Exception {
            ProductDto productDto = ProductTestBuilder.builder()
                    .build()
                    .buildProductDto();
            Product product = ProductTestBuilder.builder()
                    .withUuid(null)
                    .build()
                    .buildProduct();
            UUID generetedUuid = UUID.fromString("d3d75177-f087-4c70-ae30-05d947733c4e");
            Product createdProduct = ProductTestBuilder.builder()
                    .withUuid(generetedUuid)
                    .build()
                    .buildProduct();
            InfoProductDto infoProductDto = ProductTestBuilder.builder()
                    .withUuid(generetedUuid)
                    .build()
                    .buildInfoProductDto();
            setCacheableAspectField(cacheableAspect, cache, productRepository, mapper);

            doNothing()
                    .when(productDtoValidator).validate(productDto);
            when(mapper.toProduct(productDto))
                    .thenReturn(product);
            when(productRepository.save(product))
                    .thenReturn(createdProduct);
            when(productRepository.findById(generetedUuid))
                    .thenReturn(Optional.of(createdProduct));
            when(mapper.toInfoProductDto(createdProduct))
                    .thenReturn(infoProductDto);

            UUID actual = productService.create(productDto);
            cacheableAspect.cacheableCreate(generetedUuid);

            verify(cache).put(actual, infoProductDto);
        }

        @Test
        void createShouldSetProductUuid_whenInvokeRepository() {
            ProductDto productDto = ProductTestBuilder.builder()
                    .build()
                    .buildProductDto();
            Product productToSave = ProductTestBuilder.builder()
                    .withUuid(null)
                    .build()
                    .buildProduct();
            Product createdProduct = ProductTestBuilder.builder()
                    .build()
                    .buildProduct();

            doNothing()
                    .when(productDtoValidator).validate(productDto);
            when(mapper.toProduct(productDto))
                    .thenReturn(productToSave);
            when(productRepository.save(productToSave))
                    .thenReturn(createdProduct);

            productService.create(productDto);

            verify(productRepository, atLeastOnce()).save(captor.capture());
            assertThat(captor.getValue())
                    .hasFieldOrPropertyWithValue(Product.Fields.uuid, null);
        }

        @Test
        void createShouldLeaveProductUuid_whenProductDtoUuidExist() {
            ProductDto productDto = ProductTestBuilder.builder()
                    .build()
                    .buildProductDto();
            Product productToSave = ProductTestBuilder.builder()
                    .withUuid(null)
                    .withCreated(null)
                    .build()
                    .buildProduct();

            doNothing()
                    .when(productDtoValidator).validate(productDto);
            when(mapper.toProduct(productDto))
                    .thenReturn(productToSave);
            when(productRepository.save(productToSave))
                    .thenReturn(productToSave);

            productService.create(productDto);

            verify(productRepository, atLeastOnce()).save(captor.capture());
            assertThat(captor.getValue())
                    .hasFieldOrPropertyWithValue(Product.Fields.uuid, null)
                    .hasFieldOrPropertyWithValue(Product.Fields.created, null);
        }

        @Test
        void createShouldReturnNullPointerException_whenProductDtoIsNull() {
            assertThrows(NullPointerException.class, () -> productService.create(null));
            verify(mapper, never()).toProduct(any(ProductDto.class));
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void createShouldReturnValidateException_whenProductDtoFieldIsNotValid() {
            ProductDto productDto = ProductTestBuilder.builder()
                    .withName("Plumbus")
                    .build()
                    .buildProductDto();
            ValidationException expected = new ValidationException(List.of("incorrect product name"));

            doThrow(expected)
                    .when(productDtoValidator).validate(productDto);

            ValidationException actual = assertThrows(ValidationException.class, () -> productService.create(productDto));
            assertThat(actual)
                    .hasMessage(expected.getMessage());
            verify(mapper, never()).toProduct(any(ProductDto.class));
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Nested
    class UpdateTest {

        @Test
        void updateShouldReturnProductNotFoundException_whenIncorrectUuid() {
            Product product = ProductTestBuilder.builder()
                    .withUuid(PRODUCT_INCORRECT_UUID)
                    .build()
                    .buildProduct();
            ProductNotFoundException exception = new ProductNotFoundException(product.getUuid());

            when(productRepository.findById(product.getUuid()))
                    .thenThrow(exception);

            assertThrows(ProductNotFoundException.class, () -> productService.update(product.getUuid(), any(ProductDto.class)));
            verify(mapper, never()).toInfoProductDto(product);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Nested
    class DeleteTest {

        @Test
        void deleteShouldDeleteProduct_whenCorrectUuid() {
            UUID uuid = ProductTestBuilder.builder().build().getUuid();

            doNothing()
                    .when(productRepository).delete(uuid);

            productService.delete(uuid);
        }
    }
}

