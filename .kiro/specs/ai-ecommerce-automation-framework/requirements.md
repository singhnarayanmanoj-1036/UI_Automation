# Requirements Document

## Introduction

This document defines the requirements for an AI-Driven Ecommerce Automation Framework targeting the Advantage Online Shopping application (https://advantageonlineshopping.com). The framework is built using Java, Playwright, and TestNG, follows the Page Object Model (POM) design pattern, and leverages Kiro's AI-assisted engineering capabilities — including Spec Mode, Steering, and Hooks — to accelerate test development, reduce manual effort, and support scalable UI automation with CI/CD integration. TestNG serves as the primary test framework, providing annotations, suite management, parallel execution, data-driven testing, and reporting hooks.

---

## Glossary

- **Framework**: The AI-Driven Ecommerce Automation Framework specified in this document.
- **AUT**: Application Under Test — the Advantage Online Shopping web application at https://advantageonlineshopping.com.
- **POM**: Page Object Model — a design pattern that creates an object repository for web UI elements, separating page interaction logic from test logic.
- **Test_Runner**: The TestNG framework combined with the Maven Surefire plugin responsible for executing test suites defined in TestNG XML suite files (e.g., `testng.xml`, `testng-smoke.xml`, `testng-regression.xml`).
- **WebDriver**: The Playwright browser context managing Chrome browser sessions via Playwright's Chromium channel.
- **Page_Object**: A Java class encapsulating the UI elements and interactions for a specific page of the AUT.
- **Base_Test**: The base test class providing setup, teardown, and shared test utilities for all test classes.
- **Extent_Reporter**: The reporting component that generates HTML Extent Reports from test execution results.
- **Screenshot_Utility**: The utility component responsible for capturing and saving screenshots on test failure.
- **Login_Page**: The Page Object representing the AUT's login and authentication page.
- **Search_Page**: The Page Object representing the AUT's product search functionality.
- **Product_Page**: The Page Object representing the AUT's product detail and category pages.
- **Cart_Page**: The Page Object representing the AUT's shopping cart page.
- **Checkout_Page**: The Page Object representing the AUT's checkout flow pages.
- **Test_Data_Provider**: The component responsible for supplying test input data from external files.
- **CI_Pipeline**: The Jenkins-based continuous integration pipeline executing the Framework.
- **Smoke_Suite**: A subset of tests validating core application functionality.
- **Regression_Suite**: The full set of tests validating all automated application functionality.
- **Kiro**: The AI-assisted development environment used to generate specs, steering rules, and hooks.
- **Steering_File**: A Kiro configuration file defining coding standards and framework rules applied during AI code generation.
- **Hook**: A Kiro automation trigger that executes defined actions on IDE events.
- **WebDriverWait**: The Playwright timeout and wait mechanism used to synchronize test execution with page state.
- **TestNG**: The primary Java test framework providing annotations (`@Test`, `@BeforeMethod`, `@AfterMethod`, `@BeforeClass`, `@AfterClass`, `@BeforeSuite`, `@AfterSuite`), suite management, parallel execution, data-driven testing, and listener hooks.
- **TestNG_Suite**: A TestNG XML suite file (e.g., `testng.xml`, `testng-smoke.xml`, `testng-regression.xml`) that defines test classes, groups, parameters, and parallel execution settings.
- **TestNG_Listener**: A class implementing `ITestListener` that receives callbacks on test start, success, failure, and skip events, used for reporting and logging hooks.
- **TestNG_DataProvider**: A method annotated with `@DataProvider` that supplies parameterized test data to `@Test` methods, enabling data-driven test execution.
- **TestNG_RetryAnalyzer**: A class implementing `IRetryAnalyzer` that automatically re-runs failed tests up to a configurable maximum retry count to handle transient failures.
- **ProductDetails**: A Java data object containing name, price, description, and quantity fields for a product.
- **OrderLineItem**: A Java data object representing a single item in the checkout order summary.

---

## Requirements

### Requirement 1: User Authentication Automation

**User Story:** As a QA engineer, I want to automate user login scenarios against the AUT, so that I can validate authentication behavior without manual intervention.

#### Acceptance Criteria

1. WHEN a valid username and password are provided, THE Login_Page SHALL submit the credentials and navigate the user to the authenticated home page.
2. WHEN an invalid username or password is provided, THE Login_Page SHALL display an appropriate error message without navigating away from the login page.
3. WHEN a login attempt completes, THE Login_Page SHALL return the resulting page state to the calling test for assertion.
4. THE Login_Page SHALL expose a reusable `login(String username, String password)` method usable across all test classes.
5. IF the login form elements are not located within 10 seconds of page load, THEN THE Login_Page SHALL throw a descriptive `TimeoutException` identifying the missing element.

---

### Requirement 2: Product Search Automation

**User Story:** As a QA engineer, I want to automate product search functionality, so that I can validate that search returns correct and relevant results.

#### Acceptance Criteria

1. WHEN a search term is entered and submitted, THE Search_Page SHALL display a results list containing products matching the search term.
2. WHEN a search term returns no results, THE Search_Page SHALL display a no-results message accessible to the test for assertion.
3. WHEN a search result item is clicked, THE Search_Page SHALL navigate to the corresponding Product_Page.
4. THE Search_Page SHALL expose a reusable `searchFor(String term)` method that enters the term and submits the search form.
5. IF the search input element is not located within 10 seconds of page load, THEN THE Search_Page SHALL throw a descriptive `TimeoutException` identifying the missing element.

---

### Requirement 3: Product Selection and Validation

**User Story:** As a QA engineer, I want to automate product selection from categories, so that I can validate that product details are correctly displayed.

#### Acceptance Criteria

1. WHEN a product category is selected, THE Product_Page SHALL display a list of products belonging to that category.
2. WHEN a product is selected from the list, THE Product_Page SHALL display the product name, price, description, and available quantity.
3. THE Product_Page SHALL expose a reusable `selectProduct(String productName)` method that navigates to the specified product's detail view.
4. WHEN product details are retrieved, THE Product_Page SHALL return a `ProductDetails` object containing name, price, description, and quantity fields for assertion.
5. IF a specified product is not found on the page within 10 seconds, THEN THE Product_Page SHALL throw a descriptive `NoSuchElementException` identifying the missing product.

---

### Requirement 4: Add to Cart Automation

**User Story:** As a QA engineer, I want to automate adding products to the shopping cart, so that I can validate that cart state and product details are correctly maintained.

#### Acceptance Criteria

1. WHEN a product is added to the cart, THE Cart_Page SHALL increment the cart item count by the quantity added.
2. WHEN the cart is opened after adding a product, THE Cart_Page SHALL display the correct product name, unit price, and quantity for each added item.
3. WHEN the quantity of a cart item is updated to a new value, THE Cart_Page SHALL reflect the updated quantity and recalculate the line item total accordingly.
4. THE Cart_Page SHALL expose a reusable `addToCart(String productName, int quantity)` method usable across test classes.
5. IF the add-to-cart button is not interactable within 10 seconds, THEN THE Cart_Page SHALL throw a descriptive `ElementNotInteractableException` identifying the unresponsive element.

---

### Requirement 5: Checkout Flow Automation

**User Story:** As a QA engineer, I want to automate the checkout flow, so that I can validate that the order summary and checkout pages reflect the correct cart contents.

#### Acceptance Criteria

1. WHEN the checkout process is initiated from the cart, THE Checkout_Page SHALL display the checkout page with all required form sections visible.
2. WHEN the checkout page is loaded, THE Checkout_Page SHALL display an order summary listing each cart item with its name, quantity, and price.
3. WHEN the order summary is retrieved, THE Checkout_Page SHALL return a list of `OrderLineItem` objects matching the items added to the cart.
4. THE Checkout_Page SHALL expose a reusable `proceedToCheckout()` method that navigates from the cart to the first checkout step.
5. IF the checkout page does not load within 15 seconds, THEN THE Checkout_Page SHALL throw a descriptive `TimeoutException` identifying the page load failure.

---

### Requirement 6: UI Element Validation

**User Story:** As a QA engineer, I want to validate key UI elements across pages, so that I can ensure the AUT renders correctly and critical elements are accessible.

#### Acceptance Criteria

1. WHEN a page is loaded, THE Page_Object SHALL provide a `getPageTitle()` method returning the browser document title for assertion.
2. WHEN a button or link element is queried, THE Page_Object SHALL confirm the element is visible and enabled before returning it for interaction.
3. WHEN product information is displayed on a page, THE Page_Object SHALL confirm that the product name, price, and image elements are visible within the viewport.
4. WHEN the browser viewport is set to a width of 375 pixels, THE Page_Object SHALL confirm that primary navigation and product elements remain visible and interactable.
5. IF a required UI element is not visible within 10 seconds of page load, THEN THE Page_Object SHALL throw a descriptive `TimeoutException` identifying the element by its locator.

---

### Requirement 7: Framework Architecture and Page Object Model

**User Story:** As a framework developer, I want the framework to follow POM and a scalable folder structure, so that tests are maintainable, reusable, and easy to extend.

#### Acceptance Criteria

1. THE Framework SHALL organize source files into the folder structure: `src/test/java/base/`, `src/test/java/pages/`, `src/test/java/tests/`, `src/test/java/utils/`, and `src/test/java/listeners/`.
2. THE Framework SHALL implement a `Base_Test` class in `src/test/java/base/` providing `@BeforeSuite` and `@AfterSuite` methods for global Playwright instance setup and teardown, `@BeforeClass` and `@AfterClass` methods for class-level browser context setup and teardown, and `@BeforeMethod` and `@AfterMethod` methods for per-test page setup and teardown using TestNG annotations.
3. THE Framework SHALL annotate every test method with `@Test`, specifying the applicable `groups` attribute (e.g., `groups = {"smoke"}` or `groups = {"regression"}`) to enable TestNG group-based suite filtering.
4. THE Framework SHALL implement one Page_Object class per AUT page in `src/test/java/pages/`, encapsulating all locators and interaction methods for that page.
5. THE Framework SHALL store all test input data in external files under `testdata/` and supply them to tests via the `Test_Data_Provider`.
6. THE Framework SHALL use Playwright's Java API exclusively for all browser interactions, with no mixing of other browser automation libraries.
7. THE Framework SHALL define all Playwright locators using CSS selectors or ARIA roles, avoiding XPath unless no CSS or ARIA alternative exists.

---

### Requirement 8: Test Reporting

**User Story:** As a QA engineer, I want the framework to generate detailed HTML reports with screenshots, so that I can quickly diagnose test failures.

#### Acceptance Criteria

1. WHEN a test suite execution completes, THE Extent_Reporter SHALL generate an HTML report in the `reports/` directory containing pass, fail, and skip counts.
2. WHEN a test suite execution completes, THE TestNG_Listener SHALL generate TestNG's built-in `index.html` and `emailable-report.html` reports in the `target/surefire-reports/` directory as a supplementary report alongside the Extent Report.
3. WHEN a test fails, THE Screenshot_Utility SHALL capture a full-page screenshot and save it to the `screenshots/` directory with a filename containing the test name and a timestamp.
4. WHEN a test fails, THE Extent_Reporter SHALL embed the failure screenshot path into the corresponding test entry in the HTML report.
5. WHEN a test step is executed, THE Extent_Reporter SHALL log the step description and outcome — pass, fail, or info — to the report.
6. THE Framework SHALL maintain execution logs in a `logs/` directory, with one log file per test run named with the execution timestamp.

---

### Requirement 9: Test Execution Configuration

**User Story:** As a QA engineer, I want to execute tests in Chrome with parallel support and suite filtering, so that I can run targeted and efficient test campaigns.

#### Acceptance Criteria

1. THE Test_Runner SHALL execute all tests using the Chrome browser via Playwright's Chromium channel.
2. THE Test_Runner SHALL support parallel test execution with a configurable thread count defined in the TestNG XML suite file using the `parallel` attribute set to `"methods"` or `"tests"` and the `thread-count` attribute.
3. THE Test_Runner SHALL support execution of the Smoke_Suite by running the `testng-smoke.xml` TestNG_Suite file, which includes only tests annotated with `@Test(groups = {"smoke"})`.
4. THE Test_Runner SHALL support execution of the Regression_Suite by running the `testng-regression.xml` TestNG_Suite file, which includes all tests annotated with `@Test(groups = {"regression"})`.
5. THE Framework SHALL include a default `testng.xml` TestNG_Suite file in the project root that references all test classes and defines the default parallel execution configuration.
6. WHEN a test execution is triggered with a `browser` system property, THE Test_Runner SHALL initialize Playwright for the specified browser channel instead of the default Chromium.

---

### Requirement 10: CI/CD Pipeline Integration

**User Story:** As a DevOps engineer, I want the framework to integrate with Jenkins, so that tests execute automatically on code changes and reports are published to the pipeline.

#### Acceptance Criteria

1. THE Framework SHALL include a `Jenkinsfile` in the project root defining a declarative pipeline with stages for checkout, build, test execution, and report publishing.
2. WHEN the Jenkins pipeline executes the test stage, THE CI_Pipeline SHALL invoke `mvn test -DsuiteXmlFile=testng.xml` with configurable suite file and browser parameters passed as system properties via the Maven Surefire plugin.
3. WHEN test execution completes in the CI_Pipeline, THE CI_Pipeline SHALL archive the contents of the `reports/`, `screenshots/`, and `target/surefire-reports/` directories as build artifacts.
4. WHEN a test stage fails in the CI_Pipeline, THE CI_Pipeline SHALL mark the build as failed and send a notification containing the failure summary.
5. THE Framework SHALL include a `pom.xml` defining all required dependencies — Playwright Java, TestNG (pinned version), ExtentReports, and logging libraries — with pinned version numbers, and SHALL configure the Maven Surefire plugin to reference the active TestNG_Suite XML file via the `<suiteXmlFiles>` configuration element.

---

### Requirement 11: AI-Assisted Engineering with Kiro

**User Story:** As a framework developer, I want to use Kiro's Spec Mode, Steering, and Hooks to accelerate development and enforce standards, so that AI-generated code is consistent and aligned with framework conventions.

#### Acceptance Criteria

1. THE Framework SHALL include Steering_Files in `.kiro/steering/` defining coding standards, naming conventions, POM structure rules, and Playwright Java usage guidelines applied during AI code generation.
2. WHEN a new Page_Object file is created, THE Kiro Hook SHALL trigger a validation action confirming the file follows the POM structure defined in the active Steering_File.
3. WHEN a spec document is updated, THE Kiro Hook SHALL trigger a notification action prompting the developer to review impacted Page_Object and test files.
4. THE Framework SHALL store all Kiro spec documents — requirements, design, and tasks — in `.kiro/specs/ai-ecommerce-automation-framework/`.
5. THE Framework SHALL include at least one Steering_File defining Playwright Java coding standards, including locator strategy preferences, async handling patterns, TestNG annotation conventions, and assertion best practices.

---

### Requirement 12: Test Data Management

**User Story:** As a QA engineer, I want test data to be externalized and managed separately from test logic, so that tests can be run with different data sets without modifying code.

#### Acceptance Criteria

1. THE Test_Data_Provider SHALL read test input values — usernames, passwords, search terms, and product names — from JSON or properties files stored in the `testdata/` directory.
2. WHEN a test requires credentials, THE Test_Data_Provider SHALL supply valid and invalid credential sets as separate named data entries.
3. THE Test_Data_Provider SHALL expose a `getData(String key)` method returning the string value associated with the given key from the active data file.
4. IF a requested data key is not found in the data file, THEN THE Test_Data_Provider SHALL throw a descriptive `TestDataNotFoundException` identifying the missing key.
5. THE Framework SHALL not hardcode any test input values — usernames, passwords, URLs, product names, or search terms — directly in test or Page_Object classes.

---

### Requirement 13: TestNG Configuration and Suite Management

**User Story:** As a QA engineer, I want the framework to be fully configured with TestNG suite files, groups, data providers, listeners, parallel execution, and retry logic, so that I can manage and execute tests flexibly and reliably.

#### Acceptance Criteria

1. THE Framework SHALL include a `testng.xml` TestNG_Suite file in the project root that defines all test classes, sets `parallel="methods"` or `parallel="tests"`, and specifies a `thread-count` of at least 2 for parallel execution.
2. THE Framework SHALL include a `testng-smoke.xml` TestNG_Suite file that includes only test classes or methods belonging to the `smoke` group, enabling targeted Smoke_Suite execution.
3. THE Framework SHALL include a `testng-regression.xml` TestNG_Suite file that includes all test classes belonging to the `regression` group, enabling full Regression_Suite execution.
4. WHEN a test method is defined, THE Framework SHALL annotate it with `@Test(groups = {"smoke"})` for smoke tests or `@Test(groups = {"regression"})` for regression tests, ensuring every test belongs to at least one named group.
5. WHEN a test method requires multiple input combinations, THE Framework SHALL supply test data via a `@DataProvider`-annotated method in the `Test_Data_Provider` class, and the `@Test` method SHALL reference it using the `dataProvider` attribute.
6. THE Framework SHALL implement a `TestNG_Listener` class in `src/test/java/listeners/` that implements `ITestListener` and overrides `onTestFailure`, `onTestSuccess`, and `onTestSkip` callbacks to integrate with the `Extent_Reporter` and `Screenshot_Utility`.
7. THE Framework SHALL register the `TestNG_Listener` in each TestNG_Suite XML file using the `<listeners>` element so that it is active for all suite executions without requiring annotation on individual test classes.
8. THE Framework SHALL implement a `TestNG_RetryAnalyzer` class in `src/test/java/utils/` that implements `IRetryAnalyzer` and retries a failed test up to a configurable maximum of 2 times before marking it as failed.
9. WHEN a `@Test` method is subject to retry, THE Framework SHALL reference the `TestNG_RetryAnalyzer` via the `retryAnalyzer` attribute of the `@Test` annotation on that method.
10. THE Framework SHALL configure the Maven Surefire plugin in `pom.xml` with the `<suiteXmlFiles>` element pointing to the active TestNG_Suite XML file, enabling `mvn test` to execute the correct suite without additional command-line flags.
