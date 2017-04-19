import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by daniela on 4/12/17.
 */
public class Utils {

    public static RemoteWebDriver getRemoteWebDriver(String platformName, String platformVersion, String browserName,
                                                     String browserVersion, String screenResolution, String model) throws MalformedURLException {

        // Set cloud host and credentials values from CI, else use local values
        String PERFECTO_HOST = System.getenv("PERFECTO_HOST");
        String PERFECTO_USER = System.getenv("PERFECTO_USER");
        String PERFECTO_PASSWORD = System.getenv("PERFECTO_PASSWORD");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("user", PERFECTO_USER);
        capabilities.setCapability("password", PERFECTO_PASSWORD);
        capabilities.setCapability("platformName", platformName);
        capabilities.setCapability("platformVersion", platformVersion);
        capabilities.setCapability("browserName", browserName);
        capabilities.setCapability("browserVersion", browserVersion);
        capabilities.setCapability("model", model);

        System.out.println("Creating Remote WebDriver on: " + platformName);


        RemoteWebDriver webdriver = new RemoteWebDriver(
                new URL("https://" + PERFECTO_HOST + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities);

        // Define RemoteWebDriver timeouts
        webdriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        webdriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

        return webdriver;
    }

    /**
     * createReportiumClient
     *
     * initializing new reporting client using parameters passed from
     * CircleCI env variables
     *
     * @param tags - tags to be passed to ReportiumClient
     *
     * @return new ReportiumClient instance
     */
    public static ReportiumClient createReportiumClient(RemoteWebDriver driver, String ... tags ){

        // See: https://circleci.com/docs/1.0/environment-variables/
        // for the complete CircleCI env variables guide
        String projectName = System.getenv("CIRCLE_PROJECT_REPONAME");
        String projectNumber = System.getenv("CIRCLE_BUILD_NUM");
        String jobName = System.getenv("CIRCLE_PROJECT_REPONAME");
        int jobNumber = Integer.parseInt(System.getenv("CIRCLE_BUILD_NUM"));

        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withProject(new Project(projectName, projectNumber))
                .withJob(new Job(jobName, jobNumber))
                .withContextTags(tags)
                .withWebDriver(driver)
                .build();

        return new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
    }


}