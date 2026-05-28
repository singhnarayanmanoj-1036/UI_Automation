package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtility {

    private static final Logger log = LogManager.getLogger(ScreenshotUtility.class);
    private static final String SCREENSHOT_DIR = "screenshots";

    /**
     * Captures a screenshot on test failure.
     * - Saves with timestamp so the filename is unique per run.
     * - Also saves an overwriting copy named just by testName (no timestamp)
     *   so the "latest failure" is always easy to find.
     *
     * @return path to the timestamped screenshot file
     */
    public static String capture(WebDriver driver, String testName) {
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            // Sanitise test name for use as a filename
            String safeName = testName.replaceAll("[^a-zA-Z0-9_\\-]", "_");

            // Timestamped file — unique record of this failure
            String timestampedPath = SCREENSHOT_DIR + "/" + safeName + "_" + timestamp + ".png";

            // Latest-only file — always overwritten, easy to find
            String latestPath = SCREENSHOT_DIR + "/" + safeName + "_LATEST.png";

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Copy to timestamped path
            Files.copy(src.toPath(), Paths.get(timestampedPath),
                    StandardCopyOption.REPLACE_EXISTING);

            // Copy to latest path (overwrites previous)
            Files.copy(src.toPath(), Paths.get(latestPath),
                    StandardCopyOption.REPLACE_EXISTING);

            log.info("Screenshot saved: {}", timestampedPath);
            return timestampedPath;

        } catch (IOException e) {
            log.warn("Failed to capture screenshot for '{}': {}", testName, e.getMessage());
            return null;
        }
    }
}
