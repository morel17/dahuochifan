package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.core.model.order.OrderInfo;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.ui.views.CircleImageView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class CommentsActivity extends BaseActivity {
	private RatingBar myratingbar;
	private EditText comments_et;
	private TextView comments_tv;
	private OrderInfo obj;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private boolean canCom;
	private SweetAlertDialog pDialog;
	private Handler mHander = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0 :
					pDialog.dismissWithAnimation();
					break;
				case 1 :
					pDialog.dismissWithAnimation();
					finish();
					break;
				case 2 :

					break;
				default :
					break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		initData();
		initViews();
		listener();
	}
	private void initData() {
		obj = (OrderInfo) getIntent().getSerializableExtra("obj");
		client = new MyAsyncHttpClient();
		params = new RequestParams();
		canCom = true;
	}
	private void initViews() {
		TextView chef_nickname_tv = (TextView) findViewById(R.id.chef_nickname_tv);
		TextView chef_naviland_tv = (TextView) findViewById(R.id.chef_naviland_tv);
		CircleImageView chef_avatar_iv = (CircleImageView) findViewById(R.id.chef_avatar_iv);
		TextView level_tv = (TextView) findViewById(R.id.level_tv);
		myratingbar = (RatingBar) findViewById(R.id.myratingbar);
		myratingbar.setRating(5.0f);
		comments_et = (EditText) findViewById(R.id.comments_et);
		comments_tv = (TextView) findViewById(R.id.comments_tv);
		chef_nickname_tv.setText(obj.getNickname());
		if(!TextUtils.isEmpty(obj.getTag())){
			chef_naviland_tv.setText(obj.getTag());
		}else{
			chef_naviland_tv.setText("");
		}
		CookerHead.loadImage(obj.getAvatar(), chef_avatar_iv);
		level_tv.setText(obj.getCbname());
	}
	private void listener() {
		comments_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (canCom) {
					String comment_str = comments_et.getText().toString();
					if (TextUtils.isEmpty(comment_str)) {
						MainTools.ShowToast(CommentsActivity.this, "内容不能为空");
						return;
					}
					postMain(comment_str.replaceAll(" ", ""));
				} else {
					MainTools.ShowToast(CommentsActivity.this, "请勿重复评论");
				}
			}
		});

	}
	private void postMain(String comment_str) {
		params = ParamData.getInstance().postCommentObj(obj.getCbids(), comment_str, myratingbar.getRating() * 2 + "", obj.getOlids());
		client.post(MyConstant.URL_POSTCOMMENT, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				canCom = false;
				pDialog = new SweetAlertDialog(CommentsActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在提交评论");
				pDialog.setCancelable(false);
				pDialog.getProgressHelper().setBarColor(ContextCompat.getColor(CommentsActivity.this,R.color.blue_btn_bg_color));
				pDialog.show();
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				try {
					JSONObject jobj = new JSONObject(responseString);
					int resultcode = jobj.getInt("resultcode");
					String tag = jobj.getString("tag");
					if (resultcode == 1) {
						if (OrderDetailActivity.instance != null) {
							OrderDetailActivity.instance.finish();
						}
						pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
						EventBus.getDefault().post(new FirstEvent("MyOrder"));
						EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_TWO));
						EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_THREE));
						mHander.sendEmptyMessageDelayed(1, 1500);
					}else{
						if(!TextUtils.isEmpty(tag)){
							showTipDialog(CommentsActivity.this,tag,resultcode);
						}else{
							showTipDialog(CommentsActivity.this,"重新登录",resultcode);
						}
						pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
						pDialog.dismissWithAnimation();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				pDialog.setTitleText("网络不给力").setConfirmText("确定").changeAlertType(SweetAlertDialog.ERROR_TYPE);
				mHander.sendEmptyMessageDelayed(0, 1500);
			}
			@Override
			public void onFinish() {
				super.onFinish();
				canCom = true;
			}
		});
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_comments;
	}

	@Override
	protected String initToolbarTitle() {
		return "评论";
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
