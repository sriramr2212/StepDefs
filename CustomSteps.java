package com.novac.naf.steps;

import com.novac.naf.config.ConfigLoader;
import com.novac.naf.orm.ORLoader;
import com.novac.naf.reporting.ReportManager;
import com.novac.naf.steps.CommonSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Shared step definitions for cross-cutting concerns
 */
public class CustomSteps {
    private static final Logger logger = LoggerFactory.getLogger(CustomSteps.class);
    private final CommonSteps commonSteps;
    private final WebDriver driver;
    private final ConfigLoader configLoader;
    
    // Default constructor for Cucumber discovery
    public CustomSteps() {
        this.commonSteps = new CommonSteps();
        this.driver = commonSteps.getDriver();
        
        String excelPath = System.getProperty("excelPath", "./TestData/RunManager.xlsx");
        this.configLoader = new ConfigLoader(excelPath);
        
        logger.info("CustomSteps initialized successfully");
    }
    
    // Keep the existing constructor for backward compatibility
    public CustomSteps(CommonSteps commonSteps) {
        this.commonSteps = commonSteps;
        this.driver = commonSteps.getDriver();
        
        String excelPath = System.getProperty("excelPath", "./TestData/RunManager.xlsx");
        this.configLoader = new ConfigLoader(excelPath);
        
        logger.info("CustomSteps initialized with provided CommonSteps");
    }
    
    // ================================
    // LOGIN/LOGOUT STEP DEFINITIONS
    // ================================
    
