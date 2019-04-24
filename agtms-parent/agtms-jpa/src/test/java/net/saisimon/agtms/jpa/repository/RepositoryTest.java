package net.saisimon.agtms.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.jpa.JpaTestApplication;
import net.saisimon.agtms.jpa.domain.TestEmbeddable;
import net.saisimon.agtms.jpa.domain.TestEmbeddable.TestEmbeddablePK;
import net.saisimon.agtms.jpa.domain.TestIdClass;
import net.saisimon.agtms.jpa.domain.TestLong;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JpaTestApplication.class, properties = { "spring.main.banner-mode=OFF", "logging.level.net.saisimon=DEBUG" })
@DataJpaTest
@JdbcTest
public class RepositoryTest {

	@Autowired
	private TestLongRepository testRepository;
	@Autowired
	private TestEmbeddableRepository testEmbeddableRepository;
	@Autowired
	private TestIdClassRepository testIdClassRepository;

	@Test
	public void testLong() {
		int size = 20;
		for (int i = 0; i < size; i++) {
			createTestLong("TestLong-" + i, "100-" + i);
		}
		// Spring findAll
		Page<TestLong> testLongAll = testRepository.findAll(PageRequest.of(0, 10, Sort.by(Order.desc(Constant.ID))));
		// AGTMS findPage
		Page<TestLong> testLongPage = testRepository.findPage(null, new FilterPageable(0, 10, FilterSort.build(Constant.ID, Direction.DESC)));
		Assert.assertEquals(testLongAll.getTotalElements(), testLongPage.getTotalElements());
		Assert.assertEquals(testLongAll.getContent().size(), testLongPage.getContent().size());
		Assert.assertEquals(testLongAll.getContent().get(0).getId(), testLongPage.getContent().get(0).getId());
		Assert.assertEquals(testLongAll.getContent().get(0).getName(), testLongPage.getContent().get(0).getName());
		Assert.assertEquals(testLongAll.getContent().get(0).getAge(), testLongPage.getContent().get(0).getAge());
		// AGTMS findPage
		testLongPage = testRepository.findPage(null, new FilterPageable(0, 10, FilterSort.build(Constant.ID, Direction.DESC)), "id", "name", "idCard", "remark");
		Assert.assertEquals(testLongAll.getTotalElements(), testLongPage.getTotalElements());
		Assert.assertEquals(testLongAll.getContent().size(), testLongPage.getContent().size());
		Assert.assertEquals(testLongAll.getContent().get(0).getId(), testLongPage.getContent().get(0).getId());
		Assert.assertEquals(testLongAll.getContent().get(0).getName(), testLongPage.getContent().get(0).getName());
		Assert.assertNull(testLongPage.getContent().get(0).getAge());
		// AGTMS findOne
		Optional<TestLong> optional = testRepository.findOne(FilterRequest.build().and("name", "TestLong-14"));
		Assert.assertTrue(optional.isPresent());
		TestLong testLong = optional.get();
		Assert.assertEquals("TestLong-14", testLong.getName());
		Assert.assertEquals("100-14", testLong.getIdCard());
		Assert.assertEquals(18, testLong.getAge().intValue());
		Assert.assertEquals("-", testLong.getRemark());
		// AGTMS findOne
		optional = testRepository.findOne(FilterRequest.build().and("name", "TestLong-140"));
		Assert.assertFalse(optional.isPresent());
		// AGTMS findOne
		optional = testRepository.findOne(FilterRequest.build().and("name", "TestLong-14"), "id", "name", "idCard", "remark");
		Assert.assertTrue(optional.isPresent());
		testLong = optional.get();
		Assert.assertEquals("TestLong-14", testLong.getName());
		Assert.assertEquals("100-14", testLong.getIdCard());
		Assert.assertNull(testLong.getAge());
		Assert.assertEquals("-", testLong.getRemark());
		// AGTMS findList
		List<TestLong> testLongs = testRepository.findList(null);
		Assert.assertEquals(size, testLongs.size());
		testLong = testLongs.get(0);
		Assert.assertEquals("TestLong-0", testLong.getName());
		Assert.assertEquals("100-0", testLong.getIdCard());
		Assert.assertEquals(18, testLong.getAge().intValue());
		Assert.assertEquals("-", testLong.getRemark());
		// AGTMS findList
		testLongs = testRepository.findList(null, "id", "name", "idCard", "remark");
		Assert.assertEquals(size, testLongs.size());
		testLong = testLongs.get(0);
		Assert.assertEquals("TestLong-0", testLong.getName());
		Assert.assertEquals("100-0", testLong.getIdCard());
		Assert.assertNull(testLong.getAge());
		Assert.assertEquals("-", testLong.getRemark());
	}

