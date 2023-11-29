package by.clevertec.proxy.pdf;

import static by.clevertec.proxy.util.Constants.BACKGROUND_IMG;
import static by.clevertec.proxy.util.Constants.BACKGROUND_PATH;
import static by.clevertec.proxy.util.Constants.DATA_REPORT_FORMAT;
import static by.clevertec.proxy.util.Constants.REPORT_DIR;
import static by.clevertec.proxy.util.Constants.USER_DIR;
import static by.clevertec.proxy.util.LogUtil.getErrorMessageToLog;

import by.clevertec.proxy.data.InfoProductDto;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@UtilityClass
@Log4j2
public class PdfPrinter {

    /**
     * Создает PDF-документ на основе переданного объекта.
     *
     * @param object - контент документа
     * @param <T>    - тип объекта
     */
    public static <T> void createPdf(T object) {
        fillDocument(object);
    }

    /**
     * Заполняет документ контентом.
     *
     * @param object - контент документа
     * @param <T>    - тип объекта
     * @throws RuntimeException - выбрасываемое исключение
     */
    private <T> void fillDocument(T object) {
        Font font = getFont();
        Document document = new Document();
        try {
            Path path = getPath();
            String filePath = getFilePath();
            setNewPageEvent(document, filePath, path, font);
            document.open();
            setContentValue(object, font, document);
        } catch (URISyntaxException | DocumentException | FileNotFoundException exception) {
            log.error(getErrorMessageToLog("fillDocument()", ImageEventHandler.class), exception);
            throw new RuntimeException(exception);
        } finally {
            document.close();
        }
    }

    /**
     * Возвращает путь к изображению подложки.
     *
     * @return Path - объект указания местоположения
     * @throws URISyntaxException - выбрасываемое исключение
     */
    private Path getPath() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(BACKGROUND_PATH + BACKGROUND_IMG).toURI());
    }

    /**
     * Возвращает шрифт для документа.
     *
     * @return Font - объект с характеристиками шрифта
     */
    private Font getFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, "windows-1251", 13);
    }

    /**
     * Задает обработчик для обработки каждого нового листа документа.
     *
     * @param document - создаваемый документ
     * @param filePath - путь к папке сохранения документа
     * @param path     - объект указания местоположения к изображению подложки
     * @param font     - объект шрифта создаваемого документа
     * @throws DocumentException     - выбрасываемое исключение
     * @throws FileNotFoundException - выбрасываемое исключение
     */
    private void setNewPageEvent(Document document, String filePath, Path path, Font font) throws DocumentException, FileNotFoundException {
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        ImageEventHandler eventHandler = new ImageEventHandler(path, font);
        writer.setPageEvent(eventHandler);
    }

    /**
     * Задает контент в зависимости от переданного объекта.
     *
     * @param object   - контент документа
     * @param font     - объект с характеристиками шрифта
     * @param document - создаваемый документ
     * @param <T>      - тип объекта контента
     * @throws DocumentException - выбрасываемое исключение
     */
    private <T> void setContentValue(T object, Font font, Document document) throws DocumentException {
        Paragraph elements = new Paragraph();
        Chunk chunk = getChunk(object);
        chunk.setFont(font);
        elements.setIndentationLeft(80);
        elements.add(chunk);
        document.add(elements);
    }

    /**
     * Возвращает путь документа для сохранения.
     *
     * @return String - путь к сохраняемому документу
     */
    private String getFilePath() {
        String projectPath = System.getProperty(USER_DIR);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATA_REPORT_FORMAT));
        return projectPath + File.separator + REPORT_DIR + File.separator + "report " + date + ".pdf";
    }

    /**
     * Создает текстовый элемент в зависимости от контента.
     *
     * @param object - контент документа
     * @param <T>    - тип объекта контента
     * @return Chunk - текстовый элемент
     */
    private <T> Chunk getChunk(T object) {
        Chunk chunk = null;
        if (object instanceof InfoProductDto infoProductDto) {
            chunk = new Chunk(infoProductDto.toString());
        } else if (object instanceof List list) {
            StringBuilder builder = getStringBuilderByList(list);
            chunk = new Chunk(builder.toString());
        } else if (object instanceof UUID uuid) {
            chunk = new Chunk(uuid.toString());
        }
        return chunk;
    }

    /**
     * Возвращает StringBuilder-строку из списка.
     *
     * @param list - список объектов для контента
     * @return StringBuilder - строка из списка
     */
    private StringBuilder getStringBuilderByList(List list) {
        StringBuilder builder = new StringBuilder();
        for (Object element : list) {
            builder
                    .append(element.toString())
                    .append(";\n");
        }
        return builder;
    }
}
