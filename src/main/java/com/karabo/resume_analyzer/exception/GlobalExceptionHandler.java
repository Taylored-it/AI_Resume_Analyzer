package com.karabo.resume_analyzer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(MaxUploadSizeExceededException ex, RedirectAttributes ra) {
        log.warn("File upload rejected — size exceeded: {}", ex.getMessage());
        ra.addFlashAttribute("error", "File too large. Maximum allowed size is 10MB.");
        return "redirect:/";
    }

    @ExceptionHandler(AIAnalysisException.class)
    public String handleAIAnalysis(AIAnalysisException ex, RedirectAttributes ra) {
        log.error("AI analysis failed: {}", ex.getMessage());
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(FileProcessingException.class)
    public String handleFileProcessing(FileProcessingException ex, RedirectAttributes ra) {
        log.error("File processing failed: {}", ex.getMessage());
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, RedirectAttributes ra) {
        log.error("Unexpected error occurred", ex);
        ra.addFlashAttribute("error", "Something went wrong. Please try again.");
        return "redirect:/";
    }
}
