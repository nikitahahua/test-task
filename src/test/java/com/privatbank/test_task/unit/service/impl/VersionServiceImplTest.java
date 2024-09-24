package com.privatbank.test_task.unit.service.impl;

import com.privatbank.test_task.model.Version;
import com.privatbank.test_task.repository.VersionRepository;
import com.privatbank.test_task.service.impl.VersionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VersionServiceImplTest {

    @Mock
    private VersionRepository versionRepository;

    @InjectMocks
    private VersionServiceImpl versionService;

    private Version version;


    @Before
    public void setUp() {
        version = new Version();
        MockitoAnnotations.initMocks(this);
        version.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testGetVersionsCreatedBefore() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
        when(versionRepository.findAllByCreatedAtBefore(dateTime)).thenReturn(Collections.singletonList(version));
        List<Version> versions = versionService.getVersionsCreatedBefore(dateTime);
        assertEquals(1, versions.size());
        assertEquals(version, versions.get(0));
    }

    @Test
    public void testGetVersionsCreatedBeforeWhenVersionsDontExistThenThrowNullDateTime() {
        assertThrows(IllegalArgumentException.class, () -> versionService.getVersionsCreatedBefore(null));
    }

    @Test
    public void testGetVersionsCreatedBefore_dataAccessException() {
        when(versionRepository.findAllByCreatedAtBefore(any())).thenThrow(new DataAccessException("Database error") {});

        assertThrows(RuntimeException.class, () -> versionService.getVersionsCreatedBefore(LocalDateTime.now()));
    }

    @Test
    public void testAddCorrectVersionThenSuccess() {
        when(versionRepository.save(version)).thenReturn(version);
        Version savedVersion = versionService.addVersion(version);
        assertEquals(version, savedVersion);
        verify(versionRepository).save(version);
    }

    @Test
    public void testUpdateVersionWhenVersionDoesNotExistThenThrowIllegalArgumentException() {
        Long versionId = 1L;
        when(versionRepository.existsById(versionId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> versionService.updateVersion(versionId, version));
    }

    @Test
    public void testUpdateVersionThenSuccess() {
        Long versionId = 1L;
        when(versionRepository.existsById(versionId)).thenReturn(true);
        when(versionRepository.findById(versionId)).thenReturn(Optional.of(version));
        when(versionRepository.save(version)).thenReturn(version);

        Version updatedVersion = versionService.updateVersion(versionId, version);

        assertEquals(version, updatedVersion);
        verify(versionRepository).save(version);
    }

    @Test
    public void testDeleteVersionWhenVersionDoesNotExistThenThrowVersionNotFound() {
        Long versionId = 1L;
        when(versionRepository.existsById(versionId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> versionService.deleteVersion(versionId));
    }

    @Test
    public void testDeleteVersionWhenVersionExistsThenDeleteVersion() {
        Long versionId = 1L;
        when(versionRepository.existsById(versionId)).thenReturn(true);
        versionService.deleteVersion(versionId);
        verify(versionRepository).deleteById(versionId);
    }

    @Test
    public void testGetNewestVersionWhenVersionDoesExistThenThrowResponseStatusException() {
        when(versionRepository.findNewestVersion()).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> versionService.getNewestVersion());
    }

    @Test
    public void testGetNewestVersionWhenVersionExistsThanReturnVersion() {
        when(versionRepository.findNewestVersion()).thenReturn(Optional.of(version));
        Version newestVersion = versionService.getNewestVersion();
        assertEquals(version, newestVersion);
    }
}
