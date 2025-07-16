package com.java_web.backend.service;

import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class PromptUtil {
    public static String readPrompt(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
    }
} 