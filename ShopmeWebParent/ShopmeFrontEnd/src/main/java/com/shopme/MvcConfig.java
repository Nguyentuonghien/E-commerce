package com.shopme;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfig implements WebMvcConfigurer{
	
	/**
	 * Hiển thị hình ảnh đã tải lên trong trình duyệt : ta có thể hiển thị hình ảnh trong trình duyệt với một chút cấu hình, 
	 * ta cần hiển thị thư mục chứa các tệp đã tải lên để web browsers(trình duyệt web) có thể truy cập
	 *
	 */	
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		exposeDirectory("../category-images", registry);
		exposeDirectory("../brand-logos", registry);
		exposeDirectory("../product-images", registry);
		exposeDirectory("../site-logo", registry);
	}
	
	private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
		Path pathDir = Paths.get(pathPattern);
		String absolutePath = pathDir.toFile().getAbsolutePath();
		String logicalPath = pathPattern.replace("..", "") + "/**";
		registry.addResourceHandler(logicalPath).addResourceLocations("file:/" + absolutePath + "/"); 
	}
	
	
	/*
	 String dirName = "user-photos";
	Path userPhotosDir = Paths.get(dirName);
	String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();  // duong dan tuyet doi
	// ánh xạ tới đường dẫn dạng: /user-photos
	registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhotosPath + "/");
	
	String categoryImagesDirName = "../category-images";
	Path categoryImagesDir = Paths.get(categoryImagesDirName);
	String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();
	// ánh xạ tới đường dẫn dạng: /cagtegory-image/**
	registry.addResourceHandler("/category-images/**").addResourceLocations("file:/" + categoryImagesPath + "/");
	
	String brandLogosDirectoryName = "../brand-logos";
	Path brandLogosDir = Paths.get(brandLogosDirectoryName);
	String brandLogosPath = brandLogosDir.toFile().getAbsolutePath();
	registry.addResourceHandler("/brand-logos/**").addResourceLocations("file:/" + brandLogosPath + "/");
	*/
	
}




