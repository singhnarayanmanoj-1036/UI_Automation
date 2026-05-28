# AI Ecommerce Automation Framework — Coding Standards

## Technology Stack
- Java 11, Selenium WebDriver 4.x, TestNG 7.x
- ExtentReports 5.x + Allure 2.x for reporting
- Log4j2 for logging, Jackson for JSON test data
- WebDriverManager for automatic driver management

## Page Object Model Rules
- One class per AUT page in `src/test/java/pages/`
- All locators defined as `private final By` fields at the top of the class
- No test logic inside page objects — page objects only interact with the UI
- All page objects extend `BasePage`
- Every page object constructor accepts `WebDriver driver` and calls `super(driver)`

## Locator Strategy (in priority order)
1. CSS selectors — preferred for all elements
2. ARIA roles / accessibility attributes
3. ID selectors
4. XPath — only when no CSS or ARIA alternative exists
5. Never use absolute XPath

## Wait Strategy
- Always use `WebDriverWait` + `ExpectedConditions` — never `Thread.sleep()`
- Use `waitForVisible(By)` for elements that need to be seen
- Use `waitForClickable(By)` before clicking any button or link
- Use `waitForPageLoad()` after navigation
- Use `waitForAngular()` after actions that trigger Angular HTTP calls
- Default wait: 15 seconds | Long wait: 25 seconds | Page load: 30 seconds
- `pause(millis)` is available but use sparingly — only for Angular render delays

## Test Class Rules
- All test classes extend `BaseTest`
- Every `@Test` method must declare `groups` — either `{"smoke"}`, `{"regression"}`, or both
- Every `@Test` method must set `retryAnalyzer = RetryAnalyzer.class`
- No hardcoded URLs, credentials, product names, or search terms in test classes
- All test data comes from `TestDataProvider` reading `testdata/testdata.json`

## Reporting
- ExtentReports: HTML report written to `reports/` after each run
- Allure: JSON results written to `target/allure-results/` — run `mvn allure:report` to generate HTML
- Screenshots on failure: saved to `screenshots/` — folder cleared before each run
- Each failure saves two files: `{testName}_{timestamp}.png` and `{testName}_LATEST.png`

## Logging
- Use Log4j2 `LogManager.getLogger(ClassName.class)` — never `System.out.println`
- Log at INFO for normal flow, WARN for recoverable issues, ERROR for failures
- Logs written to `logs/` directory

## Naming Conventions
- Test methods: `testCamelCase` (e.g., `testValidLogin`)
- Page object methods: verb + noun (e.g., `searchFor`, `getProductDetails`)
- Locator fields: descriptive noun (e.g., `usernameInput`, `loginButton`)
- Test data keys: camelCase matching `testdata.json` keys

## What NOT to do
- No `Thread.sleep()` anywhere in the codebase
- No hardcoded waits or timeouts in test methods
- No mixing of test logic and page interaction logic
- No direct `driver.findElement()` calls in test classes — use page object methods
- No parallel execution — `testng.xml` uses `parallel="none"`
