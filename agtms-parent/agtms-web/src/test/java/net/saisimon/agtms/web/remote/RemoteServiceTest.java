package net.saisimon.agtms.web.remote;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.remote.service.GenerateRemoteService;
import net.saisimon.agtms.remote.service.RemoteApiService;
import net.saisimon.agtms.scanner.TemplateScanner;
import net.saisimon.agtms.web.domain.SimpleEntity;
import net.saisimon.agtms.web.remote.RemoteServiceTest.LocalRibbonClientConfiguration;
import net.saisimon.agtms.web.selection.GenderSelection;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=RemoteTestApplication.class, properties = {"spring.main.bannerMode=OFF", "logging.level.root=ERROR"})
@ContextConfiguration(classes = {LocalRibbonClientConfiguration.class})
public class RemoteServiceTest {
	
	@ClassRule
	public static WireMockClassRule wireMockRule = new WireMockClassRule(WireMockConfiguration.wireMockConfig().dynamicPort().dynamicHttpsPort());
	@Rule
	public WireMockClassRule instanceRule = wireMockRule;
	
	@Autowired
	private GenerateRemoteService generateRemoteService;
	@Autowired
	private RemoteApiService remoteApiService;
	@Autowired
	private TemplateScanner templateScanner;
	@Autowired
	private GenderSelection genderSelection;
	
	@Before
	public void setUp() throws IOException {
		List<Template> templates = templateScanner.getAllTemplates();
		instanceRule.stubFor(post(urlEqualTo("/agtms/templates"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(SystemUtils.toJson(templates))));
		instanceRule.stubFor(post(urlEqualTo("/agtms/simpleEntity/template"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(SystemUtils.toJson(templates.get(0)))));
		instanceRule.stubFor(post(urlEqualTo("/agtms/GenderSelection/selection"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(SystemUtils.toJson(genderSelection.select()))));
		List<SimpleEntity> tests = new ArrayList<>();
		SimpleEntity test = buildTestSimpleEntity();
		tests.add(test);
		Map<String, Object> page = buildTestSimpleEntityPage(tests);
		instanceRule.stubFor(post(urlEqualTo("/agtms/simpleEntity/count"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(SystemUtils.toJson(tests.size()))));
		instanceRule.stubFor(post(urlEqualTo("/agtms/simpleEntity/findList"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(SystemUtils.toJson(tests))));
		instanceRule.stubFor(post(urlEqualTo("/agtms/simpleEntity/findPage"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(SystemUtils.toJson(page))));
		instanceRule.stubFor(post(urlEqualTo("/agtms/simpleEntity/findOne"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(SystemUtils.toJson(test))));
		instanceRule.stubFor(post(urlEqualTo("/agtms/simpleEntity/findById?id=1"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(SystemUtils.toJson(test))));
		instanceRule.stubFor(post(urlEqualTo("/agtms/simpleEntity/delete"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(SystemUtils.toJson(tests.size()))));
		instanceRule.stubFor(post(urlEqualTo("/agtms/simpleEntity/deleteEntity"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
		instanceRule.stubFor(post(urlEqualTo("/agtms/simpleEntity/saveOrUpdate"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(SystemUtils.toJson(test))));
		instanceRule.stubFor(post(urlEqualTo("/agtms/simpleEntity/batchUpdate"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
	}
	
	@Test
	public void test() throws GenerateException {
		String serviceId = "127.0.0.1:" + wireMockRule.port();
		String key = "simpleEntity";
		String selectionKey = "GenderSelection";
		List<Template> templates = remoteApiService.templates(serviceId);
		Assert.assertEquals(1, templates.size());
		Template template = remoteApiService.template(serviceId, key);
		template.setService(serviceId);
		template.setKey(key);
		Assert.assertEquals("remote", template.getSource());
		LinkedHashMap<?, String> map = remoteApiService.selection(serviceId, selectionKey, new HashMap<String, Object>());
		Assert.assertEquals(2, map.size());
		generateRemoteService.init(template);
		Domain domain = generateRemoteService.newGenerate();
		Assert.assertEquals(1, generateRemoteService.count(FilterRequest.build()).intValue());
		List<Domain> domains = generateRemoteService.findList(FilterRequest.build(), FilterPageable.build(null));
		Assert.assertEquals(1, domains.size());
		Assert.assertEquals("Test", domains.get(0).getField("name"));
		domains = generateRemoteService.findList(FilterRequest.build(), FilterSort.build(null));
		Assert.assertEquals(1, domains.size());
		Assert.assertEquals("Test", domains.get(0).getField("name"));
		Page<Domain> page = generateRemoteService.findPage(FilterRequest.build(), FilterPageable.build(null), true);
		Assert.assertEquals(1, page.getTotalElements());
		Assert.assertEquals(1, page.getContent().size());
		Assert.assertEquals("Test", page.getContent().get(0).getField("name"));
		Optional<Domain> optional = generateRemoteService.findOne(FilterRequest.build(), FilterSort.build(null));
		Assert.assertTrue(optional.isPresent());
		domain = generateRemoteService.findById(1L, Arrays.asList(1L));
		Assert.assertEquals("Test", domain.getField("name"));
		generateRemoteService.delete(domain);
		generateRemoteService.delete(FilterRequest.build());
		domain = generateRemoteService.saveDomain(domain, 1L);
		Assert.assertEquals("Test", domain.getField("name"));
		Assert.assertTrue(generateRemoteService.checkExist(domain, Arrays.asList(1L)));
		domain = generateRemoteService.updateDomain(domain, generateRemoteService.newGenerate(), 1L);
		Assert.assertEquals("Test", domain.getField("name"));
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("name", "test");
		generateRemoteService.updateDomain(1L, updateMap);
	}
	
	public static class LocalRibbonClientConfiguration {
		
		@Bean
		public ServerList<Server> ribbonServerList() {
			return new StaticServerList<>(new Server("127.0.0.1", wireMockRule.port()));
		}
		
	}
	
	private SimpleEntity buildTestSimpleEntity() {
		SimpleEntity test = new SimpleEntity();
		test.setAge(18);
		test.setBirthday(new Date());
		test.setDesc("-");
		test.setGender(1);
		test.setHome("/");
		test.setId(1);
		test.setName("Test");
		return test;
	}
	
	private Map<String, Object> buildTestSimpleEntityPage(List<SimpleEntity> tests) {
		Map<String, Object> page = new HashMap<>();
		page.put("rows", tests);
		page.put("total", tests.size());
		return page;
	}
	
}

@SpringCloudApplication
@EnableFeignClients
@ComponentScan(basePackages="net.saisimon.agtms")
class RemoteTestApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RemoteTestApplication.class, args);
	}
	
}