import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.*;
import org.testng.ITestResult;
import org.testng.annotations.*;

import javax.rmi.CORBA.Util;
import java.net.MalformedURLException;

/**
 * For programming samples and updated templates refer to the Perfecto GitHub at: https://github.com/PerfectoCode
 */
public class RemoteWebDriverTest {

    private RemoteWebDriver driver;
    private ReportiumClient reportiumClient;
    private static final String TestNamePrefix = "Perfecto DigitalZoom Reporting - ";
    private String TestName;

    /**
     * BeforeMethod
     * run before each test method
     * setup a RemoteWebDriver instance and ReportiumClient
     * @param platformName - capability
     * @param platformVersion - capability
     * @param browserName - capability
     * @param browserVersion - capability
     * @param screenResolution - capability
     * @param model - capability
     * @throws MalformedURLException
     */
    @Parameters({"platformName", "platformVersion", "browserName", "browserVersion", "screenResolution", "model"})
    @BeforeMethod
    public void BeforeMethod(String platformName, String platformVersion, String browserName
            , String browserVersion, String screenResolution, String model) throws MalformedURLException {

        // Standard RemoteWebDriver
        driver = Utils.getRemoteWebDriver(platformName, platformVersion, browserName, browserVersion, screenResolution, model);

        // Setup ReportuimClient instance see Utils class for more information
        reportiumClient = Utils.createReportiumClient(driver , "Tag1", "Tag2", "Tag3");

        // test name to be passes later to the testStart command
        TestName = TestNamePrefix + platformName;
    }

    /**
     * Test method
     * the test to be run right after BeforeMethod
     * @param platformName - passed from TestNG
     * @param platformVersion - passed from TestNG
     * @param browserName - passed from TestNG
     */
    @Parameters({"platformName", "platformVersion", "browserName"})
    @Test
    public void Test(String platformName, String platformVersion, String browserName) {
        reportiumClient.testStart(TestName, new TestContext(platformName, platformVersion, browserName));

        reportiumClient.testStep("Open google"); // testStep
        driver.get("https://www.google.com/");

        reportiumClient.testStep("Search PerfectoCode"); // testStep
        driver.findElement(By.name("q")).sendKeys("PerfectoCode");

        /**
         * Complete your test here ...
         */
    }

    /**
     * AfterMethod
     * run after test
     * pass to reportiumClient the test result using testStop command
     * printing the report URL and download report PDF link.
     * @param result - test result passed automatically from TestNG
     */
    @AfterMethod
    public void AfterMethod(ITestResult result) {
        // Finish the test and determine the results
        if (result.isSuccess()) {
            // On successful test
            reportiumClient.testStop(TestResultFactory.createSuccess());
        } else {
            Throwable t = result.getThrowable(); // Case of failure, rescue the exception message
            reportiumClient.testStop(TestResultFactory.createFailure(t.getMessage(), t));
        }
        try {
            driver.quit();

            // Retrieve the URL to the DigitalZoom Report (= Reportium Application) for an aggregated view over the execution
            String reportURL = reportiumClient.getReportUrl();

            System.out.println("************ Report URL ************");
            System.out.println(reportURL);
            System.out.println("************************************");

            // Retrieve the URL to the Execution Summary PDF Report
            String reportPdfUrl = (String) (driver.getCapabilities().getCapability("reportPdfUrl"));
            System.out.println("************ Report PDF Download ************");
            System.out.println(reportPdfUrl);
            System.out.println("*********************************************");

            // For detailed documentation on how to export the Execution Summary PDF Report, the Single Test report and other attachments such as
            // video, images, device logs, vitals and network files - see http://developers.perfectomobile.com/display/PD/Exporting+the+Reports

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void afterClass(){
        String host = System.getenv("PERFECTO_HOST"); // Your PerfectoLab host
        String serv = System.getenv("REPORTING_SERVER"); // Reporting server
        String jobn = System.getenv("CIRCLE_PROJECT_REPONAME"); // Job name
        String jobv = System.getenv("CIRCLE_BUILD_NUM"); // Job number

        System.out.println("=================================================================");
        System.out.println("See the complete report at: ");
        System.out.println("https://" + host + "." + serv + ".perfectomobile.com/?jobName[0]=" + jobn + "&jobNumber[0]=" + jobv);
        System.out.println("=================================================================");
    }
}
