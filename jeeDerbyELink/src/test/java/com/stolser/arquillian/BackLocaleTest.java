package com.stolser.arquillian;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import com.stolser.forTesting.MyClass;

import javax.inject.Inject;

@RunWith(Arquillian.class)
public class BackLocaleTest {

	public BackLocaleTest() {}
	
	@Inject
	MyClass myClass;
	
	@Deployment
    public static JavaArchive createDeployment() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
            .addClasses(MyClass.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(jar.toString(true));
        return jar;
    }
	
	@Test
	public void backLocaleShouldBeInitiated() {

		String message = "Hello World!";
		String actualMessage = myClass.getMessage();
		
		Assert.assertEquals(message, actualMessage);
		
	}

}



