	@Test
	public void testIdClass() {
		int size = 20;
		for (int i = 0; i < size; i++) {
			createTestIdClass("TestIdClass-" + i, "200-" + i);
		}
		// Spring findAll
		Page<TestIdClass> testIdClassAll = testIdClassRepository.findAll(PageRequest.of(0, 10, Sort.by(Order.desc("name"), Order.desc("idCard"))));
		// AGTMS findPage
		Page<TestIdClass> testIdClassPage = testIdClassRepository.findPage(null, new FilterPageable(0, 10, FilterSort.build("name", Direction.DESC).desc("idCard")));
		Assert.assertEquals(testIdClassAll.getTotalElements(), testIdClassPage.getTotalElements());
		Assert.assertEquals(testIdClassAll.getContent().size(), testIdClassPage.getContent().size());
		Assert.assertEquals(testIdClassAll.getContent().get(0).getIdCard(), testIdClassPage.getContent().get(0).getIdCard());
		Assert.assertEquals(testIdClassAll.getContent().get(0).getName(), testIdClassPage.getContent().get(0).getName());
		Assert.assertEquals(testIdClassAll.getContent().get(0).getAge(), testIdClassPage.getContent().get(0).getAge());
		// AGTMS findPage
		testIdClassPage = testIdClassRepository.findPage(null, new FilterPageable(0, 10, FilterSort.build("name", Direction.DESC).desc("idCard")), "name", "idCard", "remark");
		Assert.assertEquals(testIdClassAll.getTotalElements(), testIdClassPage.getTotalElements());
		Assert.assertEquals(testIdClassAll.getContent().size(), testIdClassPage.getContent().size());
		Assert.assertEquals(testIdClassAll.getContent().get(0).getIdCard(), testIdClassPage.getContent().get(0).getIdCard());
		Assert.assertEquals(testIdClassAll.getContent().get(0).getName(), testIdClassPage.getContent().get(0).getName());
		Assert.assertNull(testIdClassPage.getContent().get(0).getAge());
		// AGTMS findOne
		Optional<TestIdClass> optional = testIdClassRepository.findOne(FilterRequest.build().and("name", "TestIdClass-14"));
		Assert.assertTrue(optional.isPresent());
		TestIdClass testIdClass = optional.get();
		Assert.assertEquals("TestIdClass-14", testIdClass.getName());
		Assert.assertEquals("200-14", testIdClass.getIdCard());
		Assert.assertEquals(18, testIdClass.getAge().intValue());
		Assert.assertEquals("-", testIdClass.getRemark());
		// AGTMS findOne
		optional = testIdClassRepository.findOne(FilterRequest.build().and("name", "TestIdClass-140"));
		Assert.assertFalse(optional.isPresent());
		// AGTMS findOne
		optional = testIdClassRepository.findOne(FilterRequest.build().and("name", "TestIdClass-14"), "name", "idCard", "remark");
		Assert.assertTrue(optional.isPresent());
		testIdClass = optional.get();
		Assert.assertEquals("TestIdClass-14", testIdClass.getName());
		Assert.assertEquals("200-14", testIdClass.getIdCard());
		Assert.assertNull(testIdClass.getAge());
		Assert.assertEquals("-", testIdClass.getRemark());
		// AGTMS findList
		List<TestIdClass> testIdClasses = testIdClassRepository.findList(null);
		Assert.assertEquals(size, testIdClasses.size());
		testIdClass = testIdClasses.get(0);
		Assert.assertEquals("TestIdClass-0", testIdClass.getName());
		Assert.assertEquals("200-0", testIdClass.getIdCard());
		Assert.assertEquals(18, testIdClass.getAge().intValue());
		Assert.assertEquals("-", testIdClass.getRemark());
		// AGTMS findList
		testIdClasses = testIdClassRepository.findList(null, "name", "idCard", "remark");
		Assert.assertEquals(size, testIdClasses.size());
		testIdClass = testIdClasses.get(0);
		Assert.assertEquals("TestIdClass-0", testIdClass.getName());
		Assert.assertEquals("200-0", testIdClass.getIdCard());
		Assert.assertNull(testIdClass.getAge());
		Assert.assertEquals("-", testIdClass.getRemark());
	}

