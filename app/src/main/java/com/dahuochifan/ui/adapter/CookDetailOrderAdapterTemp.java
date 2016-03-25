package com.dahuochifan.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.model.CookBookLevel;
import com.dahuochifan.utils.Tools;
import com.dahuochifan.ui.views.CircleImageView;
import com.nostra13.universalimageloader.utils.L;

public class CookDetailOrderAdapterTemp extends RootAdapter<CookBookLevel>{
	private Activity context;
	private TextView bottom_price_tv;
	private ImageView gwc_view;
	private CircleImageView buyImg;// 这是在界面上跑的小图片
	private ViewGroup anim_mask_layout;// 动画层
	private List<CookBookLevel> listinfo;
	private CookDetailAdapterTemp adapterinfo;
	private TextView price_tv;
	public CookDetailOrderAdapterTemp(Activity context,TextView bottom_price_tv,ImageView gwc_view, TextView price_tv, List<CookBookLevel> listinfo, CookDetailAdapterTemp adapterinfo) {
		super(context);
		this.context=context;
		this.bottom_price_tv=bottom_price_tv;
		this.gwc_view=gwc_view;
		this.price_tv=price_tv;
		this.listinfo=listinfo;
		this.adapterinfo=adapterinfo;
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View v=inflater.inflate(R.layout.item_cookdetail_order, parent,false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.statustv=(TextView)v.findViewById(R.id.statustv);
		viewHolder.pricetv=(TextView)v.findViewById(R.id.pricetv);
		viewHolder.delete_iv=(ImageView)v.findViewById(R.id.delete_iv);
		viewHolder.number_tv=(TextView)v.findViewById(R.id.number_tv);
		viewHolder.add_iv=(ImageView)v.findViewById(R.id.add_iv);
		viewHolder.leveltv=(TextView)v.findViewById(R.id.leveltv);
		v.setTag(viewHolder);
		return v;
	}

	@Override
	protected void bindView(View view, int position,final CookBookLevel object) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		if(object.getStatus().equals("1")){
			viewHolder.statustv.setText("商务私厨");
		}else{
			viewHolder.statustv.setText("邻家私厨");
		}
		viewHolder.pricetv.setText(object.getPrice()+"");
//		viewHolder.number_tv.setText(object.getNumber()+"");
		viewHolder.leveltv.setText(object.getName()+"");
		bottom_price_tv.setText(price_tv.getText().toString());
		viewHolder.add_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentNumber =Tools.toInteger(viewHolder.number_tv.getText().toString());
				double curprice= Tools.toDouble(bottom_price_tv.getText().toString());
				bottom_price_tv.setText(curprice+object.getPrice()+"");
				price_tv.setText(curprice+object.getPrice()+"");
				if(BuildConfig.LEO_DEBUG)L.e("==========>>>>>"+price_tv.getText().toString());
				currentNumber++;
				viewHolder.number_tv.setText(currentNumber+"");
				//initAnimation(viewHolder.number_tv);
//				object.setNumber(currentNumber);
				adapterinfo.notifyDataSetChanged();
			}
		});
		viewHolder.delete_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentNumber = Tools.toInteger(viewHolder.number_tv.getText().toString());
				if (currentNumber >= 1) {
					currentNumber--;
					double curprice=Tools.toDouble(bottom_price_tv.getText().toString());
					bottom_price_tv.setText(curprice-object.getPrice()+"");
					price_tv.setText(curprice-object.getPrice()+"");
					viewHolder.number_tv.setText(currentNumber+"");
//					object.setNumber(currentNumber);
					adapterinfo.notifyDataSetChanged();
				}else{
					
				}
			}
		});
	}
	public class ViewHolder{
		private TextView statustv;
		private TextView pricetv;
		private ImageView delete_iv;
		private TextView number_tv;
		private ImageView add_iv;
		private TextView leveltv;
	}
	
	private void initAnimation(TextView number_tv) {
		int[] start_location = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
		number_tv.getLocationInWindow(start_location);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
		buyImg=new CircleImageView(context);
		buyImg.setBackgroundResource(R.drawable.deep_dot2);
		setAnim(buyImg, start_location);// 开始执行动画
	}

	private void setAnim(final View v, int[] start_location) {
		anim_mask_layout = null;
		anim_mask_layout = createAnimLayout();
		anim_mask_layout.addView(v);// 把动画小球添加到动画层
		final View view = addViewToAnimLayout(anim_mask_layout, v,
				start_location);
		int[] end_location = new int[2];// 这是用来存储动画结束位置的X、Y坐标
		gwc_view.getLocationInWindow(end_location);// shopCart是那个购物车
		
		// 计算位移
		//int endX = 0 - start_location[0] + 40;// 动画位移的X坐标
		int endX=end_location[0]-start_location[0];
		int endY = end_location[1] - start_location[1];// 动画位移的y坐标
		TranslateAnimation translateAnimationX = new TranslateAnimation(0,
				endX, 0, 0);
		translateAnimationX.setInterpolator(new LinearInterpolator());
		translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);

		TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
				0, endY);
		translateAnimationY.setInterpolator(new AccelerateInterpolator());
		translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);

		AnimationSet set = new AnimationSet(false);
		set.setFillAfter(false);
		set.addAnimation(translateAnimationY);
		set.addAnimation(translateAnimationX);
		set.setDuration(800);// 动画的执行时间
		view.startAnimation(set);
		// 动画监听事件
		set.setAnimationListener(new AnimationListener() {
			// 动画的开始
			@Override
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			// 动画的结束
			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
				//buyNum++;// 让购买数量加1
				// buyNumView.setText(buyNum + "");//
				// buyNumView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
				// buyNumView.show();
			}
		});
	}
	/**
	 * @Description: 创建动画层
	 * @param
	 * @return void
	 * @throws
	 */
	private ViewGroup createAnimLayout() {
		ViewGroup rootView = (ViewGroup) context.getWindow().getDecorView();
		LinearLayout animLayout = new LinearLayout(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		animLayout.setLayoutParams(lp);
		animLayout.setId(Integer.MAX_VALUE);
		animLayout.setBackgroundResource(android.R.color.transparent);
		rootView.addView(animLayout);
		return animLayout;
	}
	private View addViewToAnimLayout(final ViewGroup vg, final View view,
			int[] location) {
		int x = location[0];
		int y = location[1];
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = x;
		lp.topMargin = y;
		view.setLayoutParams(lp);
		return view;
	}

}
