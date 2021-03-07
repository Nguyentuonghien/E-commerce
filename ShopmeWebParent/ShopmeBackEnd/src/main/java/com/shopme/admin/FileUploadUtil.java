package com.shopme.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
	
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
	
	// clean directory
	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);
		try {
			Files.list(dirPath).forEach(file -> {
				if(!Files.isDirectory(file)) {
					try {
						Files.delete(file);
					} catch(IOException ex) {
						System.out.println("Could not delete file: "+file);
					}
				}
			});
		} catch(IOException ex) {
			System.out.println("Could not list directory: "+dirPath);
		}
 	}
	
}