	@Test
	public void testEmbeddable() {
		int size = 20;
		for (int i = 0; i < size; i++) {
			createTestEmbeddable("TestEmbeddable-" + i, "300-" + i);
		}
		// Spring findAll
		Page<TestEmbeddable> testEmbeddableAll = testEmbeddableRepository.findAll(PageRequest.of(0, 10, Sort.by(Order.desc("pk"))));
		// AGTMS findPage
		Page<TestEmbeddable> testEmbeddablePage = testEmbeddableRepository.findPage(null, new FilterPageable(0, 10, FilterSort.build("pk", Direction.DESC)));
		Assert.assertEquals(testEmbeddableAll.getTotalElements(), testEmbeddablePage.getTotalElements());
		Assert.assertEquals(testEmbeddableAll.getContent().size(), testEmbeddablePage.getContent().size());
		Assert.assertEquals(testEmbeddableAll.getContent().get(0).getPk().getIdCard(), testEmbeddablePage.getContent().get(0).getPk().getIdCard());
		Assert.assertEquals(testEmbeddableAll.getContent().get(0).getPk().getName(), testEmbeddablePage.getContent().get(0).getPk().getName());
		Assert.assertEquals(testEmbeddableAll.getContent().get(0).getAge(), testEmbeddablePage.getContent().get(0).getAge());
		// AGTMS findPage
		testEmbeddablePage = testEmbeddableRepository.findPage(null, new FilterPageable(0, 10, FilterSort.build("pk", Direction.DESC)), "pk", "remark");
		Assert.assertEquals(testEmbeddableAll.getTotalElements(), testEmbeddablePage.getTotalElements());
		Assert.assertEquals(testEmbeddableAll.getContent().size(), testEmbeddablePage.getContent().size());
		Assert.assertEquals(testEmbeddableAll.getContent().get(0).getPk().getIdCard(), testEmbeddablePage.getContent().get(0).getPk().getIdCard());
		Assert.assertEquals(testEmbeddableAll.getContent().get(0).getPk().getName(), testEmbeddablePage.getContent().get(0).getPk().getName());
		Assert.assertNull(testEmbeddablePage.getContent().get(0).getAge());
		// AGTMS findOne
		TestEmbeddablePK pk = new TestEmbeddablePK();
		pk.setIdCard("300-14");
		pk.setName("TestEmbeddable-14");
		Optional<TestEmbeddable> optional = testEmbeddableRepository.findOne(FilterRequest.build().and("pk", pk));
		Assert.assertTrue(optional.isPresent());
		TestEmbeddable testEmbeddable = optional.get();
		Assert.assertEquals("TestEmbeddable-14", testEmbeddable.getPk().getName());
		Assert.assertEquals("300-14", testEmbeddable.getPk().getIdCard());
		Assert.assertEquals(18, testEmbeddable.getAge().intValue());
		Assert.assertEquals("-", testEmbeddable.getRemark());
		// AGTMS findOne
		pk.setName("TestEmbeddable-140");
		optional = testEmbeddableRepository.findOne(FilterRequest.build().and("pk", pk));
		Assert.assertFalse(optional.isPresent());
		// AGTMS findOne
		pk.setName("TestEmbeddable-14");
		optional = testEmbeddableRepository.findOne(FilterRequest.build().and("pk", pk), "pk", "remark");
		Assert.assertTrue(optional.isPresent());
		testEmbeddable = optional.get();
		Assert.assertEquals("TestEmbeddable-14", testEmbeddable.getPk().getName());
		Assert.assertEquals("300-14", testEmbeddable.getPk().getIdCard());
		Assert.assertNull(testEmbeddable.getAge());
		Assert.assertEquals("-", testEmbeddable.getRemark());
		// AGTMS findList
		List<TestEmbeddable> testIdClasses = testEmbeddableRepository.findList(null);
		Assert.assertEquals(size, testIdClasses.size());
		testEmbeddable = testIdClasses.get(0);
		Assert.assertEquals("TestEmbeddable-0", testEmbeddable.getPk().getName());
		Assert.assertEquals("300-0", testEmbeddable.getPk().getIdCard());
		Assert.assertEquals(18, testEmbeddable.getAge().intValue());
		Assert.assertEquals("-", testEmbeddable.getRemark());
		// AGTMS findList
		testIdClasses = testEmbeddableRepository.findList(null, "pk", "remark");
		Assert.assertEquals(size, testIdClasses.size());
		testEmbeddable = testIdClasses.get(0);
		Assert.assertEquals("TestEmbeddable-0", testEmbeddable.getPk().getName());
		Assert.assertEquals("300-0", testEmbeddable.getPk().getIdCard());
		Assert.assertNull(testEmbeddable.getAge());
		Assert.assertEquals("-", testEmbeddable.getRemark());
	}

	private TestLong createTestLong(String name, String idCard) {
		TestLong test = new TestLong();
		test.setIdCard(idCard);
		test.setName(name);
		test.setAge(18);
		test.setRemark("-");
		testRepository.saveAndFlush(test);
		return test;
	}

	private TestIdClass createTestIdClass(String name, String idCard) {
		TestIdClass test = new TestIdClass();
		test.setIdCard(idCard);
		test.setName(name);
		test.setAge(18);
		test.setRemark("-");
		testIdClassRepository.saveAndFlush(test);
		return test;
	}

	private TestEmbeddable createTestEmbeddable(String name, String idCard) {
		TestEmbeddable test = new TestEmbeddable();
		TestEmbeddablePK pk = new TestEmbeddablePK();
		pk.setIdCard(idCard);
		pk.setName(name);
		test.setPk(pk);
		test.setAge(18);
		test.setRemark("-");
		testEmbeddableRepository.saveAndFlush(test);
		return test;
	}

}
