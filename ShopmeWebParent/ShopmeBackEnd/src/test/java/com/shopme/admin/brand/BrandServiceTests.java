package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Brand;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTests {
	
	@MockBean
	private BrandRepository brandRepository;
	
	@InjectMocks
	private BrandService brandService;
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicate() {
		Integer id = null;
		String name = "Sony";
		Brand brand = new Brand(name);
		
		// khi add(id==null), name="Sony" đã trùng với name của 1 brand trong DB -> báo lỗi trùng tên 
		Mockito.when(brandRepository.findByName(name)).thenReturn(brand);
		String result = brandService.checkUnique(id, name);	
		assertThat(result).isEqualTo("Duplicate Name");
	}
	
	@Test
	public void testCheckUniqueInModeReturnOK() {
		Integer id = null;
		String name = "Samsung";
		
		// khi add(id==null), và không tìm thấy brand nào trong DB có name="Samsung" -> trả về null -> OK
		Mockito.when(brandRepository.findByName(name)).thenReturn(null);
		String result = brandService.checkUnique(id, name);	
		assertThat(result).isEqualTo("OK");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnDuplicate() {
		Integer id = 4;
		String name = "Canon";
		Brand brand = new Brand(id, name);
		
		Mockito.when(brandRepository.findByName(name)).thenReturn(brand);
		String result = brandService.checkUnique(5, "Canon");	
		assertThat(result).isEqualTo("Duplicate Name");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnOK() {
		Integer id = 1;
		String name = "Sony";
		Brand brand = new Brand(id, name);
		
		Mockito.when(brandRepository.findByName(name)).thenReturn(brand);
		String result = brandService.checkUnique(1, "Apple");	
		assertThat(result).isEqualTo("OK");
	}
	
}






