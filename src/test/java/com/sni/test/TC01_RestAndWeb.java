package com.sni.test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.core.MultivaluedMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;

import org.testng.annotations.*;
import org.testng.Reporter;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class TC01_RestAndWeb {

	public SauceOnDemandAuthentication authentication; 
	private WebDriver driver;
	public DesiredCapabilities capabilities = new DesiredCapabilities();

    /**
     * Creates a new {@link RemoteWebDriver} instance to be used to run WebDriver tests using Sauce.
     *
     * @param username the Sauce username
     * @param key the Sauce access key
     * @param os the operating system to be used
     * @param browser the name of the browser to be used
     * @param browserVersion the version of the browser to be used
     * @param method the test method being executed
     * @throws Exception thrown if any errors occur in the creation of the WebDriver instance
     */
	
	@Parameters({"username", "key", "os", "browser", "browserVersion", "userId"})
	@BeforeMethod
	public void setUp(@Optional("camilhord") String username,
            		  @Optional("616636cf-9c9c-4db8-9349-503c030b61d8") String key,
            		  @Optional("Windows 2003") String os,
            		  @Optional("firefox") String browser,
            		  @Optional("17") String browserVersion,
            		  @Optional("1550012520") String userId,
            		  Method method) throws Exception {
			
			authentication = new SauceOnDemandAuthentication(username, key);
			capabilities.setBrowserName(browser);
			capabilities.setCapability("version", browserVersion);
			capabilities.setCapability("platform", os);
			capabilities.setCapability("name", method.getName());
			driver = new RemoteWebDriver(
			      new URL("http://" + authentication.getUsername() + ":" + authentication.getAccessKey() + "@ondemand.saucelabs.com:80/wd/hub"),
			      capabilities);
			}
	
	@Parameters({"userId"})
	@Test
    public void RESTUsingBrowser(String userId) {
		    driver.get("http://aim.dev-foodnetwork.com/aim/rest/userprofile/public?userId="+userId);
			String response = driver.getPageSource();
			assert (response.contains(userId)): "The response message didn't have the user ID";
	}			
	
    @Test 
    public void WebUITest(Method method){
			
    		driver.get("http://www.google.com");
			WebElement searchbox = driver.findElement(By.name("q"));
			searchbox.sendKeys("Scripps Networks");
			searchbox.sendKeys(Keys.ENTER);
			searchbox.sendKeys(Keys.ENTER);
			
			WebDriverWait wait = new WebDriverWait(driver, 10);
			
			WebElement Searchbutton= driver.findElement(By.name("btnG"));
			Searchbutton.click();
			
			assert (driver.getTitle().contains("Scripps")): "The query didn't throw any results";
    }
    
    @Parameters({"userId"})
    @Test
	 public void RESTUsingHtmlUnit (String userId) throws RuntimeException, MalformedURLException {
		Client client = Client.create();
		
		WebResource webResource = client.resource("http://aim.dev-foodnetwork.com/aim/rest/userprofile/public");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("userId", userId);

	    ClientResponse response = webResource
	    		.queryParams(queryParams)
	    		.accept("application/json")
	    		.get(ClientResponse.class);
		 
	    String JsonResponse = response.getEntity(String.class);
	    int HTMLstatus= response.getStatus();
	    
	    assert (HTMLstatus == 200): "The response code was not 200 OK";
	    assert (JsonResponse.contains(userId)): "The response message didn't have the user ID";
	}

    @AfterMethod
    public void tearDown() throws Exception {
        driver.quit();
    }
}