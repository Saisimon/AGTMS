package net.saisimon.agtms.web.runner;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.saisimon.agtms.web.annotation.Order;

public class OrderedRunner extends SpringJUnit4ClassRunner {

	private static List<FrameworkMethod> testMethodList;

	public OrderedRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		if (testMethodList == null) {
			testMethodList = super.computeTestMethods().stream().sorted((m1, m2) -> {
				Order o1 = m1.getAnnotation(Order.class);
				Order o2 = m2.getAnnotation(Order.class);
				if (o1 == null || o2 == null) {
					return 0;
				}
				return o1.value() - o2.value();
			}).collect(Collectors.toList());
		}
		return testMethodList;
	}
}