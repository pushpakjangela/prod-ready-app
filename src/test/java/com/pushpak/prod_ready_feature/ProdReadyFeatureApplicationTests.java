package com.pushpak.prod_ready_feature;

import com.pushpak.prod_ready_feature.entities.User;
import com.pushpak.prod_ready_feature.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProdReadyFeatureApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private JwtService jwtService;

	@Test
	void testJwtGeneration() {
		User user = new User(2L, "pushpak@gmail.com", "123", "pushpak");

		String token = jwtService.generateAccessToken(user);
		System.out.println("token "+ token);

		Long id = jwtService.getUserIdFromToken(token);
		System.out.println("user id : "+id);
	}


}
