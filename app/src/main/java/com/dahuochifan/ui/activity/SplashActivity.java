package com.dahuochifan.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.MainPicAll;
import com.dahuochifan.model.wechat.MainPicObj;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.MainPicData;
import com.dahuochifan.ui.adapter.SplashAdapter;
import com.dahuochifan.ui.view.indicator.CirclePageIndicator;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SystemBarTintManager;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SplashActivity extends AppCompatActivity  {
	@Bind(R.id.pager)
	ViewPager viewpager;
	@Bind(R.id.indicator)
	CirclePageIndicator indicator;
	private LayoutInflater inflate;
	private float downx, upx;
	private SplashAdapter adapter;
	private ArrayList<String> ImgList;
	private int curPage;
	private int preState;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		initTopViews();
		inflate = LayoutInflater.from(getApplicationContext());
		ButterKnife.bind(this);
		client=new MyAsyncHttpClient();
		params=new RequestParams();
		gson=new Gson();
		getPic();
		initViewPager();
	}

	private void getPic() {
		params= ParamData.getInstance().getPicObj();
		client.post(MyConstant.APP_MAIN_PIC, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				if (ImgList == null) {
					ImgList = new ArrayList<>();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				MainTools.ShowToast(SplashActivity.this, "图片加载失败");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				MainPicData data = new MainPicData();
				if (data.dealData(responseString, SplashActivity.this, gson) == 1) {
					MainPicAll picAll = data.getAll();
					List<MainPicObj> list = picAll.getList();
					int w = getResources().getDimensionPixelOffset(R.dimen.width_80_80) / 10 * 8;
					int h = getResources().getDimensionPixelOffset(R.dimen.height_80_80) / 10 * 8;
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							String imgUrl = list.get(i).getUrl() + "?imageView2/1/w/" + w + "/h/"
									+ h + "/q/" + MyConstant.QUALITY;
							ImgList.add(i, imgUrl);
						}
						initViewPager();
					}
				}
			}
		});
	}

	private void initViewPager() {
		adapter = new SplashAdapter(getSupportFragmentManager());
		adapter.addAll(ImgList);
		viewpager.setAdapter(adapter);
		// indicator.setIndicatorType(CirclePageIndicator.IndicatorType.FRACTION); //数字显示进度如 2/6
		indicator.setViewPager(viewpager);
		indicator.setIndicatorType(CirclePageIndicator.IndicatorType.CIRCLE);
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				indicator.updateIndicator(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				curPage = arg0;
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (preState == 1 && arg0 == 0 && curPage == ImgList.size() - 1) {
					finish();
					overridePendingTransition(0, R.anim.dahuo_left_out);
				}
				preState = arg0;
			}
		});
	}
	public void initTopViews() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintColor(getResources().getColor(android.R.color.transparent));
			tintManager.setStatusBarTintEnabled(true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
	}
}
