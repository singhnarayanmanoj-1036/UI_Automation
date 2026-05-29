# Implementation Plan: YouTube UI Automation Framework

## Overview

Implement a Java 11 + Selenium WebDriver 4.x automation framework for YouTube (https://www.youtube.com), following the Page Object Model pattern. The framework covers 20 test cases across 4 test classes: HomeTest, SearchTest, VideoTest, and NavigationTest. All infrastructure (BaseTest, BasePage, utilities, listeners) is reused from the original scaffold.

---

## Tasks

- [x] 1. Scaffold Maven project structure and configure `pom.xml`
  - Maven directory tree: `src/test/java/base/`, `src/test/java/pages/`, `src/test/java/tests/`, `src/test/java/utils/`, `src/test/java/listeners/`, `testdata/`, `reports/`, `screenshots/`, `logs/`
  - `pom.xml` with pinned dependencies: Selenium Java 4.x, WebDriverManager, TestNG 7.x, ExtentReports 5.x, Log4j2, Allure TestNG, jqwik, Mockito, Jackson
  - Maven Surefire plugin configured with `<suiteXmlFiles>` pointing to `testng.xml`
  - _Requirements: 5.1, 7.4_

- [x] 2. Implement `BaseTest` and `BasePage`
  - `BaseTest`: `@BeforeSuite` (init ExtentReporter, clear screenshots), `@BeforeMethod` (launch WebDriver via WebDriverManager), `@AfterMethod` (screenshot on failure, quit driver), `@AfterSuite` (flush report)
  - `BASE_URL` loaded from `testdata/config.properties` — currently `https://www.youtube.com`
  - Guard against double-init from `allure-testng` ServiceLoader registration
  - `BasePage`: `waitForVisible`, `waitForClickable`, `waitForVisibleLong`, `waitForPageLoad`, `waitForAngular`, `pause`, `getPageTitle`
  - _Requirements: 5.2, 5.3, 7.5, 7.6, 8.5_

- [x] 3. Implement utility classes
  - `TestDataProvider`: loads `testdata/testdata.json`, `getData(key)`, `hasKey(key)`, `@DataProvider getLoginData()`
  - `TestDataNotFoundException`: extends `RuntimeException`, message includes key and file path
  - `ScreenshotUtility`: captures PNG to `screenshots/{testName}_{timestamp}.png`, returns path, logs WARN on failure
  - `ExtentReporter`: `ThreadLocal<ExtentTest>`, `init()` with null-guard, `startTest`, `log`, `embedScreenshot`, `endTest`, `flush`
  - `RetryAnalyzer`: `MAX_RETRY_COUNT = 1`, retries once before marking failed
  - _Requirements: 8.1–8.6, 9.1–9.3_

- [x] 4. Implement Page Objects
  - [x] 4.1 `HomePage` — `open(baseUrl)`, `dismissCookieConsent()`, `isLogoVisible()`, `isSearchBoxVisible()`, `hasVideoThumbnails()`, `getVideoThumbnailCount()`, `searchFor(query)`, `getCurrentUrl()`
    - Cookie consent: 5 s wait for reject button; silent catch if absent
    - _Requirements: 1.1–1.6, 5.4, 5.6_

  - [x] 4.2 `SearchResultsPage` — `hasResults()`, `getResultCount()`, `getFirstVideoTitle()`, `firstResultTitleContains(keyword)`, `clickFirstResult()`, `getPageTitle()`, `getCurrentUrl()`, `urlContainsSearchQuery(query)`, `searchFor(query)`, `hasChannelNames()`
    - _Requirements: 2.1–2.6, 5.4, 5.6_

  - [x] 4.3 `VideoPage` — `isVideoPlayerVisible()`, `getVideoTitle()`, `isVideoTitleVisible()`, `getChannelName()`, `isChannelNameVisible()`, `isShareButtonVisible()`, `hasRelatedVideos()`, `isDescriptionVisible()`, `isProgressBarVisible()`, `isMuteButtonVisible()`, `pauseVideo()`, `urlContainsWatch()`, `getCurrentUrl()`
    - `pauseVideo()` uses `JavascriptExecutor` to call `video.pause()`
    - _Requirements: 3.1–3.6, 5.4, 5.6_

- [x] 5. Implement `TestNGListener`
  - Implements `ITestListener`; registered in `testng.xml` via `<listeners>`
  - `onTestStart`: Log4j2 info
  - `onTestSuccess`: ExtentReporter PASS
  - `onTestFailure`: ScreenshotUtility capture, ExtentReporter embed + FAIL, Allure attachment
  - `onTestSkipped`: ExtentReporter SKIP
  - _Requirements: 6.1–6.4, 9.4_

- [x] 6. Create test data files
  - `testdata/testdata.json`: keys `searchTermExisting`, `searchTermTrending`, `searchTermShorts`, `searchTermMusic`, `searchTermNonExistent`, `expectedHomeTitle`, `expectedSearchResultsTitle`
  - `testdata/config.properties`: `baseUrl=https://www.youtube.com`, `browser=chrome`, `implicitWait=10`, `headless=false`, `maxRetry=1`
  - _Requirements: 8.1–8.5_

- [x] 7. Implement test classes (20 tests total)
  - [x] 7.1 `HomeTest` (5 tests)
    - `testHomePageTitle` — smoke, regression — title contains "YouTube"
    - `testLogoIsVisible` — smoke, regression — logo visible
    - `testSearchBoxIsVisible` — smoke, regression — search box visible
    - `testHomePageHasVideoThumbnails` — regression — thumbnails present
    - `testHomePageUrl` — regression — URL contains "youtube.com"
    - _Requirements: 1.1–1.5_

  - [x] 7.2 `SearchTest` (5 tests)
    - `testSearchReturnsResults` — smoke, regression — results returned
    - `testSearchResultsUrl` — smoke, regression — URL has search_query param
    - `testSearchResultsPageTitle` — regression — title contains search term
    - `testSearchResultsHaveChannelNames` — regression — channel names visible
    - `testSearchFromResultsPage` — regression — re-search from results page
    - _Requirements: 2.1–2.5_

  - [x] 7.3 `VideoTest` (5 tests)
    - `testVideoPlayerIsVisible` — smoke, regression — player visible
    - `testVideoPageUrl` — smoke, regression — URL contains /watch
    - `testVideoTitleIsVisible` — regression — title non-empty
    - `testChannelNameIsVisible` — regression — channel name non-empty
    - `testRelatedVideosAreShown` — regression — related videos present
    - _Requirements: 3.1–3.5_

  - [x] 7.4 `NavigationTest` (5 tests)
    - `testDirectNavigationToHome` — smoke, regression — direct nav loads home
    - `testBackFromSearchResultsToHome` — regression — back stays on youtube.com
    - `testSearchThenOpenVideoChangesUrl` — regression — URL changes to /watch
    - `testBackFromVideoToSearchResults` — regression — back returns to results URL
    - `testPageTitleUpdatesAfterSearch` — regression — title changes after search
    - _Requirements: 4.1–4.5_

- [x] 8. Configure `testng.xml`
  - Suite name: "YouTube Automation Suite", `parallel="none"`, `verbose="1"`
  - `<listeners>` block with `TestNGListener`
  - Four `<test>` blocks: Home Tests, Search Tests, Video Tests, Navigation Tests
  - _Requirements: 7.4, 9.4_

- [x] 9. Fix duplicate listener / double-init issue
  - `BaseTest.suiteInitialized` volatile boolean flag prevents `@BeforeSuite` from running twice
  - `ExtentReporter.init()` null-checks `extent` before creating new instance
  - `RetryAnalyzer.MAX_RETRY_COUNT` reduced to `1` to prevent excessive retries
  - _Requirements: 9.1, 9.2_

- [ ] 10. Checkpoint — verify full suite runs green
  - Run `mvn test` and confirm all 20 tests execute
  - Confirm `reports/ExtentReport_{timestamp}.html` is generated
  - Confirm `logs/test-run_{timestamp}.log` is generated
  - Confirm no `Thread.sleep()` calls remain in any source file
  - Confirm no hardcoded URLs or search terms in test or page classes

- [ ] 11. (Optional) Add smoke and regression suite XML files
  - `testng-smoke.xml` — includes only `groups = {"smoke"}` tests (9 smoke tests)
  - `testng-regression.xml` — includes all `groups = {"regression"}` tests (20 tests)
  - _Requirements: 7.2, 7.3_

- [ ] 12. (Optional) Property-based tests
  - Property 6: Missing key throws `TestDataNotFoundException` — jqwik `@Property`, 100 tries
  - Property 7: `RetryAnalyzer` retries once then returns false — jqwik `@Property`
  - Property 8: Screenshot captured and embedded on failure — Mockito mock of WebDriver
  - _Requirements: 8.3, 9.2_

---

## Notes

- All 20 tests are implemented and the framework is demo-ready
- `parallel="none"` is intentional — YouTube rate-limits parallel browser sessions
- Cookie consent handling is built into `HomePage.open()` — no manual intervention needed
- `pauseVideo()` must be called before asserting video metadata to prevent autoplay interference
- `RetryAnalyzer.MAX_RETRY_COUNT = 1` — each test gets one retry (2 total executions max)
- `BASE_URL` is always read from `config.properties` via `BaseTest` — never hardcoded in test classes
- Tasks 11 and 12 are optional enhancements for post-demo polish

---

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1"] },
    { "id": 1, "tasks": ["2", "3"] },
    { "id": 2, "tasks": ["4.1", "4.2", "4.3", "5", "6"] },
    { "id": 3, "tasks": ["7.1", "7.2", "7.3", "7.4"] },
    { "id": 4, "tasks": ["8", "9"] },
    { "id": 5, "tasks": ["10"] },
    { "id": 6, "tasks": ["11", "12"] }
  ]
}
```
