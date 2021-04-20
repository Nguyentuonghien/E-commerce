package com.shopme.admin.user.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.User;

public class UserExcelExporter extends AbstractExporter {

	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	public UserExcelExporter() {
		workbook = new XSSFWorkbook();
	}

	public void exportExcel(List<User> users, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/octet-stream", ".xlsx", "users_");
		writeHeaderLines();
		writeDataLines(users);
		// ghi nội dung của file Excel vào output stream của response, vì vậy clients(trình duyệt web) sẽ có thể tải xuống file Excel đã export
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

	private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
		XSSFCell cell = row.createCell(columnIndex);
		sheet.autoSizeColumn(columnIndex);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	private void writeHeaderLines() {
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		cellStyle.setFont(font);
		// rowHeader(dòng đầu tiên) luôn bắt đầu từ 0
		sheet = workbook.createSheet("Users");
		XSSFRow rowHeader = sheet.createRow(0);
		
		// tạo ra các cell ứng với các title trong rowHeader
		createCell(rowHeader, 0, "User ID", cellStyle);
		createCell(rowHeader, 1, "E-mail", cellStyle);
		createCell(rowHeader, 2, "First Name", cellStyle);
		createCell(rowHeader, 3, "Last Name", cellStyle);
		createCell(rowHeader, 4, "Roles", cellStyle);
		createCell(rowHeader, 5, "Enabled", cellStyle);
	}

	private void writeDataLines(List<User> listUsers) {
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		cellStyle.setFont(font);

		// vì row đầu tiên(row=0) là rowHeader(chứa các title) nên từ row thứ 2(rowIndex=1), columnIndex=0 là User ID -> chạy từ 0->4 tương ứng với từ UserID
		// đến Enabled -> cứ 1 vòng for(duyệt qua 1 user) ta sẽ tạo ra 1 row mới và binding data vào các cell úng với từng columnIndex
		int rowIndex = 1;
		for (User user : listUsers) {
			int columnIndex = 0;
			XSSFRow row = sheet.createRow(rowIndex++);
			createCell(row, columnIndex++, user.getId(), cellStyle);
			createCell(row, columnIndex++, user.getEmail(), cellStyle);
			createCell(row, columnIndex++, user.getFirstName(), cellStyle);
			createCell(row, columnIndex++, user.getLastName(), cellStyle);
			createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
			createCell(row, columnIndex++, user.isEnabled(), cellStyle);
		}
	}

}

