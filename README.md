# CircleCI-JavaDemo

[![CircleCI](https://circleci.com/gh/PerfectoCode/CircleCI-JavaDemo.svg?style=shield)](https://circleci.com/gh/PerfectoCode/CircleCI-JavaDemo)

This project shows how to integrate CircleCI with your Perfecto lab on the cloud and getting tests report with Perfecto DigitalZoom Reporting.

## Getting Started:
- Use git clone or download the project and customize your test within the [RemoteWebDriverTest](src/test/java/RemoteWebDriverTest.java) class:
 ```Java
reportiumClient.testStart(TestName, new TestContext(platformName, platformVersion, browserName));

reportiumClient.testStep("Open google"); // testStep
driver.get("https://www.google.com/");

reportiumClient.testStep("Search PerfectoCode"); // testStep
driver.findElement(By.name("q")).sendKeys("PerfectoCode");

/**
 * Complete your test here ...
 */
```

- **Importent!** set the following environment variables in order to connect Perfecto Lab via CircleCI machine:
 ```Java
 // Set cloud host and credentials values from CI, else use local values
 String PERFECTO_HOST = System.getenv("PERFECTO_HOST");
 String PERFECTO_USER = System.getenv("PERFECTO_USER");
 String PERFECTO_PASSWORD = System.getenv("PERFECTO_PASSWORD");
 ```
For more information about this step see CircleCI documentation [here](https://circleci.com/docs/1.0/environment-variables/#custom).
## DigitalZoom Reporting:
For the complete documentation of Perfecto DigitalZoom Reporting click [here](http://developers.perfectomobile.com/display/PD/Reporting).

- The [Utils](src/test/java/Utils.java) class include the ReportingClient construction as the below code snippet:
```Java
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
```
In the presented method we are using `System.getenv(...)` in order to require from CircleCI the Project name and build number.<br/>

- In order to get the complete test report for tests within the same job (In [RemoteWebDriverTest](src/test/java/RemoteWebDriverTest.java) class):
 <br/>**Note!** This step require to set first the `REPORTING_SERVER` env variable within CircleCI configuration.
 For more information about your Reporting server see [here](http://developers.perfectomobile.com/display/PD/Reporting#Reporting-ReportingserverAccessingthereports).
```Java
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
```



