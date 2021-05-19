package com.shopme.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 256, nullable = false, unique = true)
	private String name;
	
	@Column(length = 256, nullable = false, unique = true)
	private String alias;
	
	@Column(length = 4096, nullable = false, name = "short_description")
	private String shortDescription;
	
	@Column(length = 4096, nullable = false, name = "full_description")
	private String fullDescription;
	
	@Column(name="created_time")
	private Date createdTime;
	
	@Column(name="updated_time")
	private Date updatedTime;

	private boolean enabled;
	
	@Column(name = "in_stock")
	private boolean inStock;

	private float cost;

	private float price;
	
	@Column(name = "discount_percent")
	private float discountPercent;

	private float length;
	private float width;
	private float height;
	private float weight;

	@Column(name = "main_image", nullable = false)
	private String mainImage;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProductImage> productImages = new HashSet<>();
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductDetail> productDetails = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;	

	@ManyToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;

	
	public Product() {
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isInStock() {
		return inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(float discountPercent) {
		this.discountPercent = discountPercent;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}
	
	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public Set<ProductImage> getProductImages() {
		return productImages;
	}

	public void setProductImages(Set<ProductImage> productImages) {
		this.productImages = productImages;
	}

	public List<ProductDetail> getProductDetails() {
		return productDetails;
	}

	public void setProductDetails(List<ProductDetail> productDetails) {
		this.productDetails = productDetails;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + "]";
	}
	
	// add tên ảnh của ProductImage Object vào Set<>
	public void addExtraImage(String imageName) {
		this.productImages.add(new ProductImage(imageName, this)); // this là đối tượng Product 
	}
	
	// add name và value của đối tượng ProductDetail vào ArrayList<>
	public void addDetail(String name, String value) {
		this.productDetails.add(new ProductDetail(name, value, this));
	}
	
	// add id, name và value của đối tượng ProductDetail vào ArrayList<>
	public void addDetail(Integer id, String name, String value) {
		this.productDetails.add(new ProductDetail(id, name, value, this));
	}
	
	@Transient
	public String getMainImagePath() {
		if (id == null || mainImage == null) {
			return "/images/image-thumbnail.png";
		}
		return "/product-images/" + this.id + "/" + this.mainImage;
	}
	
	/**
	 * sử dụng iterator để hiển thị nội dung của Set<> productImages, 
	 * iterator.hasNext(): Nó trả về true nếu iterator còn phần tử kế tiếp phần tử đang duyệt
	 * iterator.next(): Nó trả về phần tử hiện tại và di chuyển con trỏ trỏ tới phần tử tiếp theo 
	 */
	public boolean containsImageName(String imageName) {
		Iterator<ProductImage> iterator = productImages.iterator();
		while (iterator.hasNext()) {
			ProductImage productImage = iterator.next();
			if (productImage.getName().equals(imageName)) {
				return true;
			}
		}
		return false;
	}
	
	@Transient
	public String getShortName() {
		if (name.length() > 70) {
			return name.substring(0, 70).concat("...");
		}
		return name;
	}
	
	@Transient
	public float getDiscountPrice() {
		if (discountPercent > 0) {
			return price * ((100 - discountPercent) / 100);
		}
		return price;
	}
	
}



