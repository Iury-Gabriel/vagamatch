package dev.iurygabriel.vagamatch.infrastructure.candidates;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class PdfBoxResumeExtractorTest {

    private final PdfBoxResumeExtractor extractor = new PdfBoxResumeExtractor();

    @Test
    @DisplayName("extrai o texto de um PDF")
    void extrai_texto_do_pdf() throws Exception {
        byte[] pdf = pdfWithText("Iury Gabriel - React, Node e TypeScript");

        String text = extractor.extractText(pdf);

        assertThat(text).contains("React").contains("TypeScript");
    }

    private static byte[] pdfWithText(String content) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                stream.beginText();
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                stream.newLineAtOffset(50, 700);
                stream.showText(content);
                stream.endText();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}
