package com.karabo.resume_analyzer.controller;

import com.karabo.resume_analyzer.model.Resume;
import com.karabo.resume_analyzer.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload-resume")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file provided."));
        }

        log.info("API upload request: '{}' ({} bytes)", file.getOriginalFilename(), file.getSize());

        try {
            Resume saved = resumeService.uploadResume(file);
            return ResponseEntity.ok(Map.of(
                "message", "Resume uploaded successfully.",
                "id", saved.getId(),
                "fileName", saved.getFileName(),
                "uploadedAt", saved.getUploadedAt().toString(),
                "analysis", saved.getAnalysisResult()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("API upload failed for '{}'", file.getOriginalFilename(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
