package com.karabo.resume_analyzer.exception;

public class AIAnalysisException extends RuntimeException {
    public AIAnalysisException(String message) {
        super(message);
    }
    public AIAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
