package com.karabo.resume_analyzer.service;

import com.karabo.resume_analyzer.model.Resume;
import com.karabo.resume_analyzer.exception.FileProcessingException;
import com.karabo.resume_analyzer.repository.ResumeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AIAnalysisService aiAnalysisService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ResumeService(ResumeRepository resumeRepository, AIAnalysisService aiAnalysisService) {
        this.resumeRepository = resumeRepository;
        this.aiAnalysisService = aiAnalysisService;
    }

    public Resume uploadResume(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String fileType = file.getContentType();

        log.info("Processing upload: '{}' (type: {}, size: {} bytes)", originalFileName, fileType, file.getSize());

        if (!isPdfOrDocx(fileType, originalFileName)) {
            throw new IllegalArgumentException("Only PDF and DOCX files are allowed.");
        }

        Path filePath;
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String storedFileName = UUID.randomUUID() + "_" + originalFileName;
            filePath = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), filePath);
            log.info("File saved to '{}'", filePath);
        } catch (IOException e) {
            log.error("Failed to save file '{}'", originalFileName, e);
            throw new FileProcessingException("Failed to save the uploaded file. Please try again.", e);
        }

        String extractedText;
        try {
            log.info("Extracting text from '{}'", originalFileName);
            extractedText = extractText(filePath, fileType, originalFileName);
            log.info("Extracted {} characters from '{}'", extractedText.length(), originalFileName);
        } catch (IOException e) {
            log.error("Failed to extract text from '{}'", originalFileName, e);
            throw new FileProcessingException("Could not read the file content. Ensure it is a valid PDF or DOCX.", e);
        }

        log.info("Requesting AI analysis for '{}'", originalFileName);
        String analysisResult = aiAnalysisService.analyzeResume(extractedText);

        Resume resume = new Resume();
        resume.setFileName(originalFileName);
        resume.setFileType(fileType);
        resume.setFilePath(filePath.toString());
        resume.setExtractedText(extractedText);
        resume.setAnalysisResult(analysisResult);

        Resume saved = resumeRepository.save(resume);
        log.info("Resume saved to database with id={}", saved.getId());
        return saved;
    }

    private String extractText(Path filePath, String contentType, String fileName) throws IOException {
        if (isPdf(contentType, fileName)) return extractFromPdf(filePath);
        if (isDocx(contentType, fileName)) return extractFromDocx(filePath);
        return "";
    }

    private String extractFromPdf(Path filePath) throws IOException {
        try (PDDocument document = PDDocument.load(filePath.toFile())) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractFromDocx(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             XWPFDocument document = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private boolean isPdfOrDocx(String contentType, String fileName) {
        return isPdf(contentType, fileName) || isDocx(contentType, fileName);
    }

    private boolean isPdf(String contentType, String fileName) {
        if ("application/pdf".equals(contentType)) return true;
        return fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }

    private boolean isDocx(String contentType, String fileName) {
        if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) return true;
        return fileName != null && fileName.toLowerCase().endsWith(".docx");
    }
}
