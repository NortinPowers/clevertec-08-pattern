package by.clevertec.proxy.pdf;

import static by.clevertec.proxy.util.Constants.DATA_REPORT_FORMAT;
import static by.clevertec.proxy.util.Constants.REPORT_DIR;
import static by.clevertec.proxy.util.Constants.USER_DIR;
import static by.clevertec.proxy.util.TestConstant.PRODUCT_UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import by.clevertec.proxy.data.InfoProductDto;
import by.clevertec.proxy.entity.Product;
import by.clevertec.proxy.util.ProductTestBuilder;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PdfPrinterTest {

    @Test
    public void createPdfShouldCreatePdf_withInfoProductDtoObject() {
        InfoProductDto infoProductDto = ProductTestBuilder.builder().build()
                .buildInfoProductDto();
        List<String> testPaths = getPaths();

        PdfPrinter.createPdf(infoProductDto);

        Map<String, String> testData = getTestString(testPaths);
        checkAssertion(testData, infoProductDto);
        defineDeleteFile(testData);
    }

    @Test
    public void createPdfShouldCreatePdf_withUuid() {
        List<String> testPaths = getPaths();

        PdfPrinter.createPdf(PRODUCT_UUID);

        Map<String, String> testData = getTestString(testPaths);
        checkAssertion(testData, PRODUCT_UUID);
        defineDeleteFile(testData);
    }

    private void checkAssertion(Map<String, String> testData, UUID uuid) {
        for (String value : testData.values()) {
            if (!value.isEmpty()) {
                assertEquals(uuid.toString(), value.trim());
            } else {
                fail();
            }
        }
    }

    private void checkAssertion(Map<String, String> testData, InfoProductDto infoProductDto) {
        Map<String, String> stringMap = new HashMap<>();
        for (String value : testData.values()) {
            if (!value.isEmpty()) {
                createMapByString(value, stringMap);
                assertEquals(infoProductDto.uuid().toString(), stringMap.get(Product.Fields.uuid));
                assertEquals(infoProductDto.name(), stringMap.get(Product.Fields.name));
                assertEquals(infoProductDto.description(), stringMap.get(Product.Fields.description));
                assertEquals(infoProductDto.price().toString(), stringMap.get(Product.Fields.price));
                assertEquals(infoProductDto.created().format(DateTimeFormatter.ISO_DATE), stringMap.get("created date"));
            } else {
                fail();
            }
        }
    }

    private void createMapByString(String value, Map<String, String> stringMap) {
        for (String line : value.split("\n")) {
            String[] parts = line.split(": ");
            if (parts.length == 2) {
                String key = parts[0].trim().substring(1);
                String valueMap = parts[1].trim();
                stringMap.put(key, valueMap);
            }
        }
    }

    private List<String> getPaths() {
        LocalDateTime time = LocalDateTime.now();
        String testPath = getTestPathByTime(time);
        String slowTestPath = getTestPathByTime(time.plusSeconds(1));
        String evenSlowerTestPath = getTestPathByTime(time.plusSeconds(2));
        // можно добавлять пути при недостатке мощности и поминать автора всуе
        return List.of(testPath, slowTestPath, evenSlowerTestPath);
    }

    private String getTestPathByTime(LocalDateTime now) {
        return System.getProperty(USER_DIR) + File.separator + REPORT_DIR + File.separator + "report " + now.format(DateTimeFormatter.ofPattern(DATA_REPORT_FORMAT)) + ".pdf";
    }

    private Map<String, String> getTestString(List<String> testPaths) {
        PdfReader reader;
        String text;
        Map<String, String> source = new HashMap<>();
        int count = 0;
        for (String path : testPaths) {
            try {
                reader = new PdfReader(path);
                text = PdfTextExtractor.getTextFromPage(reader, 1);
                source.put(path, text);
                reader.close();
            } catch (IOException exception) {
                count += count;
                showTestInfoMessage(testPaths, count);
            }
        }
        return source;
    }

    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    private void defineDeleteFile(Map<String, String> testData) {
        for (Map.Entry<String, String> entry : testData.entrySet()) {
            if (!entry.getKey().isEmpty() && !entry.getValue().isEmpty()) {
                deleteFile(entry.getKey());
            }
        }
    }

    private void showTestInfoMessage(List<String> testPaths, int count) {
        if (count == testPaths.size()) {
            System.out.println("Увеличьте количество возможных путей к файлу в методе getPaths() добавив секунд в time.plusSeconds(Х)");
        }
    }
}
