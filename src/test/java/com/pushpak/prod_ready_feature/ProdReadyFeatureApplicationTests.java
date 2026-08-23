package com.pushpak.prod_ready_feature;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import org.springframework.test.context.ActiveProfiles;

//@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class ProdReadyFeatureApplicationTests {

	@Test
	void contextLoads() {
	}


	@BeforeEach
	void setUp() {
		log.info("running test before each test");
	}

	@AfterEach
	void tearDown() {
		log.info("tearing down after each test");
	}

	@BeforeAll
	static  void setUpAll() {
		log.info("running test before all test ");
	}

	@AfterAll
	static void tearDownAll() {
		log.info("tearing down after all tests");
	}


	@Test
	void test1() {
		log.info("running test 1");
	}


	@Test
	void test2() {
		log.info("running test 2");
	}

	@Test
	void addNumbers(){
		int a = 5;
		int b = 3;
		int ans = addTwonumber(a,b);
		Assertions.assertThat(ans).isEqualTo(8);
	}

	@Test
	void divideTwoValue_when_denominatorIsZero(){
		int a = 5;
		int b = 0;
		Assertions.assertThatThrownBy(() -> divideTwoValues(a,b)).isInstanceOf(ArithmeticException.class).hasMessage("tried to divide by zero");

	}

	int addTwonumber(int a,int b){
		return a+b;
	}

	int divideTwoValues(int a, int b){
		try{
			return a/b;
		}catch(ArithmeticException e){
			log.info("Arithmetic Exception occured: ",e.getLocalizedMessage());
			throw new ArithmeticException("tried to divide by zero");
		}
	}


}
