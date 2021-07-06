package com.shopme.address;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;

@Repository
public interface AddressRepository extends CrudRepository<Address, Integer> {
	
	public List<Address> findByCustomer(Customer customer);
	
	@Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
	public Address findByIdAndCustomer(Integer addressId, Integer customerId);
	
	@Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
	@Modifying
	public void deleteByIdAndCustomer(Integer addressId, Integer customerId);
	
	@Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.id = ?1")	
	@Modifying
	public void setDefaultAddress(Integer id);
	
	// nếu 2 address cùng có defaultForShipping=true -> address có id khác defaultAddressId truyền vào thì defaultForShipping=false
	// vì không được 2 address có cùng defaultForShipping=true
	@Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.id != ?1 AND a.customer.id = ?2")
	@Modifying
	public void setNonDefaultAddressForOthers(Integer defaultAddressId, Integer customerId);
	
}
