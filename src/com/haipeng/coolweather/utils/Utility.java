package com.haipeng.coolweather.utils;

import java.util.Iterator;

import android.text.TextUtils;

import com.haipeng.coolweather.bean.City;
import com.haipeng.coolweather.bean.County;
import com.haipeng.coolweather.bean.Province;
import com.haipeng.coolweather.dao.CoolWeatherDB;

public class Utility {

	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolweatherDB, String response) {

		if (!TextUtils.isEmpty(response)) {

			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {

					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolweatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;

	}
	
	public synchronized static boolean handleCitiesResponse(
			CoolWeatherDB coolweatherDB, String response,int provinceId) {

		if (!TextUtils.isEmpty(response)) {

			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {

					String[] array = c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolweatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;

	}
	
	public synchronized static boolean handleCountiesResponse(
			CoolWeatherDB coolweatherDB, String response,int cityId) {

		if (!TextUtils.isEmpty(response)) {

			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {

					String[] array = c.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					coolweatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;

	}
	

}
