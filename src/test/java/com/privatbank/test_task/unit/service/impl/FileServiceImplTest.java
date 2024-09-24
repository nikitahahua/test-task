package com.privatbank.test_task.unit.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.service.impl.FileServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private ObjectMapper objectMapper;

    private String apiUrl;

    private String filePath;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        apiUrl = "https://ecom-bininfo.s3.eu-west-1.amazonaws.com/bininfo.json.zip";
        filePath =  "bininfo.json";
    }

    @Test
    public void testDownloadZipFile_Success() throws Exception {
        URL url = mock(URL.class);
        InputStream inputStream = mock(InputStream.class);
        when(url.openConnection()).thenReturn(mock(java.net.URLConnection.class));
        when(url.openConnection().getInputStream()).thenReturn(inputStream);

        doNothing().when(Files.class);
        Files.copy(any(InputStream.class), any(Path.class));

        fileService.downloadZipFile();
    }

    @Test
    public void testDownloadZipFile_Failure() throws Exception {
        when(new URL(apiUrl).openConnection()).thenThrow(new IOException("Connection failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileService.downloadZipFile();
        });

        assertEquals("Failed to download the ZIP file", exception.getMessage());
    }

    @Test
    public void testExtractAndParseJson_Success() throws IOException {
        List<CardRange> expectedCardRanges = Collections.singletonList(new CardRange());

        when(objectMapper.readValue(eq(new File(filePath)), any(TypeReference.class)))
                .thenReturn(expectedCardRanges);

        List<CardRange> actualCardRanges = fileService.extractAndParseJson();

        assertEquals(expectedCardRanges, actualCardRanges);
        verify(objectMapper).readValue(eq(new File(filePath)), any(TypeReference.class));
    }

    @Test
    public void testExtractAndParseJson_Failure() throws IOException {
        when(objectMapper.readValue(eq(new File(filePath)), any(TypeReference.class)))
                .thenThrow(new IOException("Parse error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileService.extractAndParseJson();
        });

        assertEquals("Failed to parse JSON from file: path/to/file.json", exception.getMessage());
    }
}
