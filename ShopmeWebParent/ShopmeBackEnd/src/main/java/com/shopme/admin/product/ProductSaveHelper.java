package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;

public class ProductSaveHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

	public static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}

	// Edit Extra Images
	public static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
		if (imageIDs == null || imageIDs.length == 0)
			return;
		Set<ProductImage> productImages = new HashSet<>();
		for (int count = 0; count < imageIDs.length; count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];
			// sau khi lấy ra id, name của từng p/tử trong mảng imageIDs, ta sẽ put vào
			// trong Set<> productImages
			productImages.add(new ProductImage(id, name, product));
		}
		// sau khi edit các extra Images xong -> gán lại cho product 1 Set<>
		// productImages
		product.setProductImages(productImages);
	}

	// New(Add) Extra Images
	public static void setNewExtraImageNames(MultipartFile[] extraImagesMultipart, Product product) {
		if (extraImagesMultipart.length > 0) {
			for (MultipartFile multipartFile : extraImagesMultipart) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					// trước khi add extraImage vào product, ta check product phải không chứa tên
					// của extraImage đã cho(tên extraImage k trùng nhau)
					if (!product.containsImageName(fileName)) {
						product.addExtraImage(fileName);
					}
				}
			}
		}
	}

	public static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues,
			Product product) {
		if (detailNames == null || detailNames.length == 0)
			return;
		for (int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			if (id != 0) { // edit detail object
				product.addDetail(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) { // add new detail object
				product.addDetail(name, value);
			}
		}
	}

	// sau khi main image và các extra images được upload lên(add or edit) -> lưu
	// các ảnh đó vào file system
	public static void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImagesMultipart,
			Product savedProduct) throws IOException {
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			String uploadDir = "../product-images/" + savedProduct.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}
		if (extraImagesMultipart.length > 0) {
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
			for (MultipartFile multipartFile : extraImagesMultipart) {
				if (multipartFile.isEmpty())
					continue;
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
	}

	// xóa các files extra Images đã bị remove khỏi form
	public static void deleteExtraImagesWereRemovedOnForm(Product product) {
		String extraImagesDir = "../product-images/" + product.getId() + "/extras";
		Path dirPath = Paths.get(extraImagesDir);
		try {
			Files.list(dirPath).forEach(file -> {
				// lấy ra tên của từng file ảnh trong list file, kiểm tra nếu product không chứa
				// tên của file ảnh đó -> xóa ảnh đó
				String fileName = file.toFile().getName();
				if (!product.containsImageName(fileName)) {
					try {
						Files.delete(file);
						LOGGER.info("Deleted extra image: " + fileName);
					} catch (IOException e) {
						LOGGER.error("Could not delete extra image: " + fileName);
					}
				}
			});
		} catch (IOException ex) {
			LOGGER.error("Could not list directory: " + dirPath);
		}
	}

}
