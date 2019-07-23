package net.saisimon.agtms.core.factory;

import org.junit.Assert;
import org.junit.Test;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.exception.AGTMSException;

public class FactoryTest {
	
	@Test(expected = AGTMSException.class)
	public void testActuatorFactory() {
		ActuatorFactory.get(null);
	}
	
	@Test(expected = AGTMSException.class)
	public void testFieldHandlerFactory() {
		FieldHandlerFactory.getHandler(null);
	}
	
	@Test(expected = AGTMSException.class)
	public void testGenerateServiceFactory() {
		Template template = new Template();
		GenerateServiceFactory.build(template);
	}
	
	@Test(expected = AGTMSException.class)
	public void testGenerateServiceFactoryWithNullSource() {
		Template template = new Template();
		template.setSource("jpa");
		GenerateServiceFactory.build(template);
	}
	
	@Test(expected = AGTMSException.class)
	public void testNavigationServiceFactory() {
		NavigationServiceFactory.get();
	}
	
	public void testOperationHandlerFactory() {
		Assert.assertNull(OperationHandlerFactory.getHandler(null));
	}
	
	@Test(expected = AGTMSException.class)
	public void testOperationServiceFactory() {
		OperationServiceFactory.get();
	}
	
	@Test(expected = AGTMSException.class)
	public void testSelectionServiceFactory() {
		SelectionServiceFactory.get();
	}
	
	@Test(expected = AGTMSException.class)
	public void testTaskServiceFactory() {
		TaskServiceFactory.get();
	}
	
	@Test(expected = AGTMSException.class)
	public void testTemplateServiceFactory() {
		TemplateServiceFactory.get();
	}
	
	@Test(expected = AGTMSException.class)
	public void testTokenFactory() {
		TokenFactory.get();
	}
	
	@Test(expected = AGTMSException.class)
	public void testUserServiceFactory() {
		UserServiceFactory.get();
	}
	
}
