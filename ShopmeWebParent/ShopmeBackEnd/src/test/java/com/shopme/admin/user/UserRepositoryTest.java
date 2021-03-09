package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TestEntityManager entityManagement;
	
	@Test
	public void testCreateUserWithOneRole() {
		// tim trong DB table roles voi primaryKey=1(ung voi Admin)
		Role roleAdmin = entityManagement.find(Role.class, 1);
		User userOne = new User("tuong@gmail.com", "12345", "Tuong", "Nguyen");
		userOne.addRole(roleAdmin);
		
		User savedUser = userRepository.save(userOne);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateUserWithTwoRole() {
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		User userTwo = new User("trangtran@gmail.com", "112233", "Trang", "Tran");
		
		userTwo.addRole(roleEditor);
		userTwo.addRole(roleAssistant);
		
		User savedUser = userRepository.save(userTwo);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = userRepository.findAll();
		listUsers.forEach(user -> System.out.println(user));
		
	}
	
	@Test
	public void testGetUserById() {
		User user = userRepository.findById(1).get();
		System.out.println(user);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User user = userRepository.findById(1).get();
		user.setEnabled(true);
		user.setEmail("hientuong@gmail.com");
		userRepository.save(user);
	}
	
	@Test
	public void updateUserRoles() {
		User userTrang = userRepository.findById(2).get();
		Role roleEditor = new Role(3);
		Role roleSalesperson = new Role(2);
		
		// loai bo roleEditor cua Trang va thay bang roleSalesperson
		userTrang.getRoles().remove(roleEditor);
		userTrang.addRole(roleSalesperson);
		
		userRepository.save(userTrang);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 2;
		userRepository.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "thaoduong@gmail.com";
		User user = userRepository.getUserByEmail(email);
		
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer id = 1;
		Long count = userRepository.countById(id);
		
		assertThat(count).isNotNull();
	}
	
	@Test
	public void testDisableUser() {
		userRepository.updateEnabledStatus(3, false);
	}
	
	@Test
	public void testEnableUser() {
		userRepository.updateEnabledStatus(4, true);
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;
		
		// lấy ra page đầu tiên, và mỗi page sẽ có 4 phần tử
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> pages = userRepository.findAll(pageable);
		
		List<User> listUsers = pages.getContent();
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
}







