package com.privatbank.test_task.scheduler;

import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.model.Version;
import com.privatbank.test_task.service.CardRangeService;
import com.privatbank.test_task.service.FileService;
import com.privatbank.test_task.service.VersionService;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RefreshCardDataScheduler {

    @Value("${app.zip-file-path}")
    private String zipFilePath;

    private Path ZIP_FILE_PATH;

    @PostConstruct
    public void init() {
        ZIP_FILE_PATH = Path.of(zipFilePath);
    }

    private final FileService fileService;
    private final VersionService versionService;
    private final CardRangeService cardRangeService;

    @Timed(value = "data.download_and_process.time", description = "Time taken to download and process data")
    @Scheduled(fixedRate = 60 * 60 * 1000) // get new data every hour
    @SchedulerLock(name = "get_card_range_data", lockAtMostFor = "4m", lockAtLeastFor = "1m")
    public void getBankInfoByCard() {
        try {
            fileService.downloadZipFile();
            fileService.unzipFile(ZIP_FILE_PATH);
            List<CardRange> allCardRanges = fileService.extractAndParseJson();

            cardRangeService.saveAllCardRanges(allCardRanges);
            log.info("All ranges saved successfully.");
        } catch (Exception e) {
            log.error("Error during data processing: {}", e.getMessage());
        }
    }

    @Timed(value = "data.clear_old_versions.time", description = "Time taken to clear old versions")
    @Scheduled(fixedRate = 2 * 60 * 60 * 1000, initialDelay = 10 * 60 * 1000) // clear old data every 2 hours
    @SchedulerLock(name = "clear_old_card_range_data", lockAtMostFor = "3m", lockAtLeastFor = "1m")
    public void clearCardRangeData() {
        List<Version> oldVersions = versionService.getVersionsCreatedBefore(versionService.getNewestVersion().getCreatedAt());
        if (oldVersions.isEmpty()) {
            log.info("No old versions to clear");
            return;
        }
        oldVersions.forEach(version -> versionService.deleteVersion(version.getId()));
    }
}
