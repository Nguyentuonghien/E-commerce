package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)  // test real database
@Rollback(false)
public class RoleRepositoryTest {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Admin", "manage everything");
		Role savedRole = roleRepository.save(roleAdmin);
		
		// test xem object role da duoc luu vao DB? 
		assertThat(savedRole.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateRestRole() {
		Role roleSalesperson = new Role("Salesperson", "manage product price, " + "customers, shipping, orders and sales report");
		Role roleEditor = new Role("Editor", "manage categories, brands, " + "products, articles and menus");
		Role roleShipper = new Role("Shipper", "view products, view orders " + "and update order status");
		Role roleAssistant = new Role("Assistant", "manage questions and reviews");
		
		List<Role> roles = Arrays.asList(roleSalesperson, roleEditor, roleShipper, roleAssistant);
		
		roleRepository.saveAll(roles);
	}
	
}






