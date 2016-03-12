package com.haipeng.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.haipeng.coolweather.R;
import com.haipeng.coolweather.bean.City;
import com.haipeng.coolweather.bean.County;
import com.haipeng.coolweather.bean.Province;
import com.haipeng.coolweather.dao.CoolWeatherDB;
import com.haipeng.coolweather.utils.HttpCallbackListener;
import com.haipeng.coolweather.utils.HttpUtil;
import com.haipeng.coolweather.utils.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	protected static final int LEVEL_PROVINCE = 0;
	protected static final int LEVEL_CITY = 1;
	protected static final int LEVEL_COUNTY = 2;
	private ListView lvList;
	private TextView tvTitle;
	private List<String> datalist = new ArrayList<String>();
	private ArrayAdapter<String> arrayAdapter;
	private int currentLevel;
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private Province selectedProvince;
	protected City selectedCity;
	private CoolWeatherDB coolWeatherDB;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		lvList = (ListView) findViewById(R.id.lv_list);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, datalist);

		lvList.setAdapter(arrayAdapter);

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {

					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {

					selectedCity = cityList.get(position);
					queryCounties();
				}
			}
		});
		queryProvinces();
	}

	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {

			datalist.clear();
			for (Province province : provinceList) {

				datalist.add(province.getProvinceName());
			}
			arrayAdapter.notifyDataSetChanged();
			lvList.setSelection(0);
			tvTitle.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {

			datalist.clear();
			for (City city : cityList) {

				datalist.add(city.getCityName());
			}
			arrayAdapter.notifyDataSetChanged();
			lvList.setSelection(0);
			tvTitle.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {

			datalist.clear();
			for (County county : countyList) {

				datalist.add(county.getCountyName());
			}
			arrayAdapter.notifyDataSetChanged();
			lvList.setSelection(0);
			tvTitle.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "city");
		}
	}

	private void queryFromServer(final String code, final String type) {

		String address;
		if (!TextUtils.isEmpty(code)) {

			address = "http://www.baidu.com";
		} else {

			address = "http://www.baidu.com";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {

				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);

				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}

						}

					});
				}
			}

			@Override
			public void onError(Exception e) {

				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {

		if(progressDialog==null){
			
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void closeProgressDialog() {

		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	@Override
	public void onBackPressed() {
		if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel==LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
}
