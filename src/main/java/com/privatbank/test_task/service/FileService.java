package com.privatbank.test_task.service;

import com.privatbank.test_task.model.CardRange;

import java.nio.file.Path;
import java.util.List;

public interface FileService {
    void downloadZipFile();
    void unzipFile(Path zipFilePath);
    List<CardRange> extractAndParseJson();
}
