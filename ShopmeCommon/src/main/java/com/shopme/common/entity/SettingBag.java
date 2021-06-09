package com.shopme.common.entity;

import java.util.List;

public class SettingBag {
	
	private List<Setting> listSettings;

	/**
	 *  khởi tạo constructor và 4 hàm util gồm:
	 *  getByKey(): nếu list chứa đối tượng Setting với keyword đã cho -> trả về đối tượng với index tương ứng đó
	 *  getValue(): lấy value của đối tượng đó
	 *  update(): update value của đối tượng đó
	 *  listSettings(): lấy ra 1 list đtượng Setting
	 */ 
	
	public SettingBag(List<Setting> lisSettings) {
		this.listSettings = lisSettings;
	}
	
	public Setting getByKey(String key) {
		// ta tìm kiếm vị trí xuất hiện đầu tiên của 1 phần tử với key truyền vào trong listSettings
		// kết quả trả về index xuất hiện đầu tiên của phần tử đó trong listSettings -> lấy ra đối tượng Setting tương ứng với index đó
		int index = listSettings.indexOf(new Setting(key));
		if (index >= 0) {
			return listSettings.get(index);
		}
		return null;
	}
	
	public String getValue(String key) {
		Setting setting = getByKey(key);
		if (setting != null) {
			return setting.getValue();
		}
		return null;
	}
	
	public void update(String key, String value) {
		Setting setting = getByKey(key);
		if (setting != null && value != null) {
			setting.setValue(value);
		}
	}
	
	public List<Setting> listSettings() {
		return listSettings;
	}
	
}


