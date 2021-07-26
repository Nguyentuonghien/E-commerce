package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.Setting;

@Controller
public class SettingController {
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private CurrencyRepository currencyRepository;
	
	@GetMapping("/settings")
	public String listAll(Model model) {
		List<Setting> listSettings = settingService.listAllSettings();
		List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();
		for (Setting setting : listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		model.addAttribute("listCurrencies", listCurrencies);
		return "settings/settings";
	}
	
	/**
	 * vì ở form general.html, ta không dùng th:object để binding các field của object(mà dung key để set vào name trong input)
	 * truy cập HttpServletRequest để đọc value của các field trong form 
	 */
	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes attributes, 
			           HttpServletRequest request) throws IOException {
		GeneralSettingBag generalSettingBag = settingService.getGeneralSettings();
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			generalSettingBag.updateSiteLogo(value);
			String uploadDir = "../site-logo/";
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} 
		saveCurrencySymbol(request, generalSettingBag);
		updateSettingValuesFromForm(request, generalSettingBag.listSettings());
		
		attributes.addFlashAttribute("message", "General settings have been saved.");
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSettings(HttpServletRequest servletRequest, RedirectAttributes attributes) {
		List<Setting> mailServerSettings = settingService.getMailServerSettings();
		updateSettingValuesFromForm(servletRequest, mailServerSettings);
		attributes.addFlashAttribute("message", "Mail server settings have been saved.");
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_mail_templates")
	public String saveMailTemplateSettings(HttpServletRequest servletRequest, RedirectAttributes attributes) {
		List<Setting> mailTemplateSettings = settingService.getMailTemplateSettings();
		updateSettingValuesFromForm(servletRequest, mailTemplateSettings);
		attributes.addFlashAttribute("message", "Mail template settings have been saved.");
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_payment")
	public String savePaymentSettings(HttpServletRequest request, RedirectAttributes attributes) {
		List<Setting> paymentSettings = settingService.getPaymentSettings();
		updateSettingValuesFromForm(request, paymentSettings);
		attributes.addFlashAttribute("message", "Payment settings have been saved.");
		return "redirect:/settings";
	}
	
	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag generalSettingBag) {
		// lấy ra currencyId từ form(general.html) thông qua HttpServletRequest
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> currencyResult = currencyRepository.findById(currencyId);
		// nếu có tồn tại currency với id đã cho -> update symbol cho nó
		if (currencyResult.isPresent()) {
			Currency currency = currencyResult.get();
			String symbol = currency.getSymbol();
			generalSettingBag.updateCurrencySymbol(symbol);
		}
	}	
	
	// update các field của form mail_server.html
	private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings) {
		for (Setting setting : listSettings) {
			// lấy ra giá trị của field ta muốn update trong form(general.html) thông qua key
			String value = request.getParameter(setting.getKey());
			if (value != null) {
				setting.setValue(value);
			}
		}
		settingService.saveAll(listSettings);
	}
	
}




