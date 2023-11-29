package by.clevertec.proxy;

import static by.clevertec.proxy.pdf.PdfPrinter.createPdf;

import by.clevertec.proxy.data.InfoProductDto;
import by.clevertec.proxy.mapper.ProductMapper;
import by.clevertec.proxy.mapper.ProductMapperImpl;
import by.clevertec.proxy.repository.ProductRepository;
import by.clevertec.proxy.repository.impl.ProductRepositoryImpl;
import by.clevertec.proxy.repository.util.DataSource;
import by.clevertec.proxy.service.ProductService;
import by.clevertec.proxy.service.impl.ProductServiceImpl;
import by.clevertec.proxy.validator.ProductDtoValidator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ProductDtoValidator validator = new ProductDtoValidator();
        ProductRepository productRepository = new ProductRepositoryImpl();
        ProductMapper productMapper = new ProductMapperImpl();
        ProductService productService = new ProductServiceImpl(productMapper, productRepository, validator);

        List<InfoProductDto> productDtos = productService.getAll();
        InfoProductDto infoProductDto = productDtos.get(0);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        Runnable taskAllProductsReport = () -> createPdf(productDtos);
        Runnable taskOneProductReport = () -> createPdf(infoProductDto);
        Runnable taskOneUuidReport = () -> createPdf(infoProductDto.uuid());

        executor.schedule(taskAllProductsReport, 1_200, TimeUnit.MILLISECONDS);
        executor.schedule(taskOneProductReport, 2_400, TimeUnit.MILLISECONDS);
        executor.schedule(taskOneUuidReport, 3_600, TimeUnit.MILLISECONDS);
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        DataSource.closeDataSource();
    }
}
