package com.stolser.user;

import junit.framework.Assert;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;

@Ignore
@RunWith(Arquillian.class)
public class LoginPageUITest {
	private WebDriver browser;
	
/*	@FindBy(id = "loggingForm:loginInput")
	private WebElement usernameInput;
	
	@FindBy(id = "loggingForm:passwordInput")
	private WebElement passwordInput;
	
	@FindBy(css = ".logInButton")
	private WebElement logInButton;*/
	
	@Before
	public void setUp() throws Exception {
		System.setProperty("webdriver.chrome.driver", 
				"D://Programs//Java programming//WebDrivers//chromedriver_win32//chromedriver.exe");
		browser = new ChromeDriver();
	}
	
	public LoginPageUITest() {}
	@Test
	public void usernameFieldShouldHaveMessages() {
		browser.get("http://localhost/jeeDerbyELink/adminLogin.jsf");
		WebElement usernameInput = browser.findElement(By.id("loggingForm:loginInput"));
		WebElement passwordInput = browser.findElement(By.id("loggingForm:passwordInput"));
		usernameInput.sendKeys("incorrectUserName");
		Graphene.guardAjax(passwordInput).click();
		String expectednoUserMessage = "Sorry, but we dont recognize your login.";
		String actualNoUserMessage = browser.findElement(
				By.cssSelector("#loggingForm:loginInputMsg .ui-message-error-detail"))
				.getText();
		Assert.assertEquals(expectednoUserMessage, actualNoUserMessage);
		

	}
	
	@Test(expected = NoSuchElementException.class)
	public void usernameFieldShouldBeWithoutMessages() {
		browser.get("http://localhost/jeeDerbyELink/adminLogin.jsf");
		WebElement usernameInput = browser.findElement(By.id("loggingForm:loginInput"));
		WebElement passwordInput = browser.findElement(By.id("loggingForm:passwordInput"));
		usernameInput.sendKeys("superadmin");
		Graphene.guardAjax(passwordInput).click();
		browser.findElement(
				By.cssSelector("#loggingForm:loginInputMsg .ui-message-error-detail"));		

	}
	
	@After
	public void tearDown() throws Exception {
		browser.quit();
	}
	

}












