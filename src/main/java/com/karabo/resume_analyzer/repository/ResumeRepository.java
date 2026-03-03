package com.karabo.resume_analyzer.repository;

import com.karabo.resume_analyzer.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
