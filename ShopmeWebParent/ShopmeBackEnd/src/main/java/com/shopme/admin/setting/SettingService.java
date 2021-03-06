package com.shopme.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;

@Service
public class SettingService {
	
	@Autowired
	private SettingRepository settingRepository;
	
	public List<Setting> listAllSettings() {
		return (List<Setting>) settingRepository.findAll();
	}
	
	public GeneralSettingBag getGeneralSettings() {
		List<Setting> settings = new ArrayList<>();
		// lấy ra tất cả Setting có category là GENERAL or CURRENCY trong DB
		// sau đó add chúng vào trong settings
		List<Setting> generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);
		settings.addAll(generalSettings);
		settings.addAll(currencySettings);
		
		return new GeneralSettingBag(settings);
	}
	
	public void saveAll(Iterable<Setting> settings) {
		settingRepository.saveAll(settings);
	}
	
	public List<Setting> getMailServerSettings() {
		return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
	}
	
	public List<Setting> getMailTemplateSettings() {
		return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}
	
	public List<Setting> getCurrencySetting() {
		return settingRepository.findByCategory(SettingCategory.CURRENCY);
	}
	
	public List<Setting> getPaymentSettings() {
		return settingRepository.findByCategory(SettingCategory.PAYMENT);
	}
	
}


