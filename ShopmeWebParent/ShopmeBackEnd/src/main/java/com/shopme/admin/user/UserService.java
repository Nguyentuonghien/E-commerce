package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {
	
	public static final int USERS_PER_PAGE = 4;	
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<User> listAll() {
		return (List<User>) userRepository.findAll();
	}
	
	public Page<User> listByPage(int pageNumber, String sortField, String sortOrder) {
		Sort sort = Sort.by(sortField);
		// nếu sortOrder là asc -> sắp xếp tăng dần và ngược lại
		sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();
		// vì APIs pagination coi trang đầu tiên = 0 nhưng ở màn hình view ta sẽ hiển thị trang đầu tiên là 1 -> (pageNumber - 1)  
		Pageable pageable = PageRequest.of(pageNumber-1, USERS_PER_PAGE, sort);
		return userRepository.findAll(pageable);
	}
	
	public List<Role> getListRole() {
		return (List<Role>) roleRepository.findAll();
	}
	
	public User save(User user) {
		// update: nếu user không nhập password vào trên form(user k muốn đổi pass) -> pass giữ nguyên
		// nếu user nhập pass mới -> encode cho pass mới đó
		if (user.getId() != null) {
			User existingUser = userRepository.findById(user.getId()).get();
			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			} else {
				encodePassword(user);
			}
		} else {
			encodePassword(user);
		}
		return userRepository.save(user);
	}
	
	public void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	// check email khong duoc trung nhau(1 user chi co duy nhat 1 email ung vs no) 
	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepository.getUserByEmail(email);
		if(userByEmail == null) return true;  
		
		// nếu là form add(id==null) và nếu có một user được tìm thấy bởi email đã cho(userByEmail != null) -> thì email trong form add không hợp lệ (đã được sử dụng).
        // nếu là form edit và nếu có user được tìm thấy bởi email không phải là người dùng đang được chỉnh sửa(userByEmail.getId() != id) ->
		// thì email trong form edit không hợp lệ (khác id trong DB nhưng lại có email trùng nhau)
		if(id == null) {
			if(userByEmail != null) return false;
		} else {
			if(userByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}
	
	public User getUserById(Integer id) throws UserNotFoundException {
		try {
			return userRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID: " + id);
		}
	}

	public void deleteUser(Integer id) throws UserNotFoundException {
		Long countById = userRepository.countById(id);
		// không tìm thấy id tương ứng trong DB
		if(countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID: " + id);
		}
		userRepository.deleteById(id);
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepository.updateEnabledStatus(id, enabled);
	}
	
}















