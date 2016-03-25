package com.dahuochifan.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.ui.adapter.MainGvAdapter2;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.CookBookLevel;
import com.dahuochifan.model.TagObj;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.TagData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class CookTagActivity extends BaseActivity{
	private GridView main_gv;
	private Context context;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;
	private SharedPreferences spf;
	private Editor eidtor;
	private TextView confirm_tv;
	private MainGvAdapter2 adapter;
	private List<TagObj> taglist = new ArrayList<TagObj>();
	private List<String> list=new ArrayList<String>();
	private CookBookLevel object;
	private SwipeRefreshLayout swipe;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		initData();
		initViews();
		getData();
		confirm_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				list.clear();
				for(int i=0;i<taglist.size();i++){
					if(taglist.get(i).isIsselect()){
						list.add(taglist.get(i).getName());
					}
				}
				EventBus.getDefault().post(new FirstEvent("cooktag",list));
				finish();
			}
		});
	}
	private void initData() {
		object=(CookBookLevel) getIntent().getExtras().getSerializable("obj");
	}
	private void initViews() {
		client=new MyAsyncHttpClient();
		params=new RequestParams();
		gson=new Gson();
		main_gv = (GridView) findViewById(R.id.main_gv);
		swipe=(SwipeRefreshLayout)findViewById(R.id.swipe);
		swipe.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		confirm_tv=(TextView)findViewById(R.id.confirm_tv);
		swipe.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getData();
			}
		});
	}
	private void getData() {
		params = ParamData.getInstance().postTagObj("");
		client.post(MyConstant.URL_CBTAG_LIST, params, new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				TagData data = new TagData();
				if (data.dealData(arg2, CookTagActivity.this, gson) == 1) {
					if (data.getAll().getList() != null && data.getAll().getList().size() > 0) {
						taglist = data.getAll().getList();
						String[] tags=object.getTags().split(",");
						for(int i=0;i<tags.length;i++){
							list.add(tags[i]);
						}
						adapter=new MainGvAdapter2(context, list);
						main_gv.setAdapter(adapter);
						adapter.addAll(taglist);
						adapter.notifyDataSetChanged();
					}
				} else {
					if(data.getAll()!=null&&!TextUtils.isEmpty(data.getAll().getTag())){
						showTipDialog(CookTagActivity.this,data.getAll().getTag(),data.getAll().getResultcode());
					}else{
						showTipDialog(CookTagActivity.this,"重新登录",data.getAll().getResultcode());
					}

				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				MainTools.ShowToast(context, R.string.interneterror);
			}
			@Override
			public void onFinish() {
				super.onFinish();
				swipe.setRefreshing(false);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			new SweetAlertDialog(CookTagActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("未确认修改是否退出").setCancelText("取消")
			.setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
				@Override
				public void onClick(SweetAlertDialog sDialog) {
					sDialog.dismiss();
				}
			}).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
				@Override
				public void onClick(SweetAlertDialog sDialog) {
					sDialog.dismiss();
					finish();
				}
			}).show();
		}
		return false;
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_leftmaingrid_cuisine;
	}

	@Override
	protected String initToolbarTitle() {
		return "修改菜系";
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				new SweetAlertDialog(CookTagActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("未确认修改是否退出").setCancelText("取消")
				.setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sDialog) {
						sDialog.dismiss();
					}
				}).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sDialog) {
						sDialog.dismiss();
						finish();
					}
				}).show();
			default :
				return super.onOptionsItemSelected(item);
		}
		
	}
}
