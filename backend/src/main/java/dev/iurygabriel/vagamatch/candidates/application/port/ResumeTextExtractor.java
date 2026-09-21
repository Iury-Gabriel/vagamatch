package dev.iurygabriel.vagamatch.candidates.application.port;

/**
 * Porta de saída: extrai o texto de um currículo em PDF.
 * Implementada na infraestrutura (PDFBox).
 */
public interface ResumeTextExtractor {

    String extractText(byte[] pdfBytes);
}
