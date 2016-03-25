package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.views.ripple.RippleView;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FeedBackActivity extends BaseActivity{
	private EditText et;
	private RippleView submit_tv;
	private MyAsyncHttpClient client;
	private RequestParams param;
	private Gson gson;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		initViews();
		listener();
	}
	private void initViews() {
		et=(EditText)findViewById(R.id.feedback_content);
		submit_tv=(RippleView)findViewById(R.id.feedback_submit);
		client=new MyAsyncHttpClient();
		param=new RequestParams();
		gson=new Gson();
	}
	private void listener() {
		submit_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String feedstr=et.getText().toString();
				if(TextUtils.isEmpty(feedstr)){
					MainTools.ShowToast(FeedBackActivity.this, "内容不能为空");
					return;
				}else{
					param=ParamData.getInstance().getFeedbackObj(feedstr);
					client.post(MyConstant.URL_FEEDBACK, param,new TextHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers, String responseString) {
							try {
								JSONObject jobj=new JSONObject(responseString);
								int result=jobj.getInt("resultcode");
								String tag=jobj.getString("tag");
								if(result==1){
									MainTools.ShowToast(FeedBackActivity.this, tag);
									finish();
								}else{
									if(!TextUtils.isEmpty(tag)){
										showTipDialog(FeedBackActivity.this,tag,result);
									}else{
										showTipDialog(FeedBackActivity.this,"重新登录",result);
									}

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						@Override
						public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
							MainTools.ShowToast(FeedBackActivity.this, R.string.interneterror);
						}
					});
				}
			}
		});
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_feedback;
	}

	@Override
	protected String initToolbarTitle() {
		return "意见反馈";
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				finish();
			default :
				return super.onOptionsItemSelected(item);
		}
		
	}
}
