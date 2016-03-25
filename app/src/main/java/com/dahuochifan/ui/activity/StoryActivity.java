package com.dahuochifan.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
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

public class StoryActivity extends BaseActivity {
	private EditText et;
	private RippleView submit_tv;
	private MyAsyncHttpClient client;
	private RequestParams param;
	private Gson gson;
	private ProgressDialog pd;
	private boolean canPost;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1 :
					finish();
					break;

				default :
					break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pd = new ProgressDialog(this);
		canPost = true;
		initViews();
		initData();
		listener();
	}
	private void initData() {
		param = ParamData.getInstance().getStoryContentObj();
		client.post(MyConstant.URL_GET_STORY, param, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				pd.setMessage("正在获取主厨故事");
				pd.show();
				canPost = false;
				super.onStart();
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				try {
					JSONObject jobj=new JSONObject(arg2);
					int resultcode=jobj.getInt("resultcode");
					String obj=jobj.getString("obj");
					if(resultcode==1&&obj!=null){
						et.setText(obj);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
			}
			@Override
			public void onFinish() {
				pd.dismiss();
				canPost = true;
				super.onFinish();
			}
		});
	}
	private void initViews() {
		et = (EditText) findViewById(R.id.feedback_content);
		submit_tv = (RippleView) findViewById(R.id.feedback_submit);
		client = new MyAsyncHttpClient();
		param = new RequestParams();
		gson = new Gson();
	}
	private void listener() {
		submit_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (canPost) {
					String feedstr = et.getText().toString();
					if (TextUtils.isEmpty(feedstr)) {
						MainTools.ShowToast(StoryActivity.this, "内容不能为空");
						return;
					} else {
						postStory(feedstr);
					}
				} else {
					MainTools.ShowToast(StoryActivity.this, "请稍等再试");
				}
			}
		});
	}
	private void postStory(String feedstr) {
		param = ParamData.getInstance().getStoryObj(feedstr);
		client.setTimeout(5000);
		client.post(MyConstant.URL_CHEF_EDIT, param, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				pd.setMessage("正在提交主厨故事");
				pd.show();
				super.onStart();
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				try {
					JSONObject jobj = new JSONObject(arg2);
					int resultcode = jobj.getInt("resultcode");
					String tag = jobj.getString("tag");
					MainTools.ShowToast(StoryActivity.this, tag);
					if (resultcode == 1) {
						handler.sendEmptyMessageDelayed(1, 1000);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				MainTools.ShowToast(StoryActivity.this, R.string.interneterror);
			}
			@Override
			public void onFinish() {
				pd.dismiss();
				super.onFinish();
			}
		});
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_story;
	}

	@Override
	protected String initToolbarTitle() {
		return "主厨故事";
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
