package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.ui.fragment.MyPayAllFragment;
import com.dahuochifan.ui.fragment.MyPayingFragment;
import com.dahuochifan.utils.Tools;
import com.dahuochifan.ui.view.indicator.FragmentListPageAdapter;
import com.dahuochifan.ui.view.indicator.IndicatorViewPager;
import com.dahuochifan.ui.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.dahuochifan.ui.view.indicator.ScrollIndicatorView;
import com.dahuochifan.ui.view.indicator.slidebar.ColorBar;
import com.dahuochifan.ui.view.indicator.transition.OnTransitionTextListener;
import com.dahuochifan.ui.views.MyViewPager;

public class PayTabActivity extends AppCompatActivity{
	
	private String[] names = {"全部账单", "进行中"};
	private MyViewPager viewpager;
	private ScrollIndicatorView indicatorview;
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	private int curIndex;
	private RelativeLayout back_rl;
	private TextView title_tv;
	
	private MyPayAllFragment payAllfragment;
	private MyPayingFragment payingfragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paytab);
		AppManager.getAppManager().addActivity(this);
		initViews();
		btn_listener();
	}
	private void initViews() {
		back_rl = (RelativeLayout) findViewById(R.id.back_rl);
		viewpager = (MyViewPager) findViewById(R.id.metab_viewPager);
		indicatorview = (ScrollIndicatorView) findViewById(R.id.metab_indicator);
		title_tv=(TextView)findViewById(R.id.title_tv);
		title_tv.setText("详情");
		int height = Tools.px2dip(PayTabActivity.this, getResources().getDimension(R.dimen.width_1_80));
		indicatorview.setScrollBar(new ColorBar(this, ContextCompat.getColor(PayTabActivity.this,R.color.maincolor_new), height));
		// 0xffe95504
		// 设置滚动监听
		int selectColorId = R.color.tab_top_text_select;
		int unSelectColorId = R.color.tab_top_text_unselect;
		indicatorview.setOnTransitionListener(new OnTransitionTextListener().setColorId(this, selectColorId, unSelectColorId));
		viewpager.setOffscreenPageLimit(3);
		indicatorViewPager = new IndicatorViewPager(indicatorview, viewpager);
		inflate = LayoutInflater.from(getApplicationContext());
		indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager() ));
		indicatorViewPager.setPageCanScroll(true);
		viewpager.setCurrentItem(curIndex);
	}
	private void btn_listener() {
		back_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	private class MyAdapter extends IndicatorFragmentPagerAdapter {

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.tab_top, container, false);
			}
			TextView textView = (TextView) convertView;
			textView.setText(names[position % names.length]);
			textView.setPadding(5, 0, 5, 0);
			return convertView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			if(position==0){
				if(payAllfragment==null)
					payAllfragment=new MyPayAllFragment();
				return payAllfragment;
			}else {
				if(payingfragment==null)
					payingfragment =new MyPayingFragment();
				return payingfragment;
			}
		}
		
		@Override
		public int getItemPosition(Object object) {
			return FragmentListPageAdapter.POSITION_NONE;
		}

	};
}