    @When("I login to the application")
    public void loginToApplication() {
        logger.info("=== STARTING LOGIN PROCESS ===");
        try {
            logger.info("Starting login process using configuration values");
            
            String environment = configLoader.getEnvironment();
            String url = configLoader.getBaseApplicationUrl();
            String username = configLoader.getConfigValue("App_Username");
            String password = configLoader.getConfigValue("App_Password");
            
            logger.info("Login configuration - Environment: {}, URL: {}, Username: {}", environment, url, username);
            
            try {
                commonSteps.navigateToUrl(url);
                logger.info("Navigated to login URL: {}", url);
            } catch (Throwable t) {
                logger.error("Error navigating to URL: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Navigation failed: " + t.getMessage(), t);
            }
            
            try {
                commonSteps.waitForSeconds("2");
            } catch (Throwable t) {
                logger.error("Error waiting for page load: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Wait failed: " + t.getMessage(), t);
            }
            
            try {
                commonSteps.enterTextInField(username, "customInputUsername", "LoginPage");
                logger.info("Entered username in customInputUsername field");
            } catch (Throwable t) {
                logger.error("Error entering username: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Username entry failed: " + t.getMessage(), t);
            }
            
            try {
                commonSteps.enterTextInField(password, "customInputPassword", "LoginPage");
                logger.info("Entered password in customInputPassword field");
            } catch (Throwable t) {
                logger.error("Error entering password: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Password entry failed: " + t.getMessage(), t);
            }
            
            try {
                commonSteps.clickElement("login-btn", "LoginPage");
                logger.info("Clicked login button");
            } catch (Throwable t) {
                logger.error("Error clicking login button: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Login button click failed: " + t.getMessage(), t);
            }
            
            try {
                commonSteps.waitForSeconds("2");
            } catch (Throwable t) {
                logger.error("Error waiting after login: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Post-login wait failed: " + t.getMessage(), t);
            }
            
            try {
                commonSteps.takeScreenshot("Login Success");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            ReportManager.logPass("Login operation", "Successfully logged in to application");
            logger.info("=== LOGIN PROCESS COMPLETED SUCCESSFULLY ===");
            
        } catch (Exception e) {
            logger.error("=== LOGIN PROCESS FAILED ===");
            logger.error("Error during login process: {}", e.getMessage());
            ReportManager.logFail("Login operation failed", e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }
    
    @When("I logout")
    public void logoutFromApplication() {
        try {
            logger.info("Starting logout process");
            
            // TODO: Add logout implementation once object names are provided
            logger.info("Logout step definition executed - implementation pending object names");
            
            // Take screenshot on success
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("Logout Success");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            ReportManager.logPass("Logout operation", "Logout step executed (implementation pending)");
            
        } catch (Exception e) {
            logger.error("Error during logout process: {}", e.getMessage());
            ReportManager.logFail("Logout operation failed", e.getMessage());
            throw new RuntimeException("Logout failed: " + e.getMessage(), e);
        }
    }
    
    // ================================
    // PAGINATION STEP DEFINITIONS
    // ================================
    
    @Then("the pagination control should be visible and functional")
    public void verifyPaginationControlVisibleAndFunctional() {
        try {
            logger.info("Verifying pagination control visibility and functionality");
            
            WebElement paginationContainer = findPaginationContainer();
            if (paginationContainer == null) {
                ReportManager.logFail("Pagination verification", "Pagination container not found");
                throw new RuntimeException("Pagination container not found on page");
            }
            
            ReportManager.logPass("Pagination container", "Pagination container found and visible");
            
            try {
                List<WebElement> pageButtons = paginationContainer.findElements(By.xpath(".//button[matches(text(),'^[0-9]+$')] | .//a[matches(text(),'^[0-9]+$')]"));
                if (pageButtons.size() > 0) {
                    ReportManager.logPass("Page number buttons", "Found " + pageButtons.size() + " page number buttons");
                } else {
                    ReportManager.logFail("Page number buttons", "No page number buttons found");
                }
            } catch (Exception e) {
                ReportManager.logFail("Page number buttons", "Error finding page buttons: " + e.getMessage());
            }
            
            try {
                WebElement nextButton = paginationContainer.findElement(By.xpath(".//button[contains(text(),'>') or contains(@title,'Next') or contains(@class,'next') or contains(@aria-label,'Next')]"));
                boolean nextEnabled = nextButton.isEnabled() && !nextButton.getAttribute("class").contains("disabled");
                ReportManager.logPass("Next button", "Next button found, enabled: " + nextEnabled);
            } catch (Exception e) {
                ReportManager.logFail("Next button", "Next button not found: " + e.getMessage());
            }
            
            try {
                WebElement prevButton = paginationContainer.findElement(By.xpath(".//button[contains(text(),'<') or contains(@title,'Previous') or contains(@class,'prev') or contains(@aria-label,'Previous')]"));
                boolean prevEnabled = prevButton.isEnabled() && !prevButton.getAttribute("class").contains("disabled");
                ReportManager.logPass("Previous button", "Previous button found, enabled: " + prevEnabled);
            } catch (Exception e) {
                ReportManager.logFail("Previous button", "Previous button not found: " + e.getMessage());
            }
            
            try {
                WebElement activePageIndicator = paginationContainer.findElement(By.xpath(".//button[contains(@class,'active') or contains(@class,'current')] | .//a[contains(@class,'active') or contains(@class,'current')]"));
                String currentPage = getCurrentPageNumber();
                ReportManager.logPass("Active page indicator", "Active page indicator found, current page: " + currentPage);
            } catch (Exception e) {
                ReportManager.logFail("Active page indicator", "Active page indicator not found: " + e.getMessage());
            }
            
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("Pagination Control Verification");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            logger.info("Pagination control verification completed");
            
        } catch (Exception e) {
            logger.error("Error verifying pagination control: {}", e.getMessage());
            ReportManager.logFail("Pagination verification failed", e.getMessage());
            throw new RuntimeException("Pagination verification failed: " + e.getMessage(), e);
        }
    }
    
    @When("I navigate through all pages using the pagination controls")
    public void navigateThroughAllPagesUsingPaginationControls() {
        try {
            logger.info("Starting navigation through all pages using pagination controls");
            
            WebElement paginationContainer = findPaginationContainer();
            if (paginationContainer == null) {
                throw new RuntimeException("Pagination container not found");
            }
            
            int currentPageNum = 1;
            String firstUserId = getFirstUserIdFromTable();
            logger.info("Starting navigation - Page {}, First User ID: {}", currentPageNum, firstUserId);
            
            // Navigate forward through all pages
            while (true) {
                try {
                    WebElement nextButton = paginationContainer.findElement(By.xpath(".//button[contains(text(),'>') or contains(@title,'Next') or contains(@class,'next') or contains(@aria-label,'Next')]"));
                    
                    if (!isNextButtonEnabled()) {
                        logger.info("Next button is disabled, reached last page");
                        break;
                    }
                    
                    nextButton.click();
                    currentPageNum++;
                    
                    waitForTableUpdate();
                    addDemoDelay();
                    
                    String newFirstUserId = getFirstUserIdFromTable();
                    logger.info("Navigated to page {}, First User ID: {}", currentPageNum, newFirstUserId);
                    
                    if (!firstUserId.equals(newFirstUserId)) {
                        ReportManager.logPass("Page navigation", "Successfully navigated to page " + currentPageNum + ", table content updated");
                    } else {
                        ReportManager.logFail("Page navigation", "Table content did not change after navigation to page " + currentPageNum);
                    }
                    
                    firstUserId = newFirstUserId;
                    
                } catch (NoSuchElementException e) {
                    logger.info("Next button not found, assuming last page reached");
                    break;
                }
            }
            
            // Navigate back to first page using Previous button
            logger.info("Navigating back to first page using Previous button");
            while (currentPageNum > 1) {
                try {
                    WebElement prevButton = paginationContainer.findElement(By.xpath(".//button[contains(text(),'<') or contains(@title,'Previous') or contains(@class,'prev') or contains(@aria-label,'Previous')]"));
                    
                    if (!isPreviousButtonEnabled()) {
                        logger.info("Previous button is disabled, reached first page");
                        break;
                    }
                    
                    prevButton.click();
                    currentPageNum--;
                    
                    waitForTableUpdate();
                    addDemoDelay();
                    
                    String newFirstUserId = getFirstUserIdFromTable();
                    logger.info("Navigated back to page {}, First User ID: {}", currentPageNum, newFirstUserId);
                    
                    ReportManager.logPass("Backward navigation", "Successfully navigated back to page " + currentPageNum);
                    
                } catch (NoSuchElementException e) {
                    logger.info("Previous button not found, assuming first page reached");
                    break;
                }
            }
            
            try {
                commonSteps.takeScreenshot("Pagination Navigation Complete");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            ReportManager.logPass("Full pagination navigation", "Successfully navigated through all pages and returned to first page");
            logger.info("Completed navigation through all pages");
            
        } catch (Exception e) {
            logger.error("Error navigating through pages: {}", e.getMessage());
            ReportManager.logFail("Pagination navigation failed", e.getMessage());
            throw new RuntimeException("Pagination navigation failed: " + e.getMessage(), e);
        }
    }
    
    @When("I go to page number {string} using the pagination bar")
    public void goToPageNumberUsingPaginationBar(String pageNumber) {
        try {
            logger.info("Navigating directly to page number: {}", pageNumber);
            
            WebElement paginationContainer = findPaginationContainer();
            if (paginationContainer == null) {
                throw new RuntimeException("Pagination container not found");
            }
            
            String firstUserIdBefore = getFirstUserIdFromTable();
            logger.info("Current first User ID before navigation: {}", firstUserIdBefore);
            
            try {
                WebElement pageButton = paginationContainer.findElement(By.xpath(".//button[contains(text(),'" + pageNumber + "') or @data-page='" + pageNumber + "'] | .//a[contains(text(),'" + pageNumber + "') or @data-page='" + pageNumber + "']"));
                pageButton.click();
                logger.info("Clicked on page number button: {}", pageNumber);
            } catch (NoSuchElementException e) {
                logger.error("Page number button {} not found in pagination bar", pageNumber);
                throw new RuntimeException("Page number button " + pageNumber + " not found in pagination bar", e);
            }
            
            waitForTableUpdate();
            addDemoDelay();
            
            String currentPageNumber = getCurrentPageNumber();
            if (currentPageNumber.equals(pageNumber)) {
                ReportManager.logPass("Direct page navigation", "Successfully navigated to page " + pageNumber);
            } else {
                ReportManager.logFail("Direct page navigation", "Expected to be on page " + pageNumber + " but current page is " + currentPageNumber);
            }
            
            String firstUserIdAfter = getFirstUserIdFromTable();
            logger.info("First User ID after navigation to page {}: {}", pageNumber, firstUserIdAfter);
            
            try {
                commonSteps.takeScreenshot("Direct Page Navigation - Page " + pageNumber);
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            logger.info("Successfully navigated to page: {}", pageNumber);
            
        } catch (Exception e) {
            logger.error("Error navigating to page number {}: {}", pageNumber, e.getMessage());
            ReportManager.logFail("Direct page navigation failed", e.getMessage());
            throw new RuntimeException("Direct page navigation failed: " + e.getMessage(), e);
        }
    }
    
    @When("I select {string} rows per page from dropdown")
    public void selectRowsPerPageFromDropdown(String rowCount) {
        try {
            logger.info("Selecting {} rows per page from dropdown", rowCount);
            
            try {
                WebElement rowsPerPageDropdown = driver.findElement(By.xpath("//select[contains(@id,'rows') or contains(@id,'page')] | //div[contains(@class,'dropdown') and contains(text(),'rows')] | //select[contains(@class,'page-size')]"));
                rowsPerPageDropdown.click();
                logger.info("Clicked rows per page dropdown");
            } catch (NoSuchElementException e) {
                logger.error("Rows per page dropdown not found");
                throw new RuntimeException("Rows per page dropdown not found", e);
            }
            
            try {
                commonSteps.waitForSeconds("1");
            } catch (Throwable t) {
                logger.error("Error waiting for dropdown to open: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Dropdown wait failed: " + t.getMessage(), t);
            }
            
            try {
                WebElement rowCountOption = driver.findElement(By.xpath("//option[contains(text(),'" + rowCount + "')] | //div[contains(@class,'option') and contains(text(),'" + rowCount + "')] | //li[contains(text(),'" + rowCount + "')]"));
                rowCountOption.click();
                logger.info("Selected {} rows per page option", rowCount);
            } catch (NoSuchElementException e) {
                logger.error("Row count option {} not found in dropdown", rowCount);
                throw new RuntimeException("Row count option " + rowCount + " not found in dropdown", e);
            }
            
            try {
                commonSteps.waitForSeconds("3");
            } catch (Throwable t) {
                logger.error("Error waiting for table to update: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Table update wait failed: " + t.getMessage(), t);
            }
            
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("Rows Per Page - " + rowCount);
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            ReportManager.logPass("Rows per page selection", "Successfully selected " + rowCount + " rows per page");
            
        } catch (Exception e) {
            logger.error("Error selecting {} rows per page: {}", rowCount, e.getMessage());
            ReportManager.logFail("Rows per page selection failed", "Failed to select " + rowCount + " rows per page - " + e.getMessage());
            throw new RuntimeException("Rows per page selection failed: " + e.getMessage(), e);
        }
    }
    
    @Then("I should not see {string} on {string} page")
    public void verifyElementNotPresent(String elementName, String pageName) {
        try {
            By locator = ORLoader.getLocator(pageName, elementName);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            
            try {
                boolean elementDisappeared = wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
                
                if (elementDisappeared) {
                    logger.info("Element {} is correctly not visible on {}", elementName, pageName);
                    
                    // Take screenshot on success
                    addDemoDelay();
                    try {
                        commonSteps.takeScreenshot("Element Not Present Verification - " + elementName);
                    } catch (Throwable t) {
                        logger.error("Error taking screenshot: {}", t.getMessage());
                        t.printStackTrace();
                        throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
                    }
                    
                    ReportManager.logPass("Element verification", "Element " + elementName + " correctly not visible on " + pageName);
                } else {
                    logger.error("Element {} is unexpectedly visible on {}", elementName, pageName);
                    
                    // Take screenshot on failure (element found when it shouldn't be)
                    addDemoDelay();
                    try {
                        commonSteps.takeScreenshot("Element Unexpectedly Found - " + elementName);
                    } catch (Throwable t) {
                        logger.error("Error taking screenshot: {}", t.getMessage());
                        t.printStackTrace();
                        throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
                    }
                    
                    ReportManager.logFail("Element verification failed", "Element " + elementName + " should not be visible but it is");
                    throw new AssertionError("Element " + elementName + " should not be visible but it is");
                }
            } catch (TimeoutException e) {
                logger.info("Element {} is correctly not visible on {}", elementName, pageName);
                
                // Take screenshot on success
                addDemoDelay();
                try {
                    commonSteps.takeScreenshot("Element Not Present Verification - " + elementName);
                } catch (Throwable t) {
                    logger.error("Error taking screenshot: {}", t.getMessage());
                    t.printStackTrace();
                    throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
                }
                
                ReportManager.logPass("Element verification", "Element " + elementName + " correctly not visible on " + pageName);
            }
            
        } catch (Exception e) {
            if (!(e instanceof TimeoutException)) {
                logger.error("Error verifying element {} not present on page {}: {}", elementName, pageName, e.getMessage());
                ReportManager.logFail("Element verification failed", e.getMessage());
                throw new RuntimeException("Element verification failed: " + e.getMessage(), e);
            }
        }
    }
    
    // ================================
    // FILE UPLOAD STEP DEFINITIONS
    // ================================
    
    @When("I upload the file {string} into the {string} field")
    public void uploadFileIntoField(String filePath, String label) {
        try {
            logger.info("Uploading file {} into field with label: {}", filePath, label);
            
            // Find the file input element by label
            WebElement fileInput = findFileInputByLabel(label);
            
            if (fileInput == null) {
                throw new RuntimeException("File input field with label '" + label + "' not found");
            }
            
            // Verify file exists
            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("File not found: " + filePath);
            }
            
            // Upload the file
            fileInput.sendKeys(file.getAbsolutePath());
            logger.info("File upload initiated for: {}", filePath);
            
            // Wait for upload to process
            try {
                commonSteps.waitForSeconds("2");
            } catch (Throwable t) {
                logger.error("Error waiting for upload: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Upload wait failed: " + t.getMessage(), t);
            }
            
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("File Upload - " + label);
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            ReportManager.logPass("File upload", "Successfully uploaded file " + filePath + " to field " + label);
            
        } catch (Exception e) {
            logger.error("Error uploading file {} to field {}: {}", filePath, label, e.getMessage());
            ReportManager.logFail("File upload failed", e.getMessage());
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }
    
    @Then("I should see file upload error {string}")
    public void verifyFileUploadError(String expectedMessage) {
        try {
            logger.info("Verifying file upload error message: {}", expectedMessage);
            
            // Wait for error message to appear
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // Try multiple selectors for error messages
            List<String> errorSelectors = java.util.Arrays.asList(
                "//div[contains(@class,'error') and contains(text(),'" + expectedMessage + "')]",
                "//span[contains(@class,'error') and contains(text(),'" + expectedMessage + "')]",
                "//p[contains(@class,'error') and contains(text(),'" + expectedMessage + "')]",
                "//div[contains(@class,'alert') and contains(text(),'" + expectedMessage + "')]",
                "//*[contains(text(),'" + expectedMessage + "')]"
            );
            
            WebElement errorElement = null;
            for (String selector : errorSelectors) {
                try {
                    errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
                    if (errorElement != null) {
                        break;
                    }
                } catch (TimeoutException e) {
                    continue;
                }
            }
            
            if (errorElement != null) {
                String actualMessage = errorElement.getText().trim();
                if (actualMessage.contains(expectedMessage)) {
                    logger.info("File upload error message verified: {}", actualMessage);
                    ReportManager.logPass("File upload error verification", "Error message displayed: " + actualMessage);
                } else {
                    ReportManager.logFail("File upload error verification", "Expected: " + expectedMessage + ", Actual: " + actualMessage);
                    throw new AssertionError("Error message mismatch. Expected: " + expectedMessage + ", Actual: " + actualMessage);
                }
            } else {
                ReportManager.logFail("File upload error verification", "Error message not found: " + expectedMessage);
                throw new RuntimeException("Error message not found: " + expectedMessage);
            }
            
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("File Upload Error");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
        } catch (Exception e) {
            logger.error("Error verifying file upload error message: {}", e.getMessage());
            
            // Take screenshot on failure (error message not found or doesn't match)
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("File Upload Error Verification Failed");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            ReportManager.logFail("File upload error verification failed", e.getMessage());
            throw new RuntimeException("File upload error verification failed: " + e.getMessage(), e);
        }
    }
    
    @When("I set the toggle in the table row where {string} is {string} to {string}")
    public void setToggleInTableRow(String columnName, String value, String toggleState) {
        try {
            logger.info("Setting toggle in table row where {} is {} to {}", columnName, value, toggleState);
            
            // Find the row containing the specified value in the specified column
            WebElement targetRow = findTableRowByColumnValue(columnName, value);
            
            if (targetRow == null) {
                throw new RuntimeException("Table row not found where " + columnName + " is " + value);
            }
            
            // Find the toggle switch in that row
            WebElement toggleElement = findToggleInRow(targetRow);
            
            if (toggleElement == null) {
                throw new RuntimeException("Toggle switch not found in table row");
            }
            
            // Get current toggle state
            boolean currentState = isToggleOn(toggleElement);
            boolean desiredState = toggleState.equalsIgnoreCase("ON");
            
            // Click toggle if state needs to change
            if (currentState != desiredState) {
                toggleElement.click();
                logger.info("Clicked toggle to change state from {} to {}", currentState ? "ON" : "OFF", toggleState);
                
                // Wait for state change
                try {
                    commonSteps.waitForSeconds("1");
                } catch (Throwable t) {
                    logger.error("Error waiting for toggle state change: {}", t.getMessage());
                    t.printStackTrace();
                    throw new RuntimeException("Toggle wait failed: " + t.getMessage(), t);
                }
                
                // Verify state changed
                boolean newState = isToggleOn(toggleElement);
                if (newState == desiredState) {
                    ReportManager.logPass("Table row toggle", "Successfully set toggle to " + toggleState + " for row where " + columnName + " is " + value);
                } else {
                    ReportManager.logFail("Table row toggle", "Failed to set toggle to " + toggleState);
                    throw new RuntimeException("Toggle state did not change as expected");
                }
            } else {
                logger.info("Toggle is already in desired state: {}", toggleState);
                ReportManager.logPass("Table row toggle", "Toggle already in desired state: " + toggleState);
            }
            
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("Table Row Toggle - " + toggleState);
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
        } catch (Exception e) {
            logger.error("Error setting toggle in table row: {}", e.getMessage());
            ReportManager.logFail("Table row toggle failed", e.getMessage());
            throw new RuntimeException("Table row toggle failed: " + e.getMessage(), e);
        }
    }
    
    // ================================
    // MULTI-SELECT DROPDOWN STEP DEFINITIONS
    // Supports single or multiple values separated by commas
    // ================================
    
    @When("I select {string} from {string} dropdown on {string} page")
    public void selectMultipleValuesFromDropdown(String values, String elementName, String pageName) {
        try {
            logger.info("=== STARTING MULTI-SELECT DROPDOWN OPERATION ===");
            logger.info("Selecting values: {} from dropdown: {} on page: {}", values, elementName, pageName);
            
            // Replace test data placeholders
            String processedValues = commonSteps.replaceTestDataPlaceholders(values);
            String processedElementName = commonSteps.replaceTestDataPlaceholders(elementName);
            String processedPageName = commonSteps.replaceTestDataPlaceholders(pageName);
            
            // Parse comma-separated values
            List<String> valuesList = Arrays.asList(processedValues.split(","));
            if (valuesList.isEmpty()) {
                throw new RuntimeException("No values provided for multi-select dropdown");
            }
            
            // Trim whitespace from each value
            valuesList.replaceAll(String::trim);
            logger.info("Parsed values list: {}", valuesList);
            
            // Find the dropdown element using object repository
            WebElement dropdownElement = commonSteps.findElement(processedElementName, processedPageName);
            logger.info("Found dropdown element - Tag: {}, Type: {}", 
                       dropdownElement.getTagName(), dropdownElement.getAttribute("type"));
            
            // Determine dropdown type and handle accordingly
            if ("select".equals(dropdownElement.getTagName().toLowerCase())) {
                handleNativeSelectDropdown(dropdownElement, valuesList);
            } else {
                handleCustomDropdown(dropdownElement, valuesList);
            }
            
            // Take screenshot on success
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("Multi-select Dropdown Success");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            ReportManager.logPass("Multi-select dropdown", "Successfully selected " + valuesList.size() + " values from dropdown: " + processedElementName);
            logger.info("=== MULTI-SELECT DROPDOWN OPERATION COMPLETED SUCCESSFULLY ===");
            
        } catch (Exception e) {
            logger.error("=== MULTI-SELECT DROPDOWN OPERATION FAILED ===");
            logger.error("Error during multi-select dropdown operation: {}", e.getMessage());
            
            // Take screenshot on failure
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("Multi-select Dropdown Failed");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            ReportManager.logFail("Multi-select dropdown failed", e.getMessage());
            throw new RuntimeException("Multi-select dropdown failed: " + e.getMessage(), e);
        }
    }
    
    // ================================
    // TABLE HEADER VERIFICATION STEP DEFINITION    
    // ================================
    
    @Then("I verify values from {string} table appear as headers in {string} before column {string}")
    public void verifyValuesAsHeaders(String sourcePage, String targetPage, String finalColumnLabel) {
        try {
            logger.info("=== STARTING TABLE HEADER VERIFICATION ===");
            logger.info("Source page: {}, Target page: {}, Final column: {}", sourcePage, targetPage, finalColumnLabel);
            
            // Step 1: Read all values from source table first column
            List<WebElement> sourceElements = driver.findElements(ORLoader.getLocator(sourcePage, "firstColumn"));
            List<String> sourceValues = new ArrayList<>();
            
            if (sourceElements.isEmpty()) {
                ReportManager.logFail("Source table reading", "No elements found in first column of " + sourcePage + " table");
                throw new RuntimeException("Source table first column elements not found");
            }
            
            for (WebElement element : sourceElements) {
                String value = element.getText().trim();
                if (!value.isEmpty()) {
                    sourceValues.add(value);
                }
            }
            
            logger.info("Found {} values in source table: {}", sourceValues.size(), sourceValues);
            ReportManager.logPass("Source table reading", "Successfully read " + sourceValues.size() + " values from " + sourcePage);
            
            // Step 2: Navigate to target page if different
            if (!sourcePage.equals(targetPage)) {
                logger.info("Navigating to target page: {}", targetPage);
                // Add small delay for page transition
                try {
                    commonSteps.waitForSeconds("1");
                } catch (Throwable t) {
                    logger.error("Error waiting for page transition: {}", t.getMessage());
                }
            }
            
            // Step 3: Read target table headers
            List<WebElement> headerElements = driver.findElements(ORLoader.getLocator(targetPage, "tableHeaders"));
            List<String> headerValues = new ArrayList<>();
            
            if (headerElements.isEmpty()) {
                ReportManager.logFail("Target table reading", "No header elements found in " + targetPage + " table");
                throw new RuntimeException("Target table header elements not found");
            }
            
            for (WebElement header : headerElements) {
                headerValues.add(header.getText().trim());
            }
            
            logger.info("Found {} headers in target table: {}", headerValues.size(), headerValues);
            ReportManager.logPass("Target table reading", "Successfully read " + headerValues.size() + " headers from " + targetPage);
            
            // Step 4: Find final column index
            int finalColumnIndex = -1;
            for (int i = 0; i < headerValues.size(); i++) {
                if (headerValues.get(i).equals(finalColumnLabel)) {
                    finalColumnIndex = i;
                    break;
                }
            }
            
            if (finalColumnIndex == -1) {
                ReportManager.logFail("Final column location", "Final column '" + finalColumnLabel + "' not found in headers");
                throw new RuntimeException("Final column not found: " + finalColumnLabel);
            }
            
            logger.info("Final column '{}' found at index {}", finalColumnLabel, finalColumnIndex);
            ReportManager.logPass("Final column location", "Final column '" + finalColumnLabel + "' found at position " + finalColumnIndex);
            
            // Step 5: Verify each source value appears before final column
            boolean allValuesFound = true;
            for (String sourceValue : sourceValues) {
                boolean found = false;
                for (int i = 0; i < finalColumnIndex; i++) {
                    if (headerValues.get(i).equals(sourceValue)) {
                        found = true;
                        ReportManager.logPass("Header verification", "Value '" + sourceValue + "' found at position " + i + " (before final column)");
                        logger.info("✓ Value '{}' found at position {} before final column", sourceValue, i);
                        break;
                    }
                }
                
                if (!found) {
                    allValuesFound = false;
                    ReportManager.logFail("Header verification", "Value '" + sourceValue + "' not found before final column '" + finalColumnLabel + "'");
                    logger.error("✗ Value '{}' not found before final column", sourceValue);
                    
                    // Take screenshot on first failure
                    addDemoDelay();
                    try {
                        commonSteps.takeScreenshot("Header Verification Failed - " + sourceValue);
                    } catch (Throwable t) {
                        logger.error("Error taking screenshot: {}", t.getMessage());
                        t.printStackTrace();
                        throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
                    }
                    break;
                }
            }
            
            if (allValuesFound) {
                ReportManager.logPass("Complete header verification", "All " + sourceValues.size() + " values found as headers before final column");
                
                // Take screenshot on success
                addDemoDelay();
                try {
                    commonSteps.takeScreenshot("Header Verification Success");
                } catch (Throwable t) {
                    logger.error("Error taking screenshot: {}", t.getMessage());
                    t.printStackTrace();
                    throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
                }
                
                logger.info("=== TABLE HEADER VERIFICATION COMPLETED SUCCESSFULLY ===");
            } else {
                logger.error("=== TABLE HEADER VERIFICATION FAILED ===");
                throw new RuntimeException("Header verification failed - not all values found before final column");
            }
            
        } catch (Exception e) {
            logger.error("=== TABLE HEADER VERIFICATION FAILED ===");
            logger.error("Error during header verification: {}", e.getMessage());
            ReportManager.logFail("Header verification failed", e.getMessage());
            throw new RuntimeException("Header verification failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Performs inline editing of a record inside a table on a page.
     * 
     * @param fieldName The name of the field to edit
     * @param newValue The new value to set
     * @param rowIdentifierField The field name used to identify the row
     * @param rowIdentifierValue The value to match for row identification
     * @param pageName The name of the page containing the table
     * @throws Throwable If any step fails
     */
    @When("I edit {string} field to {string} in row where {string} is {string} on {string} page")
    public void editFieldInTableRow(String fieldName, String newValue, String rowIdentifierField, String rowIdentifierValue, String pageName) throws Throwable {
        try {
            // Resolve all parameters using test data placeholders
            String resolvedFieldName = commonSteps.replaceTestDataPlaceholders(fieldName);
            String resolvedNewValue = commonSteps.replaceTestDataPlaceholders(newValue);
            String resolvedRowIdentifierField = commonSteps.replaceTestDataPlaceholders(rowIdentifierField);
            String resolvedRowIdentifierValue = commonSteps.replaceTestDataPlaceholders(rowIdentifierValue);
            String resolvedPageName = commonSteps.replaceTestDataPlaceholders(pageName);
            
            logger.info("Starting inline table editing - Field: '{}', New Value: '{}', Row Identifier: '{}' = '{}', Page: '{}'", 
                       resolvedFieldName, resolvedNewValue, resolvedRowIdentifierField, resolvedRowIdentifierValue, resolvedPageName);
            
            // Step 1: Locate the target row
            WebElement targetRow = findTableRowByColumnValue(resolvedRowIdentifierField, resolvedRowIdentifierValue);
            if (targetRow == null) {
                String errorMessage = String.format("Row with %s='%s' not found on page '%s'", 
                                                   resolvedRowIdentifierField, resolvedRowIdentifierValue, resolvedPageName);
                logger.error(errorMessage);
                ReportManager.logFail("Row Location", errorMessage);
                commonSteps.takeScreenshot("Row Not Found - " + resolvedRowIdentifierField);
                throw new AssertionError(errorMessage);
            }
            
            logger.info("Target row located successfully");
            ReportManager.logPass("Row Location", "Found row where " + resolvedRowIdentifierField + " = " + resolvedRowIdentifierValue);
            
            // Step 2: Check if row is already in edit mode
            boolean isInEditMode = checkIfRowInEditMode(targetRow);
            
            if (isInEditMode) {
                logger.info("Row is already in edit mode, proceeding to field update");
                ReportManager.logPass("Edit Mode Check", "Row is already in edit mode");
            } else {
                logger.info("Row is not in edit mode, attempting to enter edit mode");
                
                // Step 3: Enter edit mode by clicking Edit icon
                WebElement editIcon = findEditIcon(targetRow);
                if (editIcon == null || !editIcon.isDisplayed() || !editIcon.isEnabled()) {
                    String errorMessage = "Edit icon not found, not visible, or not enabled for the target row";
                    logger.error(errorMessage);
                    ReportManager.logFail("Edit Icon", errorMessage);
                    commonSteps.takeScreenshot("Edit Icon Not Available - " + resolvedRowIdentifierField);
                    throw new AssertionError(errorMessage);
                }
                
                editIcon.click();
                logger.info("Clicked Edit icon successfully");
                ReportManager.logPass("Edit Mode Entry", "Successfully clicked Edit icon to enter edit mode");
                
                // Wait for edit mode to activate
                Thread.sleep(1000);
                
                // Verify edit mode is now active
                if (!checkIfRowInEditMode(targetRow)) {
                    String errorMessage = "Failed to enter edit mode after clicking Edit icon";
                    logger.error(errorMessage);
                    ReportManager.logFail("Edit Mode Entry", errorMessage);
                    commonSteps.takeScreenshot("Edit Mode Entry Failed - " + resolvedRowIdentifierField);
                    throw new AssertionError(errorMessage);
                }
            }
            
            // Step 4: Update the field value
            updateInlineField(targetRow, resolvedFieldName, resolvedNewValue, resolvedPageName);
            
            // Step 5: Save changes
            saveInlineChanges(targetRow);
            
            // Step 6: Verify the updated value
            verifyUpdatedValue(targetRow, resolvedFieldName, resolvedNewValue, resolvedPageName);
            
            logger.info("Inline table editing completed successfully");
            ReportManager.logPass("Inline Table Editing", 
                                 String.format("Successfully edited field '%s' to '%s' in row where %s='%s'", 
                                             resolvedFieldName, resolvedNewValue, resolvedRowIdentifierField, resolvedRowIdentifierValue));
            
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("Error during inline table editing: %s", e.getMessage());
            logger.error(errorMessage, e);
            ReportManager.logFail("Inline Table Editing", errorMessage);
            commonSteps.takeScreenshot("Inline Editing Error - " + fieldName);
            throw new AssertionError(errorMessage, e);
        }
    }
    
    /**
     * Verifies date-picker field enforces specified date rules.
     * 
     * @param pageName The page where the picker lives
     * @param pickerElement The element name or locator key for the date-picker field
     * @param rule Either "pastOnly" or "futureOnly"
     * @throws Throwable If verification fails
     */
    @Then("I verify date-picker {string} on {string} page for field {string} enforces date rules")
    public void verifyDatePickerEnforcesRules(String pageName, String pickerElement, String rule) throws Throwable {
        try {
            // Resolve test data placeholders
            String resolvedPageName = commonSteps.replaceTestDataPlaceholders(pageName);
            String resolvedPickerElement = commonSteps.replaceTestDataPlaceholders(pickerElement);
            String resolvedRule = commonSteps.replaceTestDataPlaceholders(rule);
            
            logger.info("Verifying date-picker '{}' on page '{}' enforces '{}' rule", 
                       resolvedPickerElement, resolvedPageName, resolvedRule);
            
            // Validate rule parameter
            if (!"pastOnly".equals(resolvedRule) && !"futureOnly".equals(resolvedRule)) {
                String errorMessage = String.format("Invalid rule '%s'. Expected 'pastOnly' or 'futureOnly'", resolvedRule);
                logger.error(errorMessage);
                ReportManager.logFail("Date-picker Rule Validation", errorMessage);
                throw new AssertionError(errorMessage);
            }
            
            // Find the date-picker element
            WebElement pickerElementObj = commonSteps.findElement(resolvedPickerElement, resolvedPageName);
            if (pickerElementObj == null || !pickerElementObj.isDisplayed()) {
                String errorMessage = String.format("Date-picker element '%s' not found or not visible on page '%s'", 
                                                   resolvedPickerElement, resolvedPageName);
                logger.error(errorMessage);
                ReportManager.logFail("Date-picker Element Location", errorMessage);
                commonSteps.takeScreenshot("Date-picker Not Found - " + resolvedPickerElement);
                throw new AssertionError(errorMessage);
            }
            
            // Open the date-picker
            if (!openDatePicker(pickerElementObj)) {
                String errorMessage = String.format("Failed to open date-picker '%s' on page '%s'", 
                                                   resolvedPickerElement, resolvedPageName);
                logger.error(errorMessage);
                ReportManager.logFail("Date-picker Opening", errorMessage);
                commonSteps.takeScreenshot("Date-picker Opening Failed - " + resolvedPickerElement);
                throw new AssertionError(errorMessage);
            }
            
            ReportManager.logPass("Date-picker Opening", "Successfully opened date-picker");
            
            // Find the calendar widget
            WebElement calendarWidget = null;
            List<WebElement> calendars = commonSteps.getDriver().findElements(By.xpath(
                "//div[contains(@class, 'calendar') or contains(@class, 'datepicker') or contains(@class, 'date-picker')]"
            ));
            
            for (WebElement calendar : calendars) {
                if (calendar.isDisplayed()) {
                    calendarWidget = calendar;
                    break;
                }
            }
            
            if (calendarWidget == null) {
                String errorMessage = "Calendar widget not found after opening date-picker";
                logger.error(errorMessage);
                ReportManager.logFail("Calendar Widget Location", errorMessage);
                commonSteps.takeScreenshot("Calendar Widget Not Found - " + resolvedPickerElement);
                throw new AssertionError(errorMessage);
            }
            
            // Validate date-picker rules
            boolean rulesValid = validateDatePickerRules(resolvedRule, calendarWidget);
            
            if (rulesValid) {
                String successMessage = String.format("Date-picker '%s' correctly enforces '%s' rule on page '%s'", 
                                                     resolvedPickerElement, resolvedRule, resolvedPageName);
                ReportManager.logPass("Date-picker Rule Validation", successMessage);
                logger.info("✓ Date-picker rule validation successful: {}", successMessage);
            } else {
                String failureMessage = String.format("Date-picker '%s' does not properly enforce '%s' rule on page '%s'", 
                                                     resolvedPickerElement, resolvedRule, resolvedPageName);
                ReportManager.logFail("Date-picker Rule Validation", failureMessage);
                logger.error("✗ Date-picker rule validation failed: {}", failureMessage);
                commonSteps.takeScreenshot("Date-picker Rule Validation Failed - " + resolvedPickerElement);
                throw new AssertionError(failureMessage);
            }
            
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("Error verifying date-picker rules: %s", e.getMessage());
            logger.error(errorMessage, e);
            ReportManager.logFail("Date-picker Rule Validation", errorMessage);
            commonSteps.takeScreenshot("Date-picker Rule Validation Error - " + pickerElement);
            throw new AssertionError(errorMessage, e);
        }
    }
    
    /**
     * Verifies that a row with specified identifier has been deleted from a table.
     * 
     * @param columnName The header name of the unique-identifier column
     * @param identifierValue The value to search for and confirm deletion
     * @param tableName The table's locator key
     * @param pageName The page where the table lives
     * @throws Throwable If verification fails
     */
    @Then("I verify row with {string} = {string} is deleted from {string} table on {string} page")
    public void verifyRowIsDeletedFromTable(String columnName, String identifierValue, String tableName, String pageName) throws Throwable {
        try {
            // Resolve test data placeholders
            String resolvedColumnName = commonSteps.replaceTestDataPlaceholders(columnName);
            String resolvedIdentifierValue = commonSteps.replaceTestDataPlaceholders(identifierValue);
            String resolvedTableName = commonSteps.replaceTestDataPlaceholders(tableName);
            String resolvedPageName = commonSteps.replaceTestDataPlaceholders(pageName);
            
            logger.info("Verifying row with {}='{}' is deleted from table '{}' on page '{}'", 
                       resolvedColumnName, resolvedIdentifierValue, resolvedTableName, resolvedPageName);
            
            // Find the table
            WebElement table = commonSteps.findElement(resolvedTableName, resolvedPageName);
            if (table == null || !table.isDisplayed()) {
                String errorMessage = String.format("Table '%s' not found or not visible on page '%s'", 
                                                   resolvedTableName, resolvedPageName);
                logger.error(errorMessage);
                ReportManager.logFail("Table Location", errorMessage);
                commonSteps.takeScreenshot("Table Not Found - " + resolvedTableName);
                throw new AssertionError(errorMessage);
            }
            
            ReportManager.logPass("Table Location", "Successfully located table: " + resolvedTableName);
            
            // Search all pages for the identifier
            int pageCount = 0;
            boolean identifierFound = false;
            
            do {
                pageCount++;
                logger.debug("Searching page {} for identifier '{}'", pageCount, resolvedIdentifierValue);
                
                // Search current page
                boolean foundOnCurrentPage = searchCurrentTablePage(resolvedColumnName, resolvedIdentifierValue, table);
                
                if (foundOnCurrentPage) {
                    identifierFound = true;
                    String errorMessage = String.format("Row with %s='%s' was found on page %d - deletion verification failed", 
                                                       resolvedColumnName, resolvedIdentifierValue, pageCount);
                    logger.error(errorMessage);
                    ReportManager.logFail("Row Deletion Verification", errorMessage);
                    commonSteps.takeScreenshot("Row Still Exists - " + resolvedIdentifierValue);
                    throw new AssertionError(errorMessage);
                }
                
                ReportManager.logPass("Page Search", String.format("Page %d: Row with %s='%s' not found (as expected)", 
                                                                  pageCount, resolvedColumnName, resolvedIdentifierValue));
                
                // Check if there's a next page
                if (hasNextPage(table)) {
                    if (!goToNextPage(table)) {
                        logger.warn("Failed to navigate to next page, stopping search");
                        break;
                    }
                } else {
                    break;
                }
                
            } while (true);
            
            if (!identifierFound) {
                String successMessage = String.format("Row deletion verification successful: Row with %s='%s' not found on any of %d pages in table '%s'", 
                                                     resolvedColumnName, resolvedIdentifierValue, pageCount, resolvedTableName);
                ReportManager.logPass("Row Deletion Verification", successMessage);
                logger.info("✓ Row deletion verification successful: {}", successMessage);
            }
            
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("Error verifying row deletion: %s", e.getMessage());
            logger.error(errorMessage, e);
            ReportManager.logFail("Row Deletion Verification", errorMessage);
            commonSteps.takeScreenshot("Row Deletion Verification Error - " + identifierValue);
            throw new AssertionError(errorMessage, e);
        }
    }
    
    // ================================
    // HELPER METHODS
    // ================================
    
    /**
     * Validates date-picker rules (pastOnly or futureOnly).
     * 
     * @param rule The rule to validate ("pastOnly" or "futureOnly")
     * @param calendarWidget The calendar widget element
     * @return True if validation passes, false otherwise
     */
    private boolean validateDatePickerRules(String rule, WebElement calendarWidget) {
        try {
            LocalDate today = LocalDate.now();
            List<WebElement> dateCells = getVisibleCalendarDates(calendarWidget);
            
            boolean allValidationsPass = true;
            int validatedCells = 0;
            
            for (WebElement dateCell : dateCells) {
                try {
                    String dateText = dateCell.getText().trim();
                    if (dateText.isEmpty() || !dateText.matches("\\d+")) {
                        continue; // Skip non-date cells
                    }
                    
                    // Parse the date (this is simplified - in real implementation you'd need to handle month/year context)
                    int dayOfMonth = Integer.parseInt(dateText);
                    LocalDate cellDate = today.withDayOfMonth(dayOfMonth);
                    
                    boolean isEnabled = isDateCellEnabled(dateCell);
                    boolean shouldBeEnabled = false;
                    
                    if ("pastOnly".equals(rule)) {
                        shouldBeEnabled = !cellDate.isAfter(today);
                    } else if ("futureOnly".equals(rule)) {
                        shouldBeEnabled = !cellDate.isBefore(today);
                    }
                    
                    if (isEnabled != shouldBeEnabled) {
                        logger.error("Date validation failed for {}: Expected enabled={}, Actual enabled={}", 
                                   cellDate, shouldBeEnabled, isEnabled);
                        allValidationsPass = false;
                    }
                    
                    validatedCells++;
                } catch (Exception e) {
                    logger.debug("Error validating date cell: {}", e.getMessage());
                }
            }
            
            logger.info("Validated {} date cells for rule '{}'", validatedCells, rule);
            return allValidationsPass;
            
        } catch (Exception e) {
            logger.error("Error validating date-picker rules: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets all visible calendar date cells.
     * 
     * @param calendarWidget The calendar widget element
     * @return List of date cell elements
     */
    private List<WebElement> getVisibleCalendarDates(WebElement calendarWidget) {
        try {
            // Look for common date cell patterns
            List<WebElement> dateCells = calendarWidget.findElements(By.xpath(
                ".//td[contains(@class, 'day') or contains(@class, 'date')] | " +
                ".//button[contains(@class, 'day') or contains(@class, 'date')] | " +
                ".//span[contains(@class, 'day') or contains(@class, 'date')]"
            ));
            
            if (dateCells.isEmpty()) {
                // Fallback to any clickable elements in calendar
                dateCells = calendarWidget.findElements(By.xpath(".//td | .//button"));
            }
            
            return dateCells;
        } catch (Exception e) {
            logger.error("Error getting calendar dates: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Checks if a date cell is enabled.
     * 
     * @param dateCell The date cell element
     * @return True if enabled, false if disabled
     */
    private boolean isDateCellEnabled(WebElement dateCell) {
        try {
            // Check if element is enabled
            if (!dateCell.isEnabled()) {
                return false;
            }
            
            // Check for disabled CSS classes
            String classes = dateCell.getAttribute("class");
            if (classes != null && classes.contains("disabled")) {
                return false;
            }
            
            // Check for disabled attribute
            String disabled = dateCell.getAttribute("disabled");
            if (disabled != null && !disabled.equals("false")) {
                return false;
            }
            
            // Check cursor style for no-symbol
            String cursor = dateCell.getCssValue("cursor");
            if ("not-allowed".equals(cursor) || "no-drop".equals(cursor)) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            logger.debug("Error checking date cell enabled state: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Finds a table row by searching for a specific column value.
     * This helper method is used by both inline editing and deletion verification.
     * 
     * @param columnName The name of the column to search in
     * @param columnValue The value to search for
     * @param pageName The page containing the table
     * @return The WebElement representing the table row, or null if not found
     */
    private WebElement findTableRowByColumnValue(String columnName, String columnValue, String pageName) {
        try {
            // Find all table rows
            List<WebElement> rows = commonSteps.getDriver().findElements(By.xpath("//table//tbody//tr"));
            
            if (rows.isEmpty()) {
                logger.warn("No table rows found on page {}", pageName);
                return null;
            }
            
            // Find the column index by header name
            int columnIndex = findColumnIndex(columnName, null);
            if (columnIndex == -1) {
                logger.error("Column '{}' not found in table headers", columnName);
                return null;
            }
            
            // Search through rows for the matching value
            for (WebElement row : rows) {
                try {
                    List<WebElement> cells = row.findElements(By.xpath(".//td"));
                    if (cells.size() > columnIndex) {
                        String cellText = cells.get(columnIndex).getText().trim();
                        if (columnValue.equals(cellText)) {
                            logger.debug("Found row with {}='{}' in column index {}", columnName, columnValue, columnIndex);
                            return row;
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Error processing row: {}", e.getMessage());
                }
            }
            
            logger.debug("Row with {}='{}' not found on current page", columnName, columnValue);
            return null;
            
        } catch (Exception e) {
            logger.error("Error finding table row: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Finds the column index by header name.
     * 
     * @param columnName The name of the column header
     * @param table The table element (can be null to search all tables)
     * @return The column index (0-based), or -1 if not found
     */
    private int findColumnIndex(String columnName, WebElement table) {
        try {
            String headerXPath = table != null ? 
                ".//thead//tr//th" : 
                "//table//thead//tr//th";
            
            List<WebElement> headers = table != null ? 
                table.findElements(By.xpath(headerXPath)) : 
                	commonSteps.getDriver().findElements(By.xpath(headerXPath));
            
            for (int i = 0; i < headers.size(); i++) {
                String headerText = headers.get(i).getText().trim();
                if (columnName.equals(headerText)) {
                    return i;
                }
            }
            
            return -1;
        } catch (Exception e) {
            logger.error("Error finding column index: {}", e.getMessage());
            return -1;
        }
    }
    
    /**
     * Checks if there is a next page in the table pagination.
     * 
     * @param table The table element
     * @return True if there is a next page, false otherwise
     */
    private boolean hasNextPage(WebElement table) {
        try {
            // Look for common next page indicators
            List<WebElement> nextButtons = commonSteps.getDriver().findElements(By.xpath(
                "//button[contains(@class, 'next') or contains(@title, 'Next') or contains(text(), 'Next')] | " +
                "//a[contains(@class, 'next') or contains(@title, 'Next') or contains(text(), 'Next')] | " +
                "//li[contains(@class, 'next')]/a | " +
                "//span[contains(@class, 'next')]"
            ));
            
            for (WebElement nextButton : nextButtons) {
                if (nextButton.isDisplayed() && nextButton.isEnabled()) {
                    String classes = nextButton.getAttribute("class");
                    if (classes != null && !classes.contains("disabled")) {
                        return true;
                    }
                }
            }
            
            return false;
        } catch (Exception e) {
            logger.debug("Error checking for next page: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Navigates to the next page in table pagination.
     * 
     * @param table The table element
     * @return True if successfully navigated to next page, false otherwise
     */
    private boolean goToNextPage(WebElement table) {
        try {
            // Find and click the next page button
            List<WebElement> nextButtons = commonSteps.getDriver().findElements(By.xpath(
                "//button[contains(@class, 'next') or contains(@title, 'Next') or contains(text(), 'Next')] | " +
                "//a[contains(@class, 'next') or contains(@title, 'Next') or contains(text(), 'Next')] | " +
                "//li[contains(@class, 'next')]/a"
            ));
            
            for (WebElement nextButton : nextButtons) {
                if (nextButton.isDisplayed() && nextButton.isEnabled()) {
                    String classes = nextButton.getAttribute("class");
                    if (classes == null || !classes.contains("disabled")) {
                        nextButton.click();
                        Thread.sleep(1000); // Wait for page to load
                        return true;
                    }
                }
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error navigating to next page: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Searches the current table page for a specific identifier value.
     * 
     * @param columnName The name of the column to search in
     * @param identifierValue The value to search for
     * @param table The table element
     * @return True if the identifier is found, false otherwise
     */
    private boolean searchCurrentTablePage(String columnName, String identifierValue, WebElement table) {
        try {
            WebElement row = findTableRowByColumnValue(columnName, identifierValue, "current");
            return row != null;
        } catch (Exception e) {
            logger.error("Error searching current table page: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Opens a date-picker if it's not already open.
     * 
     * @param pickerElement The date-picker element
     * @return True if successfully opened, false otherwise
     */
    private boolean openDatePicker(WebElement pickerElement) {
        try {
            // Check if calendar is already open
            List<WebElement> openCalendars = commonSteps.getDriver().findElements(By.xpath(
                "//div[contains(@class, 'calendar') or contains(@class, 'datepicker') or contains(@class, 'date-picker')]"
            ));
            
            boolean calendarOpen = false;
            for (WebElement calendar : openCalendars) {
                if (calendar.isDisplayed()) {
                    calendarOpen = true;
                    break;
                }
            }
            
            if (!calendarOpen) {
                // Click to open the date-picker
                pickerElement.click();
                Thread.sleep(500); // Wait for calendar to appear
                
                // Verify calendar opened
                openCalendars = commonSteps.getDriver().findElements(By.xpath(
                    "//div[contains(@class, 'calendar') or contains(@class, 'datepicker') or contains(@class, 'date-picker')]"
                ));
                
                for (WebElement calendar : openCalendars) {
                    if (calendar.isDisplayed()) {
                        return true;
                    }
                }
                
                return false;
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Error opening date-picker: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if a table row is currently in edit mode
     * 
     * @param row The table row element
     * @return True if row is in edit mode, false otherwise
     */
    private boolean checkIfRowInEditMode(WebElement row) {
        try {
            // Check for Save icon visibility
            List<WebElement> saveIcons = row.findElements(By.xpath(".//button[@title='Save'] | .//i[contains(@class,'save')] | .//button[contains(@class,'save')]"));
            boolean saveIconVisible = false;
            for (WebElement saveIcon : saveIcons) {
                if (saveIcon.isDisplayed()) {
                    saveIconVisible = true;
                    break;
                }
            }
            
            // Check for Edit icon visibility
            List<WebElement> editIcons = row.findElements(By.xpath(".//button[@title='Edit'] | .//i[contains(@class,'edit')] | .//button[contains(@class,'edit')]"));
            boolean editIconVisible = false;
            for (WebElement editIcon : editIcons) {
                if (editIcon.isDisplayed()) {
                    editIconVisible = true;
                    break;
                }
            }
            
            // Row is in edit mode if Save icon is visible and Edit icon is not visible
            boolean inEditMode = saveIconVisible && !editIconVisible;
            logger.debug("Edit mode check - Save icon visible: {}, Edit icon visible: {}, In edit mode: {}", 
                        saveIconVisible, editIconVisible, inEditMode);
            
            return inEditMode;
            
        } catch (Exception e) {
            logger.warn("Error checking edit mode status: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Finds the Edit icon in a table row
     * 
     * @param row The table row element
     * @return The Edit icon element, or null if not found
     */
    private WebElement findEditIcon(WebElement row) {
        try {
            List<WebElement> editIcons = row.findElements(By.xpath(".//button[@title='Edit'] | .//i[contains(@class,'edit')] | .//button[contains(@class,'edit')]"));
            for (WebElement editIcon : editIcons) {
                if (editIcon.isDisplayed()) {
                    return editIcon;
                }
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error finding Edit icon: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Updates an inline field in a table row
     * 
     * @param row The table row element
     * @param fieldName The name of the field to update
     * @param newValue The new value to set
     * @param pageName The page name for object repository lookup
     * @throws Throwable 
     */
    private void updateInlineField(WebElement row, String fieldName, String newValue, String pageName) throws Throwable {
        try {
            // Try to find the field within the row context first
            WebElement fieldElement = null;
            try {
                By fieldLocator = ORLoader.getLocator(pageName, fieldName);
                List<WebElement> fieldsInRow = row.findElements(fieldLocator);
                if (!fieldsInRow.isEmpty()) {
                    fieldElement = fieldsInRow.get(0);
                }
            } catch (Exception e) {
                logger.debug("Could not find field using OR locator within row context: {}", e.getMessage());
            }
            
            // If not found in row context, try general page context
            if (fieldElement == null) {
                fieldElement = commonSteps.findElement(fieldName, pageName);
            }
            
            if (fieldElement == null || !fieldElement.isDisplayed()) {
                String errorMessage = String.format("Field '%s' not found or not visible in edit mode", fieldName);
                throw new RuntimeException(errorMessage);
            }
            
            // Check if field is editable
            if (!fieldElement.isEnabled()) {
                String errorMessage = String.format("Field '%s' is not editable", fieldName);
                throw new RuntimeException(errorMessage);
            }
            
            String tagName = fieldElement.getTagName().toLowerCase();
            String fieldType = fieldElement.getAttribute("type");
            
            logger.debug("Updating field - Tag: {}, Type: {}, Value: {}", tagName, fieldType, newValue);
            
            if ("select".equals(tagName)) {
                // Handle dropdown
                Select dropdown = new Select(fieldElement);
                dropdown.selectByVisibleText(newValue);
                logger.info("Selected '{}' from dropdown field '{}'", newValue, fieldName);
                
            } else if ("input".equals(tagName)) {
                if ("checkbox".equals(fieldType) || "radio".equals(fieldType)) {
                    // Handle checkbox/radio toggle
                    boolean shouldBeChecked = "true".equalsIgnoreCase(newValue) || "yes".equalsIgnoreCase(newValue) || "1".equals(newValue);
                    if (fieldElement.isSelected() != shouldBeChecked) {
                        fieldElement.click();
                    }
                    logger.info("Set toggle field '{}' to '{}'", fieldName, shouldBeChecked);
                } else {
                    // Handle text input
                    fieldElement.clear();
                    fieldElement.sendKeys(newValue);
                    logger.info("Entered '{}' into input field '{}'", newValue, fieldName);
                }
                
            } else if ("textarea".equals(tagName)) {
                // Handle textarea
                fieldElement.clear();
                fieldElement.sendKeys(newValue);
                logger.info("Entered '{}' into textarea field '{}'", newValue, fieldName);
                
            } else {
                // Handle other elements (might be custom components)
                fieldElement.clear();
                fieldElement.sendKeys(newValue);
                logger.info("Entered '{}' into field '{}' (type: {})", newValue, fieldName, tagName);
            }
            
            ReportManager.logPass("Field Update", String.format("Successfully updated field '%s' to '%s'", fieldName, newValue));
            
        } catch (Exception e) {
            String errorMessage = String.format("Failed to update field '%s': %s", fieldName, e.getMessage());
            logger.error(errorMessage, e);
            ReportManager.logFail("Field Update", errorMessage);
            commonSteps.takeScreenshot("Field Update Failed - " + fieldName);
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    /**
     * Saves inline changes by clicking the Save icon
     * 
     * @param row The table row element
     * @throws Throwable 
     */
    private void saveInlineChanges(WebElement row) throws Throwable {
        try {
            WebElement saveIcon = findSaveIcon(row);
            if (saveIcon == null || !saveIcon.isDisplayed() || !saveIcon.isEnabled()) {
                String errorMessage = "Save icon not found, not visible, or not enabled";
                throw new RuntimeException(errorMessage);
            }
            
            saveIcon.click();
            logger.info("Clicked Save icon successfully");
            
            // Wait for save operation to complete
            Thread.sleep(1500);
            
            // Verify we're out of edit mode (Save icon should be hidden, Edit icon should be visible)
            boolean stillInEditMode = checkIfRowInEditMode(row);
            if (stillInEditMode) {
                logger.warn("Row still appears to be in edit mode after save operation");
            }
            
            ReportManager.logPass("Save Changes", "Successfully saved inline changes");
            
        } catch (Exception e) {
            String errorMessage = String.format("Failed to save changes: %s", e.getMessage());
            logger.error(errorMessage, e);
            ReportManager.logFail("Save Changes", errorMessage);
            commonSteps.takeScreenshot("Save Changes Failed");
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    /**
     * Finds the Save icon in a table row
     * 
     * @param row The table row element
     * @return The Save icon element, or null if not found
     */
    private WebElement findSaveIcon(WebElement row) {
        try {
            List<WebElement> saveIcons = row.findElements(By.xpath(".//button[@title='Save'] | .//i[contains(@class,'save')] | .//button[contains(@class,'save')]"));
            for (WebElement saveIcon : saveIcons) {
                if (saveIcon.isDisplayed()) {
                    return saveIcon;
                }
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error finding Save icon: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifies the updated value is displayed correctly after saving
     * 
     * @param row The table row element
     * @param fieldName The name of the field to verify
     * @param expectedValue The expected value
     * @param pageName The page name for object repository lookup
     * @throws Throwable 
     */
    private void verifyUpdatedValue(WebElement row, String fieldName, String expectedValue, String pageName) throws Throwable {
        try {
            // Wait a moment for the UI to update
            Thread.sleep(1000);
            
            // Re-locate the field to get the updated value
            WebElement fieldElement = null;
            try {
                By fieldLocator = ORLoader.getLocator(pageName, fieldName);
                List<WebElement> fieldsInRow = row.findElements(fieldLocator);
                if (!fieldsInRow.isEmpty()) {
                    fieldElement = fieldsInRow.get(0);
                }
            } catch (Exception e) {
                logger.debug("Could not find field using OR locator within row context for verification: {}", e.getMessage());
            }
            
            if (fieldElement == null) {
                fieldElement = commonSteps.findElement(fieldName, pageName);
            }
            
            if (fieldElement == null) {
                String errorMessage = String.format("Field '%s' not found for verification", fieldName);
                throw new RuntimeException(errorMessage);
            }
            
            String actualValue = getFieldValue(fieldElement);
            
            logger.debug("Verification - Expected: '{}', Actual: '{}'", expectedValue, actualValue);
            
            if (expectedValue.equals(actualValue)) {
                logger.info("Verification successful: Field '{}' has correct value '{}'", fieldName, actualValue);
                ReportManager.logPass("Value Verification", 
                                     String.format("Field '%s' correctly displays updated value '%s'", fieldName, actualValue));
            } else {
                String errorMessage = String.format("Value verification failed for field '%s': Expected '%s', Actual '%s'", 
                                                   fieldName, expectedValue, actualValue);
                logger.error(errorMessage);
                ReportManager.logFail("Value Verification", errorMessage);
                commonSteps.takeScreenshot("Value Verification Failed - " + fieldName);
                throw new AssertionError(errorMessage);
            }
            
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("Error during value verification: %s", e.getMessage());
            logger.error(errorMessage, e);
            ReportManager.logFail("Value Verification", errorMessage);
            commonSteps.takeScreenshot("Value Verification Error - " + fieldName);
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    /**
     * Gets the current value of a field element
     * 
     * @param element The field element
     * @return The current value
     */
    private String getFieldValue(WebElement element) {
        String tagName = element.getTagName().toLowerCase();
        String fieldType = element.getAttribute("type");
        
        if ("select".equals(tagName)) {
            Select select = new Select(element);
            try {
                return select.getFirstSelectedOption().getText().trim();
            } catch (NoSuchElementException e) {
                return "";
            }
        } else if ("input".equals(tagName)) {
            if ("checkbox".equals(fieldType) || "radio".equals(fieldType)) {
                return element.isSelected() ? "true" : "false";
            } else {
                String value = element.getAttribute("value");
                return value != null ? value : "";
            }
        } else if ("textarea".equals(tagName)) {
            String value = element.getAttribute("value");
            return value != null ? value : "";
        } else {
            // For other elements, try text content first, then value attribute
            String text = element.getText().trim();
            if (!text.isEmpty()) {
                return text;
            }
            String value = element.getAttribute("value");
            return value != null ? value : "";
        }
    }
    
    /**
     * Add a demo delay after test step execution
     */
    public void addDemoDelay() {
        try {
            Thread.sleep(1500);
            logger.debug("Demo delay: Added 1.5-second wait after step execution");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.debug("Demo delay interrupted");
        }
    }
    
    /**
     * Handle native HTML select dropdown with multiple attribute
     */
    private void handleNativeSelectDropdown(WebElement selectElement, List<String> values) {
        try {
            Select select = new Select(selectElement);
            
            // Check if multiple selection is supported
            if (!select.isMultiple()) {
                logger.warn("Select element does not support multiple selection, selecting last value only");
            }
            
            for (String value : values) {
                try {
                    logger.info("Attempting to select native option: {}", value);
                    
                    // Try selecting by visible text first, then by value
                    try {
                        select.selectByVisibleText(value);
                        logger.info("Selected by visible text: {}", value);
                    } catch (NoSuchElementException e) {
                        select.selectByValue(value);
                        logger.info("Selected by value attribute: {}", value);
                    }
                    
                    ReportManager.logPass("Multi-select value", "Successfully selected: " + value);
                    Thread.sleep(300); // Small delay between selections
                    
                } catch (Exception e) {
                    logger.error("Failed to select native option: {} - {}", value, e.getMessage());
                    ReportManager.logFail("Multi-select value failed", "Failed to select: " + value + " - " + e.getMessage());
                    
                    // Take screenshot on individual value failure
                    addDemoDelay();
                    try {
                        commonSteps.takeScreenshot("Multi-select Failed - " + value);
                    } catch (Throwable t) {
                        logger.error("Error taking screenshot: {}", t.getMessage());
                        t.printStackTrace();
                        throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
                    }
                    
                    throw new RuntimeException("Failed to select value: " + value, e);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error handling native select dropdown: {}", e.getMessage());
            throw new RuntimeException("Native select dropdown error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Handle custom dropdown implementations (Angular, React, Bootstrap, etc.)
     */
    private void handleCustomDropdown(WebElement dropdownElement, List<String> values) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        try {
            // First, try to open the dropdown
            openCustomDropdown(dropdownElement, wait);
            
            for (String value : values) {
                try {
                    logger.info("Attempting to select custom dropdown option: {}", value);
                    
                    // Find the option element using multiple strategies
                    WebElement optionElement = findDropdownOption(value, wait);
                    
                    if (optionElement == null) {
                        throw new RuntimeException("Option not found: " + value);
                    }
                    
                    // Click the option
                    wait.until(ExpectedConditions.elementToBeClickable(optionElement));
                    optionElement.click();
                    logger.info("Clicked option: {}", value);
                    
                    ReportManager.logPass("Multi-select value", "Successfully selected: " + value);
                    
                    // Small delay between selections for UI stability
                    Thread.sleep(500);
                    
                    // For some dropdowns, we might need to reopen after each selection
                    if (values.indexOf(value) < values.size() - 1) {
                        try {
                            // Check if dropdown is still open, if not reopen it
                            Thread.sleep(200);
                            if (!isDropdownOpen()) {
                                openCustomDropdown(dropdownElement, wait);
                            }
                        } catch (Exception reopenException) {
                            logger.debug("Could not determine dropdown state or reopen: {}", reopenException.getMessage());
                        }
                    }
                    
                } catch (Exception e) {
                    logger.error("Failed to select custom option: {} - {}", value, e.getMessage());
                    ReportManager.logFail("Multi-select value failed", "Failed to select: " + value + " - " + e.getMessage());
                    
                    // Take screenshot on individual value failure
                    addDemoDelay();
                    try {
                        commonSteps.takeScreenshot("Multi-select Failed - " + value);
                    } catch (Throwable t) {
                        logger.error("Error taking screenshot: {}", t.getMessage());
                        t.printStackTrace();
                        throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
                    }
                    
                    throw new RuntimeException("Failed to select value: " + value, e);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error handling custom dropdown: {}", e.getMessage());
            throw new RuntimeException("Custom dropdown error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Open custom dropdown using various trigger strategies
     */
    private void openCustomDropdown(WebElement dropdownElement, WebDriverWait wait) {
        try {
            logger.debug("Attempting to open custom dropdown");
            
            // Try clicking the main dropdown element first
            try {
                wait.until(ExpectedConditions.elementToBeClickable(dropdownElement));
                dropdownElement.click();
                Thread.sleep(300);
                logger.debug("Clicked main dropdown element");
                return;
            } catch (Exception e) {
                logger.debug("Main dropdown element not clickable: {}", e.getMessage());
            }
            
            // Try finding and clicking specific trigger elements
            List<String> triggerSelectors = Arrays.asList(
                ".//button", 
                ".//div[contains(@class,'dropdown-toggle')]",
                ".//span[contains(@class,'select')]", 
                ".//i[contains(@class,'arrow')]",
                ".//div[contains(@class,'trigger')]",
                ".//div[contains(@class,'control')]"
            );
            
            for (String selector : triggerSelectors) {
                try {
                    WebElement trigger = dropdownElement.findElement(By.xpath(selector));
                    wait.until(ExpectedConditions.elementToBeClickable(trigger));
                    trigger.click();
                    Thread.sleep(300);
                    logger.debug("Clicked dropdown trigger using selector: {}", selector);
                    return;
                } catch (Exception e) {
                    continue;
                }
            }
            
            logger.warn("Could not find clickable trigger for custom dropdown");
            
        } catch (Exception e) {
            logger.debug("Error opening custom dropdown: {}", e.getMessage());
        }
    }
    
    /**
     * Find dropdown option using multiple XPath strategies
     */
    private WebElement findDropdownOption(String value, WebDriverWait wait) {
        List<String> optionSelectors = Arrays.asList(
            "//option[normalize-space(text())='" + value + "']",  // Native select options
            "//li[normalize-space(text())='" + value + "']",      // Custom dropdown li items
            "//div[contains(@class,'option') and normalize-space(text())='" + value + "']", // Custom div options
            "//span[contains(@class,'option') and normalize-space(text())='" + value + "']", // Custom span options
            "//*[@data-value='" + value + "']",                   // Options with data-value attribute
            "//*[contains(@class,'dropdown-item') and normalize-space(text())='" + value + "']", // Bootstrap dropdown items
            "//mat-option[normalize-space(span/text())='" + value + "']", // Angular Material options
            "//*[contains(@class,'select-option') and normalize-space(text())='" + value + "']", // Generic select options
            "//a[normalize-space(text())='" + value + "']"        // Link-based options
        );
        
        for (String selector : optionSelectors) {
            try {
                WebElement option = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(selector)));
                if (option.isDisplayed()) {
                    logger.debug("Found option using selector: {}", selector);
                    return option;
                }
            } catch (TimeoutException e) {
                continue;
            }
        }
        
        logger.error("Option '{}' not found with any selector strategy", value);
        return null;
    }
    
    /**
     * Check if custom dropdown is still open
     */
    private boolean isDropdownOpen() {
        try {
            List<String> openDropdownSelectors = Arrays.asList(
                "//div[contains(@class,'dropdown-menu') and contains(@class,'show')]",
                "//ul[contains(@class,'dropdown-menu') and not(contains(@class,'hidden'))]",
                "//div[contains(@class,'select-dropdown') and contains(@class,'open')]",
                "//*[contains(@class,'options') and not(contains(@style,'display: none'))]"
            );
            
            for (String selector : openDropdownSelectors) {
                try {
                    WebElement openDropdown = driver.findElement(By.xpath(selector));
                    if (openDropdown.isDisplayed()) {
                        return true;
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
            
            return false;
        } catch (Exception e) {
            logger.debug("Error checking dropdown state: {}", e.getMessage());
            return false;
        }
    }
    
    // ================================
    // LAST ROW VERIFICATION STEP DEFINITION
    // ================================
    
    /**
     * Verifies that a newly added row appears as the last row in a table.
     * 
     * @param columnName The name of the column to search in
     * @param value The value to search for in that column
     * @throws Throwable If verification fails
     */
    @Then("I verify row with {string} value {string} appears as the last row in the table")
    public void verifyRowAppearsAsLastRow(String columnName, String value) throws Throwable {
        try {
            logger.info("=== STARTING LAST ROW VERIFICATION ===");
            logger.info("Verifying row with {}='{}' appears as the last row in the table", columnName, value);
            
            // Replace test data placeholders
            String resolvedColumnName = commonSteps.replaceTestDataPlaceholders(columnName);
            String resolvedValue = commonSteps.replaceTestDataPlaceholders(value);
            
            logger.info("Resolved parameters - Column: '{}', Value: '{}'", resolvedColumnName, resolvedValue);
            
            // Step 1: Check if table has pagination
            boolean hasPagination = checkIfTableHasPagination();
            logger.info("Table has pagination: {}", hasPagination);
            
            if (hasPagination) {
                // Navigate to last page and verify
                verifyLastRowWithPagination(resolvedColumnName, resolvedValue);
            } else {
                // Check single page table
                verifyLastRowSinglePage(resolvedColumnName, resolvedValue);
            }
            
            // Take screenshot on success
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("Last Row Verification Success");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Screenshot failed: " + t.getMessage(), t);
            }
            
            ReportManager.logPass("Last Row Verification", 
                String.format("Successfully verified row with %s='%s' appears as the last row", resolvedColumnName, resolvedValue));
            logger.info("=== LAST ROW VERIFICATION COMPLETED SUCCESSFULLY ===");
            
        } catch (AssertionError e) {
            logger.error("=== LAST ROW VERIFICATION FAILED ===");
            logger.error("Assertion failed: {}", e.getMessage());
            
            // Take screenshot on failure
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("Last Row Verification Failed");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
            }
            
            ReportManager.logFail("Last Row Verification Failed", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("=== LAST ROW VERIFICATION FAILED ===");
            logger.error("Error during last row verification: {}", e.getMessage());
            
            // Take screenshot on error
            addDemoDelay();
            try {
                commonSteps.takeScreenshot("Last Row Verification Error");
            } catch (Throwable t) {
                logger.error("Error taking screenshot: {}", t.getMessage());
            }
            
            ReportManager.logFail("Last Row Verification Error", e.getMessage());
            throw new RuntimeException("Last row verification failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Checks if the table has pagination controls
     * 
     * @return true if pagination exists, false otherwise
     */
    private boolean checkIfTableHasPagination() {
        try {
            WebElement paginationContainer = findPaginationContainer();
            if (paginationContainer != null) {
                // Check for page buttons or navigation elements
                List<WebElement> pageElements = paginationContainer.findElements(By.xpath(
                    ".//button[matches(text(),'^[0-9]+$')] | .//a[matches(text(),'^[0-9]+$')] | " +
                    ".//button[contains(text(),'>') or contains(@title,'Next')] | " +
                    ".//button[contains(text(),'<') or contains(@title,'Previous')]"
                ));
                return !pageElements.isEmpty();
            }
        } catch (Exception e) {
            logger.debug("Error checking pagination: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * Verifies last row in a table with pagination
     * 
     * @param columnName The column name to search in
     * @param value The value to search for
     * @throws Throwable If verification fails
     */
    private void verifyLastRowWithPagination(String columnName, String value) throws Throwable {
        try {
            logger.info("Verifying last row in paginated table");
            
            // Navigate to the last page
            navigateToLastPage();
            
            // Check if the last row contains the specified value
            boolean isLastRow = checkIfValueIsInLastRow(columnName, value);
            
            if (isLastRow) {
                logger.info("Successfully found value '{}' in column '{}' in the last row of last page", value, columnName);
                ReportManager.logPass("Last Row Check", 
                    String.format("Value '%s' found in column '%s' in the last row of the table", value, columnName));
            } else {
                // Search all pages to find where the row actually is
                searchAllPagesAndReport(columnName, value);
            }
            
        } catch (Exception e) {
            logger.error("Error verifying last row with pagination: {}", e.getMessage());
            throw new RuntimeException("Pagination verification failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifies last row in a single page table
     * 
     * @param columnName The column name to search in
     * @param value The value to search for
     * @throws Throwable If verification fails
     */
    private void verifyLastRowSinglePage(String columnName, String value) throws Throwable {
        try {
            logger.info("Verifying last row in single page table");
            
            boolean isLastRow = checkIfValueIsInLastRow(columnName, value);
            
            if (isLastRow) {
                logger.info("Successfully found value '{}' in column '{}' in the last row", value, columnName);
                ReportManager.logPass("Last Row Check", 
                    String.format("Value '%s' found in column '%s' in the last row of the table", value, columnName));
            } else {
                // Search the entire single page to find where the row actually is
                searchSinglePageAndReport(columnName, value);
            }
            
        } catch (Exception e) {
            logger.error("Error verifying last row in single page: {}", e.getMessage());
            throw new RuntimeException("Single page verification failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Navigates to the last page of a paginated table
     * 
     * @throws Exception If navigation fails
     */
    private void navigateToLastPage() throws Exception {
        try {
            logger.info("Navigating to last page");
            
            WebElement paginationContainer = findPaginationContainer();
            if (paginationContainer == null) {
                throw new Exception("Pagination container not found");
            }
            
            int maxAttempts = 50; // Prevent infinite loops
            int attempts = 0;
            
            while (attempts < maxAttempts) {
                try {
                    // Look for Next button
                    WebElement nextButton = paginationContainer.findElement(By.xpath(
                        ".//button[contains(text(),'>') or contains(@title,'Next') or contains(@class,'next') or contains(@aria-label,'Next')]"
                    ));
                    
                    // Check if Next button is enabled
                    if (!isNextButtonEnabled()) {
                        logger.info("Reached last page after {} navigation attempts", attempts);
                        break;
                    }
                    
                    // Click Next button
                    nextButton.click();
                    attempts++;
                    
                    // Wait for page to load
                    waitForTableUpdate();
                    addDemoDelay();
                    
                    logger.debug("Navigated to next page, attempt: {}", attempts);
                    
                } catch (NoSuchElementException e) {
                    logger.info("Next button not found, assuming last page reached after {} attempts", attempts);
                    break;
                }
            }
            
            if (attempts >= maxAttempts) {
                throw new Exception("Failed to reach last page within " + maxAttempts + " attempts");
            }
            
            ReportManager.logPass("Navigation", "Successfully navigated to last page");
            
        } catch (Exception e) {
            logger.error("Error navigating to last page: {}", e.getMessage());
            throw new Exception("Failed to navigate to last page: " + e.getMessage(), e);
        }
    }
    
    /**
     * Checks if the specified value exists in the last row of the current page
     * 
     * @param columnName The column name to search in
     * @param value The value to search for
     * @return true if value is found in the last row, false otherwise
     */
    private boolean checkIfValueIsInLastRow(String columnName, String value) {
        try {
            logger.debug("Checking if value '{}' exists in last row of column '{}'", value, columnName);
            
            // Find all table rows
            List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
            
            if (rows.isEmpty()) {
                logger.warn("No table rows found");
                return false;
            }
            
            // Get the last row
            WebElement lastRow = rows.get(rows.size() - 1);
            logger.debug("Found {} total rows, checking last row (index {})", rows.size(), rows.size() - 1);
            
            // Find the column index
            int columnIndex = findColumnIndex(columnName, null);
            if (columnIndex == -1) {
                logger.error("Column '{}' not found in table headers", columnName);
                return false;
            }
            
            // Get cells in the last row
            List<WebElement> cells = lastRow.findElements(By.xpath(".//td"));
            if (cells.size() <= columnIndex) {
                logger.warn("Last row has {} cells, but column index is {}", cells.size(), columnIndex);
                return false;
            }
            
            // Check if the value matches
            String cellText = cells.get(columnIndex).getText().trim();
            boolean matches = value.equals(cellText);
            
            logger.debug("Last row cell text in column '{}': '{}', Expected: '{}', Matches: {}", 
                columnName, cellText, value, matches);
            
            return matches;
            
        } catch (Exception e) {
            logger.error("Error checking last row: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Searches all pages to find where the row actually exists and reports the location
     * 
     * @param columnName The column name to search in
     * @param value The value to search for
     * @throws AssertionError Always throws since this means the row was not in the last position
     */
    private void searchAllPagesAndReport(String columnName, String value) throws AssertionError {
        try {
            logger.info("Searching all pages to locate row with {}='{}'", columnName, value);
            
            // Navigate back to first page
            navigateToFirstPage();
            
            int currentPage = 1;
            boolean found = false;
            String foundLocation = "";
            
            do {
                logger.debug("Searching page {} for value '{}'", currentPage, value);
                
                // Search current page
                int rowNumber = findRowNumberOnCurrentPage(columnName, value);
                
                if (rowNumber > 0) {
                    found = true;
                    foundLocation = String.format("row number %d on page %d", rowNumber, currentPage);
                    logger.info("Found value '{}' at {}", value, foundLocation);
                    break;
                }
                
                // Try to go to next page
                if (hasNextPage(null)) {
                    if (!goToNextPage(null)) {
                        break;
                    }
                    currentPage++;
                } else {
                    break;
                }
                
            } while (true);
            
            if (found) {
                String errorMessage = String.format(
                    "Row with value '%s' in column '%s' found in %s, but not in the last row.", 
                    value, columnName, foundLocation
                );
                logger.error(errorMessage);
                ReportManager.logFail("Last Row Verification", errorMessage);
                throw new AssertionError(errorMessage);
            } else {
                String errorMessage = String.format(
                    "Row with value '%s' in column '%s' not found in the table.", 
                    value, columnName
                );
                logger.error(errorMessage);
                ReportManager.logFail("Last Row Verification", errorMessage);
                throw new AssertionError(errorMessage);
            }
            
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during comprehensive search: {}", e.getMessage());
            String errorMessage = String.format(
                "Error occurred while searching for row with value '%s' in column '%s': %s", 
                value, columnName, e.getMessage()
            );
            ReportManager.logFail("Last Row Verification", errorMessage);
            throw new AssertionError(errorMessage, e);
        }
    }
    
    /**
     * Searches single page to find where the row actually exists and reports the location
     * 
     * @param columnName The column name to search in
     * @param value The value to search for
     * @throws AssertionError Always throws since this means the row was not in the last position
     */
    private void searchSinglePageAndReport(String columnName, String value) throws AssertionError {
        try {
            logger.info("Searching single page to locate row with {}='{}'", columnName, value);
            
            int rowNumber = findRowNumberOnCurrentPage(columnName, value);
            
            if (rowNumber > 0) {
                String errorMessage = String.format(
                    "Row with value '%s' in column '%s' found in row number %d on page 1, but not in the last row.", 
                    value, columnName, rowNumber
                );
                logger.error(errorMessage);
                ReportManager.logFail("Last Row Verification", errorMessage);
                throw new AssertionError(errorMessage);
            } else {
                String errorMessage = String.format(
                    "Row with value '%s' in column '%s' not found in the table.", 
                    value, columnName
                );
                logger.error(errorMessage);
                ReportManager.logFail("Last Row Verification", errorMessage);
                throw new AssertionError(errorMessage);
            }
            
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during single page search: {}", e.getMessage());
            String errorMessage = String.format(
                "Error occurred while searching for row with value '%s' in column '%s': %s", 
                value, columnName, e.getMessage()
            );
            ReportManager.logFail("Last Row Verification", errorMessage);
            throw new AssertionError(errorMessage, e);
        }
    }
    
    /**
     * Finds the row number (1-based) of a value in the specified column on the current page
     * 
     * @param columnName The column name to search in
     * @param value The value to search for
     * @return The row number (1-based) if found, 0 if not found
     */
    private int findRowNumberOnCurrentPage(String columnName, String value) {
        try {
            // Find all table rows
            List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
            
            if (rows.isEmpty()) {
                logger.debug("No table rows found on current page");
                return 0;
            }
            
            // Find the column index
            int columnIndex = findColumnIndex(columnName, null);
            if (columnIndex == -1) {
                logger.error("Column '{}' not found in table headers", columnName);
                return 0;
            }
            
            // Search through rows
            for (int i = 0; i < rows.size(); i++) {
                try {
                    List<WebElement> cells = rows.get(i).findElements(By.xpath(".//td"));
                    if (cells.size() > columnIndex) {
                        String cellText = cells.get(columnIndex).getText().trim();
                        if (value.equals(cellText)) {
                            logger.debug("Found value '{}' in row {} (1-based)", value, i + 1);
                            return i + 1; // Return 1-based row number
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Error processing row {}: {}", i + 1, e.getMessage());
                }
            }
            
            logger.debug("Value '{}' not found on current page", value);
            return 0;
            
        } catch (Exception e) {
            logger.error("Error finding row number: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Navigates to the first page of a paginated table
     * 
     * @throws Exception If navigation fails
     */
    private void navigateToFirstPage() throws Exception {
        try {
            logger.debug("Navigating to first page");
            
            WebElement paginationContainer = findPaginationContainer();
            if (paginationContainer == null) {
                logger.debug("No pagination container found, assuming single page");
                return;
            }
            
            int maxAttempts = 50; // Prevent infinite loops
            int attempts = 0;
            
            while (attempts < maxAttempts) {
                try {
                    // Look for Previous button
                    WebElement prevButton = paginationContainer.findElement(By.xpath(
                        ".//button[contains(text(),'<') or contains(@title,'Previous') or contains(@class,'prev') or contains(@aria-label,'Previous')]"
                    ));
                    
                    // Check if Previous button is enabled
                    if (!isPreviousButtonEnabled()) {
                        logger.debug("Reached first page after {} navigation attempts", attempts);
                        break;
                    }
                    
                    // Click Previous button
                    prevButton.click();
                    attempts++;
                    
                    // Wait for page to load
                    waitForTableUpdate();
                    Thread.sleep(500);
                    
                    logger.debug("Navigated to previous page, attempt: {}", attempts);
                    
                } catch (NoSuchElementException e) {
                    logger.debug("Previous button not found, assuming first page reached after {} attempts", attempts);
                    break;
                }
            }
            
            if (attempts >= maxAttempts) {
                throw new Exception("Failed to reach first page within " + maxAttempts + " attempts");
            }
            
        } catch (Exception e) {
            logger.error("Error navigating to first page: {}", e.getMessage());
            throw new Exception("Failed to navigate to first page: " + e.getMessage(), e);
        }
    }
    
    
    /**
     * Find pagination container using multiple XPath selectors
     */
    public WebElement findPaginationContainer() {
        try {
            List<String> paginationSelectors = java.util.Arrays.asList(
                "//div[contains(@class,'pagination')]",
                "//nav[contains(@class,'pagination')]", 
                "//ul[contains(@class,'pagination')]",
                "//div[contains(@class,'pager')]",
                "//div[contains(@class,'page-nav')]"
            );
            
            for (String selector : paginationSelectors) {
                try {
                    WebElement element = driver.findElement(By.xpath(selector));
                    if (element.isDisplayed()) {
                        logger.debug("Found pagination container using selector: {}", selector);
                        return element;
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
            
            logger.warn("Pagination container not found with any of the standard selectors");
            return null;
            
        } catch (Exception e) {
            logger.error("Error finding pagination container: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Get current page number from pagination
     */
    public String getCurrentPageNumber() {
        try {
            WebElement paginationContainer = findPaginationContainer();
            if (paginationContainer != null) {
                WebElement activePageElement = paginationContainer.findElement(By.xpath(".//button[contains(@class,'active') or contains(@class,'current')] | .//a[contains(@class,'active') or contains(@class,'current')]"));
                return activePageElement.getText().trim();
            }
        } catch (Exception e) {
            logger.debug("Error getting current page number: {}", e.getMessage());
        }
        return "1"; // Default to page 1
    }
    
    /**
     * Check if Next button is enabled
     */
    public boolean isNextButtonEnabled() {
        try {
            WebElement paginationContainer = findPaginationContainer();
            if (paginationContainer != null) {
                WebElement nextButton = paginationContainer.findElement(By.xpath(".//button[contains(text(),'>') or contains(@title,'Next') or contains(@class,'next') or contains(@aria-label,'Next')]"));
                return nextButton.isEnabled() && !nextButton.getAttribute("class").contains("disabled");
            }
        } catch (Exception e) {
            logger.debug("Error checking Next button status: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * Check if Previous button is enabled
     */
    public boolean isPreviousButtonEnabled() {
        try {
            WebElement paginationContainer = findPaginationContainer();
            if (paginationContainer != null) {
                WebElement prevButton = paginationContainer.findElement(By.xpath(".//button[contains(text(),'<') or contains(@title,'Previous') or contains(@class,'prev') or contains(@aria-label,'Previous')]"));
                return prevButton.isEnabled() && !prevButton.getAttribute("class").contains("disabled");
            }
        } catch (Exception e) {
            logger.debug("Error checking Previous button status: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * Get first User ID from table for page change verification
     */
    public String getFirstUserIdFromTable() {
        try {
            WebElement firstRowUserIdCell = driver.findElement(By.xpath("//table//tbody//tr[1]//td[1]"));
            return firstRowUserIdCell.getText().trim();
        } catch (Exception e) {
            logger.debug("Error getting first User ID from table: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Wait for table content to refresh after navigation
     */
    public void waitForTableUpdate() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table//tbody//tr[1]//td[1]")));
            Thread.sleep(1000); // Additional wait for content to stabilize
        } catch (Exception e) {
            logger.debug("Error waiting for table update: {}", e.getMessage());
        }
    }
    
    /**
     * Find a table row by a key value in any column
     */
    public WebElement findRowByKey(String keyValue) {
        try {
            logger.debug("Searching for table row with key value: {}", keyValue);
            
            List<WebElement> tableRows = driver.findElements(By.xpath("//table//tr"));
            
            for (WebElement row : tableRows) {
                try {
                    List<WebElement> cells = row.findElements(By.xpath(".//td"));
                    for (WebElement cell : cells) {
                        if (cell.getText().trim().equals(keyValue)) {
                            logger.debug("Found key value {} in table row", keyValue);
                            return row;
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
            logger.error("Key value {} not found in the table", keyValue);
            throw new RuntimeException("Key value \"" + keyValue + "\" not found in the table");
            
        } catch (Exception e) {
            logger.error("Error searching for key value {}: {}", keyValue, e.getMessage());
            throw new RuntimeException("Error finding table row for key value: " + keyValue, e);
        }
    }
    
    /**
     * Click on a menu item (utility method)
     */
    public void clickMenuItem(String menuName, String pageName) {
        try {
            logger.info("Clicking menu item: {} on page: {}", menuName, pageName);
            
            try {
                By userManagementLocator = ORLoader.getLocator(pageName, "User Management");
                WebElement userManagementElement = driver.findElement(userManagementLocator);
                
                if (!userManagementElement.getAttribute("class").contains("expanded")) {
                    logger.info("Expanding User Management parent menu");
                    userManagementElement.click();
                    try {
                        commonSteps.waitForSeconds("1");
                    } catch (Throwable t) {
                        logger.error("Error waiting for menu expansion: {}", t.getMessage());
                        t.printStackTrace();
                        throw new RuntimeException("Menu expansion wait failed: " + t.getMessage(), t);
                    }
                }
            } catch (Exception e) {
                logger.debug("User Management parent menu may already be expanded or not found: {}", e.getMessage());
            }
            
            String elementName = menuName;
            try {
                commonSteps.clickElement(elementName, pageName);
                logger.info("Clicked on menu item: {}", menuName);
            } catch (Throwable t) {
                logger.error("Error clicking menu item {}: {}", menuName, t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Menu click failed: " + t.getMessage(), t);
            }
            
            try {
                commonSteps.waitForSeconds("2");
            } catch (Throwable t) {
                logger.error("Error waiting for page load: {}", t.getMessage());
                t.printStackTrace();
                throw new RuntimeException("Page load wait failed: " + t.getMessage(), t);
            }
            
        } catch (Exception e) {
            logger.error("Error clicking menu item {}: {}", menuName, e.getMessage());
            throw new RuntimeException("Menu item click failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Find file input element by label text
     */
    private WebElement findFileInputByLabel(String label) {
        try {
            // Try multiple approaches to find file input by label
            List<String> labelSelectors = java.util.Arrays.asList(
                "//label[contains(text(),'" + label + "')]/following-sibling::input[@type='file']",
                "//label[contains(text(),'" + label + "')]/..//input[@type='file']",
                "//input[@type='file' and contains(@placeholder,'" + label + "')]",
                "//input[@type='file' and contains(@title,'" + label + "')]",
                "//div[contains(text(),'" + label + "')]//input[@type='file']",
                "//span[contains(text(),'" + label + "')]//input[@type='file']"
            );
            
            for (String selector : labelSelectors) {
                try {
                    WebElement element = driver.findElement(By.xpath(selector));
                    if (element.isDisplayed() || element.isEnabled()) {
                        logger.debug("Found file input using selector: {}", selector);
                        return element;
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
            
            logger.warn("File input with label '{}' not found with any selector", label);
            return null;
            
        } catch (Exception e) {
            logger.error("Error finding file input by label '{}': {}", label, e.getMessage());
            return null;
        }
    }
    
    /**
     * Find table row by column value
     */
    private WebElement findTableRowByColumnValue(String columnName, String value) {
        try {
            logger.debug("Searching for table row where {} = {}", columnName, value);
            
            // First, find the column index by header text
            List<WebElement> headers = driver.findElements(By.xpath("//table//th"));
            int columnIndex = -1;
            
            for (int i = 0; i < headers.size(); i++) {
                if (headers.get(i).getText().trim().equalsIgnoreCase(columnName)) {
                    columnIndex = i + 1; // XPath is 1-indexed
                    break;
                }
            }
            
            if (columnIndex == -1) {
                throw new RuntimeException("Column '" + columnName + "' not found in table headers");
            }
            
            // Find the row with the matching value in that column
            WebElement targetRow = driver.findElement(By.xpath("//table//tr[td[" + columnIndex + "][contains(text(),'" + value + "')]]"));
            logger.debug("Found table row where {} = {}", columnName, value);
            return targetRow;
            
        } catch (Exception e) {
            logger.error("Error finding table row where {} = {}: {}", columnName, value, e.getMessage());
            return null;
        }
    }
    
    /**
     * Find toggle switch in a table row
     */
    private WebElement findToggleInRow(WebElement row) {
        try {
            // Try multiple selectors for toggle switches
            List<String> toggleSelectors = java.util.Arrays.asList(
                ".//input[@type='checkbox']",
                ".//button[contains(@class,'toggle')]",
                ".//div[contains(@class,'toggle')]",
                ".//span[contains(@class,'toggle')]",
                ".//input[contains(@class,'switch')]",
                ".//label[contains(@class,'switch')]"
            );
            
            for (String selector : toggleSelectors) {
                try {
                    WebElement toggle = row.findElement(By.xpath(selector));
                    if (toggle.isDisplayed()) {
                        logger.debug("Found toggle using selector: {}", selector);
                        return toggle;
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
            
            logger.warn("Toggle switch not found in table row");
            return null;
            
        } catch (Exception e) {
            logger.error("Error finding toggle in table row: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if toggle is in ON state
     */
    private boolean isToggleOn(WebElement toggleElement) {
        try {
            // Check various attributes that indicate toggle state
            String checked = toggleElement.getAttribute("checked");
            String ariaChecked = toggleElement.getAttribute("aria-checked");
            String className = toggleElement.getAttribute("class");
            
            // For checkbox inputs
            if ("checkbox".equals(toggleElement.getAttribute("type"))) {
                return toggleElement.isSelected();
            }
            
            // For aria-checked attribute
            if ("true".equals(ariaChecked)) {
                return true;
            }
            
            // For checked attribute
            if (checked != null && !checked.isEmpty()) {
                return true;
            }
            
            // For CSS classes indicating active/on state
            if (className != null && (className.contains("active") || className.contains("on") || className.contains("checked"))) {
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.debug("Error checking toggle state: {}", e.getMessage());
            return false;
        }
    }
}
