package dev.iurygabriel.vagamatch.infrastructure.candidates;

import dev.iurygabriel.vagamatch.candidates.application.port.ResumeTextExtractor;
import dev.iurygabriel.vagamatch.shared.domain.DomainException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Adapter que extrai o texto do currículo em PDF usando Apache PDFBox. */
@Component
public class PdfBoxResumeExtractor implements ResumeTextExtractor {

    @Override
    public String extractText(byte[] pdfBytes) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new DomainException("Arquivo de currículo vazio");
        }
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            String text = new PDFTextStripper().getText(document);
            return text == null ? "" : text.trim();
        } catch (IOException e) {
            throw new DomainException("Não foi possível ler o PDF: " + e.getMessage());
        }
    }
}
