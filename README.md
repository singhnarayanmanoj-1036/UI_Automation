# YouTube UI Automation Framework

A Selenium WebDriver + TestNG automation framework for [YouTube](https://www.youtube.com), following the Page Object Model pattern with ExtentReports, Allure, Log4j2, and property-based testing via jqwik.

---

## Technology Stack

| Concern | Technology |
|---|---|
| Language | Java 11 |
| Browser Automation | Selenium WebDriver 4.x |
| Driver Management | WebDriverManager (io.github.bonigarcia) |
| Test Framework | TestNG 7.x |
| Build Tool | Maven |
| Reporting | ExtentReports 5.x + Allure 2.x |
| Logging | Log4j2 |
| Test Data | JSON + `.properties` files |
| Property-Based Testing | jqwik 1.8.1 |
| Mocking | Mockito 5.11.0 |

---

## Project Structure

```
src/test/java/
  base/           BaseTest.java — WebDriver lifecycle, BASE_URL, suite setup/teardown
  pages/          Page Object classes (HomePage, SearchResultsPage, VideoPage, BasePage)
  tests/          Test classes (HomeTest, SearchTest, VideoTest, NavigationTest)
  utils/          TestDataProvider, ExtentReporter, ScreenshotUtility, RetryAnalyzer
  listeners/      TestNGListener (ITestListener — reporting hooks)

testdata/
  testdata.json       Test input values (search terms, expected strings)
  config.properties   Runtime configuration (baseUrl, browser, wait timeouts)

reports/            ExtentReports HTML output (generated per run)
screenshots/        Failure screenshots (cleared before each run)
logs/               Log4j2 rolling log files (one per run)
target/
  allure-results/   Allure JSON results (run `mvn allure:report` for HTML)
  surefire-reports/ TestNG built-in reports (index.html, emailable-report.html)
```

---

## Configuration

### `testdata/config.properties`

Runtime settings read by `BaseTest`:

```properties
baseUrl=https://www.youtube.com
browser=chrome
implicitWait=10
headless=false
maxRetry=1
```

### `testdata/testdata.json`

All test input values — no hardcoded data in test or page classes:

```json
{
  "searchTermExisting": "Selenium WebDriver tutorial",
  "searchTermTrending": "Java programming",
  "searchTermShorts": "funny cats shorts",
  "searchTermMusic": "lofi hip hop",
  "searchTermNonExistent": "xyznonexistentvideo999abcdef123",
  "expectedHomeTitle": "YouTube",
  "expectedSearchResultsTitle": "Selenium WebDriver tutorial - YouTube"
}
```

---

## Test Coverage — 20 Test Cases

| Class | Test | Groups |
|---|---|---|
| `HomeTest` | `testHomePageTitle` | smoke, regression |
| `HomeTest` | `testLogoIsVisible` | smoke, regression |
| `HomeTest` | `testSearchBoxIsVisible` | smoke, regression |
| `HomeTest` | `testHomePageHasVideoThumbnails` | regression |
| `HomeTest` | `testHomePageUrl` | regression |
| `SearchTest` | `testSearchReturnsResults` | smoke, regression |
| `SearchTest` | `testSearchResultsUrl` | smoke, regression |
| `SearchTest` | `testSearchResultsPageTitle` | regression |
| `SearchTest` | `testSearchResultsHaveChannelNames` | regression |
| `SearchTest` | `testSearchFromResultsPage` | regression |
| `VideoTest` | `testVideoPlayerIsVisible` | smoke, regression |
| `VideoTest` | `testVideoPageUrl` | smoke, regression |
| `VideoTest` | `testVideoTitleIsVisible` | regression |
| `VideoTest` | `testChannelNameIsVisible` | regression |
| `VideoTest` | `testRelatedVideosAreShown` | regression |
| `NavigationTest` | `testDirectNavigationToHome` | smoke, regression |
| `NavigationTest` | `testBackFromSearchResultsToHome` | regression |
| `NavigationTest` | `testSearchThenOpenVideoChangesUrl` | regression |
| `NavigationTest` | `testBackFromVideoToSearchResults` | regression |
| `NavigationTest` | `testPageTitleUpdatesAfterSearch` | regression |

**Smoke tests (9):** `testHomePageTitle`, `testLogoIsVisible`, `testSearchBoxIsVisible`, `testSearchReturnsResults`, `testSearchResultsUrl`, `testVideoPlayerIsVisible`, `testVideoPageUrl`, `testDirectNavigationToHome`

> **Note:** `testng.xml` currently runs only `HomeTest` (5 tests). The Search, Video, and Navigation `<test>` blocks have been removed. Add them back to run the full 20-test suite (see the Running Tests section below).

---

## Key Classes

### `BaseTest` (`src/test/java/base/BaseTest.java`)

Root class for all test classes. Manages the WebDriver lifecycle via TestNG annotations.

- **`BASE_URL`** — `public static final String` loaded from `testdata/config.properties`. Currently `https://www.youtube.com`. Never hardcode the base URL in test classes.
- **`@BeforeSuite`** — clears `screenshots/`, initializes ExtentReporter. Guard against double-init from `allure-testng` ServiceLoader.
- **`@BeforeMethod`** — reads `browser` system property, launches WebDriver via WebDriverManager, maximizes window, creates `WebDriverWait`. Driver initialization is wrapped in try/catch — any setup failure is rethrown so TestNG marks the test as FAILED rather than SKIPPED.
- **`@AfterMethod`** — captures screenshot on failure, ends ExtentReporter test node, quits driver.
- **`@AfterSuite`** — flushes ExtentReporter HTML report.

### `BasePage` (`src/test/java/pages/BasePage.java`)

Shared base for all Page Objects. Provides:
- `waitForVisible(By)` — waits up to 15 s for element visibility.
- `waitForClickable(By)` — waits up to 15 s for element to be clickable.
- `waitForPageLoad()` — waits for `document.readyState == "complete"`.
- `waitForAngular()` — waits for Angular HTTP calls to settle (no-op on non-Angular pages).
- `pause(millis)` — use sparingly for render delays only.

### `HomePage` (`src/test/java/pages/HomePage.java`)

- `open(baseUrl)` — navigates to YouTube, waits for Angular bootstrap, then auto-dismisses cookie consent dialogs (EU regions) and sign-in prompts.
- `dismissCookieConsent()` — clicks the reject/dismiss button on cookie dialogs. No-op if absent.
- `dismissSignInPrompt()` — dismisses "Sign in" overlay prompts if they appear. No-op if absent.
- `searchFor(query)` — locates the search input via JavaScript (shadow DOM safe for Chrome 148+), types the query, and submits with ENTER. Falls back to direct URL navigation (`/results?search_query=`) if JS interaction fails. Returns `SearchResultsPage`.
- `isSearchBoxVisible()` — uses JavaScript to locate the input, safe against YouTube's shadow DOM embedding of `<ytd-searchbox>` in Chrome 148+.

> **Chrome 148+ shadow DOM note:** YouTube embeds the search input inside a `<ytd-searchbox>` shadow root in Chrome 148+. Standard CSS selectors cannot pierce shadow roots, so `HomePage` uses `JavascriptExecutor` to locate and interact with the input. A direct URL fallback ensures search always works even if JS interaction fails.

### `SearchResultsPage` (`src/test/java/pages/SearchResultsPage.java`)

- `hasResults()` — returns `true` if at least one video result is present.
- `clickFirstResult()` — clicks the first video title and returns `VideoPage`.
- `searchFor(query)` — re-searches from the results page.

### `VideoPage` (`src/test/java/pages/VideoPage.java`)

- `isVideoPlayerVisible()` — confirms the HTML5 player is rendered.
- `pauseVideo()` — pauses via JavaScript to prevent autoplay interference during assertions.
- `urlContainsWatch()` — confirms the URL is a watch page.

### `RetryAnalyzer` (`src/test/java/utils/RetryAnalyzer.java`)

`MAX_RETRY_COUNT = 1` — a failed test is retried **once** before being marked as failed. Referenced via `retryAnalyzer = RetryAnalyzer.class` on every `@Test` method.

---

## Running Tests

### Current active suite (Home Tests only)

The default `testng.xml` runs only `HomeTest` (5 tests). The Search, Video, and Navigation `<test>` blocks have been removed. To run the full suite, add them back to `testng.xml`:

```xml
<test name="Search Tests">
    <classes><class name="tests.SearchTest"/></classes>
</test>
<test name="Video Tests">
    <classes><class name="tests.VideoTest"/></classes>
</test>
<test name="Navigation Tests">
    <classes><class name="tests.NavigationTest"/></classes>
</test>
```

Then run:

```bash
mvn test
```

### Run the full suite (all 20 tests)

Add all four `<test>` blocks to `testng.xml` (see above), then:

```bash
mvn test
```

### Run a specific test class

```bash
mvn test -Dtest=tests.HomeTest
```

### Run a specific test method

```bash
mvn test -Dtest=tests.SearchTest#testSearchReturnsResults
```

### Override browser

```bash
mvn test -Dbrowser=firefox
```

### Run headless

```bash
mvn test -Dheadless=true
```

### Generate Allure HTML report

```bash
mvn allure:report
```

Report is written to `target/allure-report/index.html`.

---

## Writing New Tests

1. Extend `BaseTest`.
2. Use `BASE_URL` (from `BaseTest`) for navigation — never hardcode URLs.
3. Read all test input from `TestDataProvider` — never hardcode search terms or expected strings.
4. Annotate every `@Test` method with a `groups` attribute and `retryAnalyzer = RetryAnalyzer.class`.
5. Use page object methods for all browser interactions — no `driver.findElement()` in test classes.

```java
@Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
      description = "Search should return results")
public void testSearchReturnsResults() {
    HomePage home = new HomePage(driver);
    home.open(BASE_URL);
    SearchResultsPage results = home.searchFor(testData.getData("searchTermExisting"));
    Assert.assertTrue(results.hasResults(), "Search should return at least one result");
}
```

---

## Reports and Artifacts

| Artifact | Location |
|---|---|
| ExtentReports HTML | `reports/ExtentReport_{timestamp}.html` |
| Allure HTML | `target/allure-report/index.html` (after `mvn allure:report`) |
| TestNG built-in | `target/surefire-reports/index.html` |
| Failure screenshots | `screenshots/{testName}_{timestamp}.png` |
| Execution logs | `logs/test-run_{timestamp}.log` |

---

## Known Behaviors

- **Cookie consent** — `HomePage.open()` auto-dismisses consent dialogs (EU regions). No manual action needed.
- **Sign-in prompt** — `HomePage.open()` also dismisses YouTube sign-in overlay prompts automatically.
- **Shadow DOM search input (Chrome 148+)** — YouTube embeds the search box inside a `<ytd-searchbox>` shadow root in Chrome 148+. `HomePage.searchFor()` and `isSearchBoxVisible()` use `JavascriptExecutor` to pierce the shadow root. If JS interaction fails, `searchFor()` falls back to direct URL navigation (`/results?search_query=`).
- **Video autoplay** — `VideoPage.pauseVideo()` is called before metadata assertions to prevent the player from navigating away.
- **Duplicate listener** — `allure-testng` registers a listener via ServiceLoader in addition to `testng.xml`. Both `BaseTest` and `ExtentReporter` guard against double-initialization.
- **Retry count** — `MAX_RETRY_COUNT = 1` means each test runs at most twice. Increase this constant in `RetryAnalyzer` if more retries are needed.
