package com.karabo.resume_analyzer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karabo.resume_analyzer.model.Resume;
import com.karabo.resume_analyzer.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class PageController {

    private final ResumeService resumeService;
    private final ObjectMapper objectMapper;

    public PageController(ResumeService resumeService, ObjectMapper objectMapper) {
        this.resumeService = resumeService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         Model model,
                         RedirectAttributes ra) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file before uploading.");
            return "index";
        }

        log.info("Upload request received: '{}' ({} bytes)", file.getOriginalFilename(), file.getSize());

        try {
            Resume saved = resumeService.uploadResume(file);
            JsonNode analysis = objectMapper.readTree(saved.getAnalysisResult());

            model.addAttribute("fileName", saved.getFileName());
            model.addAttribute("summary", analysis.path("summary").asText("No summary available."));

            List<String> skills = new ArrayList<>();
            analysis.path("skills").forEach(node -> skills.add(node.asText()));
            model.addAttribute("skills", skills);

            List<String> suggestions = new ArrayList<>();
            analysis.path("suggestions").forEach(node -> suggestions.add(node.asText()));
            model.addAttribute("suggestions", suggestions);

            log.info("Analysis complete for '{}' — {} skills, {} suggestions",
                    saved.getFileName(), skills.size(), suggestions.size());

            return "result";

        } catch (IllegalArgumentException e) {
            log.warn("Invalid file upload attempt: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "index";
        } catch (Exception e) {
            log.error("Failed to process upload for '{}'", file.getOriginalFilename(), e);
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }
}
