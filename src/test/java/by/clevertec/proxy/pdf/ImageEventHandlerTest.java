package by.clevertec.proxy.pdf;

import static by.clevertec.proxy.util.TestConstant.TEST_IMG_PATH;
import static by.clevertec.proxy.util.TestConstant.TEST_PDF_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImageEventHandlerTest {

    @Mock
    private Document document;

    @Mock
    private PdfWriter writer;

    @Mock
    private PdfImportedPage page;

    @Mock
    private PdfContentByte contentByte;

    @InjectMocks
    private ImageEventHandler eventHandler;

    @Captor
    private ArgumentCaptor<Paragraph> captor;

    @Test
    void onEndPageShouldAddBackgroundJpgImage_whenCreatedNewPage() throws DocumentException {
        Path path = Paths.get(TEST_IMG_PATH);
        ImageEventHandler imageEventHandler = new ImageEventHandler(path, null);

        when(document.getPageSize())
                .thenReturn(new Rectangle(210, 297));
        when(writer.getDirectContentUnder())
                .thenReturn(contentByte);

        imageEventHandler.onEndPage(writer, document);

        verify(contentByte).addImage(any(Image.class));
    }

    @Test
    void onEndPageShouldAddBackgroundPdfImage_whenCreatedNewPage() {
        Path path = Paths.get(TEST_PDF_PATH);
        ImageEventHandler imageEventHandler = new ImageEventHandler(path, null);

        when(writer.getImportedPage(any(PdfReader.class), eq(1)))
                .thenReturn(page);
        when(writer.getDirectContentUnder())
                .thenReturn(contentByte);

        imageEventHandler.onEndPage(writer, document);

        verify(contentByte).addTemplate(eq(page), any(Float.class), any(Float.class));
    }

    @Nested
    class OnStartPageTest {
        @Test
        void onStartPageShouldAddParagraph_whenCreatedNewPage() throws DocumentException {
            eventHandler.onStartPage(writer, document);

            verify(document).add(any(Paragraph.class));
        }

        @Test
        void onStartPageShouldAddParagraphWithCorrectFontAndSize_whenCreatedNewPage() throws DocumentException {
            Font expectedFont = FontFactory.getFont(FontFactory.HELVETICA, 13, BaseColor.BLACK);
            int expectedSize = 10;
            ImageEventHandler imageEventHandler = new ImageEventHandler(null, expectedFont);

            imageEventHandler.onStartPage(writer, document);

            verify(document).add(captor.capture());
            Paragraph actual = captor.getValue();

            assertEquals(expectedFont, actual.getFont());
            assertEquals(expectedSize, actual.size());
        }
    }
}
