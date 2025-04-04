package com.gayathricn.selenium;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
public class App 
{
    public static void main( String[] args )
    {        
        //Set ChromeDriver path  
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        
        //Setup Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remove-allow-origins=*");        
        WebDriver driver = new ChromeDriver(options);
        
        try {
        	
        	//maximise window and setting global implicit wait
        	driver.manage().window().maximize();
        	driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        	
        	//Open Playtech people homepage
        	driver.get("https://www.playtechpeople.com/");
            System.out.println( "Webpage opened successfully!" );
        
            //creating explicit wait	
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
           
            //cookie consent management
            try {
            WebElement allowAll = wait.until(ExpectedConditions.elementToBeClickable(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll")));
            allowAll.click();
            } catch(Exception e) {
                //ignore if not present            
            	}
            
            // Click on "Locations"
            driver.findElement(By.linkText("Locations")).click();

            // Click on "View all locations"
            WebElement viewAll = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("View all locations")));
            viewAll.click();

            // Wait for the location list to be ready
            List<WebElement> locations = wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.cssSelector("div.location-item-wrap")));
            
            // Print total count of locations
            System.out.println("Total locations found: " + locations.size());

            // Print only the names of locations
            System.out.println("Locations list: ");
            for (WebElement location : locations) {
                String fullText = location.findElement(By.cssSelector("a.location-item")).getText().trim();
                String[] lines = fullText.split("\\n");  
                if (lines.length > 1) {
                    String country = lines[1];  // 1 = country name
                    System.out.println(country);
                }
            }
            //Click on "life at Playtech" and then on "Who we are"
            driver.findElement(By.linkText("Life at Playtech")).click();
            WebElement whoWeAre = driver.findElement(By.linkText("Who we are"));
            whoWeAre.click();
            Thread.sleep(2000);  // can be replaced by proper wait if required

           // Scroll down to the product suite section
           JavascriptExecutor js = (JavascriptExecutor) driver;
           js.executeScript("window.scrollBy(0, 1000);"); // scroll down to make sure it's in view

           // Find the first product card (Casino is the first one)
           WebElement casinoCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.product-card__inner")));

           // Get and print the description text inside the card
           String description = casinoCard.getText().trim();
           System.out.println("Casino Description:\n" + description);  
         
            // Click All Jobs
            WebElement allJobsBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("All Jobs")));
            allJobsBtn.click();
            Thread.sleep(3000); // Let page load jobs

            // Get all job items showing "Estonia"
            List<WebElement> allJobs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.job-item")));
            List<String> estonianJobLinks = new ArrayList<>();

            for (WebElement job : allJobs) {
                String text = job.getText().toLowerCase();
                if (text.contains("estonia")) {
                    estonianJobLinks.add(job.getAttribute("href"));
                }
            }

            System.out.println("Filtered Estonia job count: " + estonianJobLinks.size());

            String tartuLink = "";
            String tallinnLink = "";
            for (String link : estonianJobLinks) {
                driver.navigate().to(link);
                Thread.sleep(4000);
                // Optional cookie handling
                try {
                    WebElement cookieBtn = driver.findElement(By.xpath("//button[contains(text(), 'Reject all')]"));
                    cookieBtn.click();
                } catch (Exception e) {
                    //ignore if not present
                }

                Thread.sleep(4000); // Let full content load including address

                try {
                    // Search for location only with the main job content
                    WebElement jobContent = driver.findElement(By.cssSelector("main.jobad-main.job"));
                    String address = jobContent.getText().toLowerCase();
                    //printing the tartu, tallinn job link
                    if (address.contains("tartu") && tartuLink.isEmpty()) {
                        tartuLink = link;
                        System.out.println("✅ Tartu job found: " + tartuLink);
                    }
                    if (address.contains("tallinn") && tallinnLink.isEmpty()) {
                        tallinnLink = link;
                        System.out.println("✅ Tallinn job found: " + tallinnLink);
                    }

                    if (!tartuLink.isEmpty() && !tallinnLink.isEmpty()) {
                        break;
                    }

                } catch (Exception e) {
                    System.out.println("❌ Couldn't extract location from: " + link);
                }
            }

            	    // DEBUG: show the source in case needed
            	    // System.out.println(driver.getPageSource());
            if (tartuLink.isEmpty()) {
                System.out.println("❌ No job found in Tartu.");
            }
            if (tallinnLink.isEmpty()) {
                System.out.println("❌ No job found in Tallinn.");
            }
            
            } catch(Exception e) {
            	System.out.println("Error message:" + e.getMessage());
            	} finally {
            		driver.quit();
                }
    }
}
       
