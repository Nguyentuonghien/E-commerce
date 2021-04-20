package com.shopme.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);
	
	public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
		// lay ra duong dan den thu muc chua cac file uploaded(trong dự án thì uploadPath="user-photos/1")
		Path uploadPath = Paths.get(uploadDir);
		// kiem tra neu chua ton tai --> tao thu muc moi
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		// lưu file đã uploaded từ đối tượng MultipartFile tới 1 file trong file system
		try (InputStream inputStream = multipartFile.getInputStream()) {
			// nối path -> filePath="user-photos/1/fileName"
			Path filePath = uploadPath.resolve(fileName);  
			// sao chép dữ liệu từ inputstream và lưu nó vào ổ cứng
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new IOException("Could not save file " + fileName, ex);
		}
	}
	
	// clean directory: xóa các file ảnh đã tồn tại trong thư mục
	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);
		try {
			Files.list(dirPath).forEach(file -> {
				if(!Files.isDirectory(file)) {
					try {
						Files.delete(file);
					} catch(IOException ex) {
						LOGGER.error("Could not delete file: " + file);
					}
				}
			});
		} catch(IOException ex) {
			LOGGER.error("Could not list directory: " + dirPath);
		}
 	}
	
	public static void removeDir(String dir) {
		// đầu tiên ta sẽ xóa các file ảnh trong thư mục rồi sau đó sẽ xóa thư mục đó
		cleanDir(dir);
		try {
			Files.delete(Paths.get(dir));
		} catch (IOException e) {
			LOGGER.error("Could not remove directory: " + dir);
		}
		
	}
}
