package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.utils.CookerHead;

public class CookStoryActivity extends BaseActivity {

	private String avatar, numberStr, nickname, story, distance;
	private Float score;
	private CircleImageView chef_head;
	private TextView name_tv;
	private TextView content_tv;
	private TextView number_tv;
	private RatingBar myratingbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		initData();
		initViews();
		btn_listener();
	}
	private void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		avatar = bundle.getString("avatar");
		numberStr=bundle.getString("commentnum");
		score=bundle.getFloat("score");
		nickname = bundle.getString("nickname");
		story = bundle.getString("story");
	}
	private void initViews() {
		chef_head = (CircleImageView) findViewById(R.id.chef_head_iv);
		name_tv = (TextView) findViewById(R.id.chef_nickname_tv);
		content_tv = (TextView) findViewById(R.id.chef_content_tv);
		myratingbar=(RatingBar)findViewById(R.id.myratingbar);
		number_tv=(TextView)findViewById(R.id.chef_comment);
		// -----------界面数据-----------------
		CookerHead.loadImage(avatar, chef_head);
		name_tv.setText(nickname);
		myratingbar.setRating(score);
		number_tv.setText(numberStr+"评论");
		if(!TextUtils.isEmpty(story)){
			content_tv.setText(story);
		}

	}
	private void btn_listener() {
		chef_head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (!TextUtils.isEmpty(avatar)) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent(CookStoryActivity.this, SimpleViewActivity.class);
							int[] startLocation = new int[2];
							v.getLocationOnScreen(startLocation);
							startLocation[0] += v.getMeasuredWidth() / 2;
							intent.putExtra("imgpath", avatar);
							intent.putExtra("location", startLocation);
							startActivity(intent);
							overridePendingTransition(0, 0);
						}
					}, 200);
				}
			}
		});
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_cookstory;
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
