package com.privatbank.test_task.service.impl;

import com.privatbank.test_task.model.Version;
import com.privatbank.test_task.repository.VersionRepository;
import com.privatbank.test_task.service.VersionService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VersionServiceImpl implements VersionService {

    private final VersionRepository versionRepository;

    public VersionServiceImpl(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    @Override
    public List<Version> getVersionsCreatedBefore(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("The dateTime parameter cannot be null.");
        }
        try {
            return versionRepository.findAllByCreatedAtBefore(dateTime);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error while retrieving versions created before " + dateTime, e);
        }
    }

    @Transactional
    @Override
    public Version addVersion(Version version) {
        return versionRepository.save(version);
    }

    @Transactional
    @Override
    public Version updateVersion(Long versionId, Version updatedVersion) {
        if (!versionRepository.existsById(versionId)) {
            throw new IllegalArgumentException("Version with ID " + versionId + " does not exist.");
        }

        Version existingVersion = versionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found"));

        existingVersion.setCreatedAt(updatedVersion.getCreatedAt());
        existingVersion.setCardRanges(updatedVersion.getCardRanges());

        return versionRepository.save(existingVersion);
    }

    @Transactional
    @Override
    public void deleteVersion(Long versionId) {
        if (!versionRepository.existsById(versionId)) {
            throw new IllegalArgumentException("Version with ID " + versionId + " does not exist.");
        }
        versionRepository.deleteById(versionId);
    }

    @Override
    public Version getNewestVersion() {
        return versionRepository.findNewestVersion()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No versions found"));
    }
}
