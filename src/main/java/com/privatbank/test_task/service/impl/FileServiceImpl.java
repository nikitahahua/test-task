package com.privatbank.test_task.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.service.FileService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${app.api-url}")
    private String apiUrl;

    @Value("${app.zip-file-path}")
    private String zipFilePath;

    @Value("${app.file-path}")
    private String filePath;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MeterRegistry meterRegistry;

    @Override
    @Retryable(maxAttempts = 4, backoff = @Backoff(2000))
    @Timed(value = "file.download.time")
    public void downloadZipFile() {
        try (InputStream inputStream = new URL(apiUrl).openConnection().getInputStream()) {
            deleteRecentFiles();
            Files.copy(inputStream, Path.of(zipFilePath));
            log.info("ZIP file downloaded successfully.");
        } catch (IOException e) {
            log.error("Failed to download the ZIP file", e);
            throw new RuntimeException("Failed to download the ZIP file", e);
        }
    }

    @Recover
    public String recover(RuntimeException e) {
        log.error("Recovery method called after failed attempts to download ZIP file: {}", e.getMessage());
        meterRegistry.counter("file.download.failures").increment();
        return "Failed to download ZIP file after multiple attempts. Error: " + e.getMessage();
    }

    @Timed(value = "file.unzip.time", description = "Time taken to unzip files")
    @Override
    public void unzipFile(Path zipFilePath) {
        String fileBaseName = FilenameUtils.getBaseName(zipFilePath.getFileName().toString());
        Path entryPath = Path.of(fileBaseName);

        try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().startsWith("__MACOSX")) {
                    continue;
                }
                if (entryPath.startsWith(entryPath)) {
                    try (InputStream in = zipFile.getInputStream(entry);
                         OutputStream out = Files.newOutputStream(entryPath)) {
                        IOUtils.copy(in, out);
                    }
                }
            }
            log.info("File is unzipped.");
        } catch (IOException e) {
            log.error("Failed to unzip file", e);
            throw new RuntimeException(e);
        }
    }

    @Timed(value = "file.parse.json.time", description = "Time taken to parse JSON from file")
    @Override
    public List<CardRange> extractAndParseJson() {
        List<CardRange> cardRanges;
        try {
            cardRanges = objectMapper.readValue(Path.of(filePath).toFile(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CardRange.class));
        } catch (IOException e) {
            log.error("Failed to parse JSON from file: " + zipFilePath, e);
            throw new RuntimeException("Failed to parse JSON from file: " + zipFilePath, e);
        }
        return cardRanges;
    }

    private void deleteRecentFiles() throws IOException {
        if (Files.exists(Path.of(zipFilePath))) {
            Files.delete(Path.of(zipFilePath));
            log.info("Old ZIP file deleted.");
        }
        if (Files.exists(Path.of(filePath))) {
            try {
                Files.delete(Path.of(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info("Old unzipped file deleted.");
        }
    }
}

