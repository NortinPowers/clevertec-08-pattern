package by.clevertec.proxy.pdf;

import static by.clevertec.proxy.util.LogUtil.getErrorMessageToLog;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
public class ImageEventHandler extends PdfPageEventHelper {

    private final Path imagePath;
    private final Font font;

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        try {
            Paragraph emptyLines = new Paragraph();
            emptyLines.setFont(font);
            addEmptyLine(emptyLines, 10);
            document.add(emptyLines);
        } catch (Exception exception) {
            log.error(getErrorMessageToLog("onStartPage()", ImageEventHandler.class), exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        String path = imagePath.toAbsolutePath().toString();
        try {
            setBackgroundByType(writer, document, path);
        } catch (Exception exception) {
            log.error(getErrorMessageToLog("onEndPage()", ImageEventHandler.class), exception);
            throw new RuntimeException(exception);
        }
    }

    /**
     * Настраивает фон исходя из типа файла подложки (rev.1 - jpg / pdf).
     *
     * @param writer   - объект для чтения PDF-документа
     * @param document - текущий создаваемый документ
     * @param path     - адрес ресурса фона
     * @throws IOException       - выбрасываемое исключение
     * @throws DocumentException - выбрасываемое исключен
     */
    private void setBackgroundByType(PdfWriter writer, Document document, String path) throws IOException, DocumentException {
        if (path.endsWith(".jpg")) {
            Image img = Image.getInstance(imagePath.toAbsolutePath().toString());
            img.scaleAbsolute(document.getPageSize().getWidth(), document.getPageSize().getHeight());
            img.setAbsolutePosition(0, 0);
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.addImage(img);
        } else if (path.endsWith(".pdf")) {
            PdfReader reader = new PdfReader(path);
            PdfImportedPage page = writer.getImportedPage(reader, 1);
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.addTemplate(page, 0, 0);
        }
    }

    /**
     * Добавляет пустые строки.
     *
     * @param paragraph - текстовый блок
     * @param number    - количество необходимых пустых строк
     */
    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}
