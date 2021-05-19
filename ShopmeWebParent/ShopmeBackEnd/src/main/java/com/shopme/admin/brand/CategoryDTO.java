package com.shopme.admin.brand;



/**
 * DTO: đối tượng của lớp này sẽ được gửi từ server đến client -> nó phải càng nhẹ càng tốt 
 *
 */
public class CategoryDTO {

	private Integer id;
	private String name;

	public CategoryDTO() {
	}

	public CategoryDTO(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
