package com.privatbank.test_task.service;

import com.privatbank.test_task.model.Version;

import java.time.LocalDateTime;
import java.util.List;

public interface VersionService {
    List<Version> getVersionsCreatedBefore(LocalDateTime dateTime);
    Version addVersion(Version version);
    Version updateVersion(Long versionId, Version updatedVersion);
    void deleteVersion(Long versionId);
    Version getNewestVersion();
}
