package com.dahuochifan.ui.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.ui.activity.CommentsDetailActivity;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.dao.DaoMaster;
import com.dahuochifan.dao.DaoMaster.DevOpenHelper;
import com.dahuochifan.dao.DaoSession;
import com.dahuochifan.dao.Gwc;
import com.dahuochifan.dao.GwcDao;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.model.CookBookLevel;
import com.dahuochifan.utils.Tools;
import com.dahuochifan.ui.views.CircleImageView;
import com.nostra13.universalimageloader.utils.L;

public class CookDetailAdapter extends BaseAdapter {
	private final Activity context;
	private LayoutInflater inflater;
	private List<CookBookLevel> list;
	private ImageView gwc_view;
	
	private CircleImageView buyImg;// 这是在界面上跑的小图片
	private ViewGroup anim_mask_layout;// 动画层
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private GwcDao gwcDao;
	private ChefList chefList;
	private Calendar ca;
	private String date;
	private TextView bottom_price_tv;
	private List<Gwc> numberlist=new ArrayList<Gwc>();
	public CookDetailAdapter(Activity activity, List<CookBookLevel> list, ImageView gwc_view, ChefList chefList, TextView bottom_price_tv,List<Gwc> numberlist) {
		super();
		this.context = activity;
		this.list = list;
		inflater = LayoutInflater.from(context);
		this.gwc_view=gwc_view;
		DevOpenHelper helper = new DevOpenHelper(activity, "dhcf-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = (DaoSession) daoMaster.newSession();
		gwcDao = daoSession.getGwcDao();
		this.chefList=chefList;
		this.bottom_price_tv=bottom_price_tv;
		ca=Calendar.getInstance();
		date=ca.get(ca.YEAR)+"-"+ca.get(ca.MONTH)+"-"+ca.get(ca.DATE);
		List<Gwc> templist=gwcDao.queryRaw("where mid = ?",MyConstant.user.getUserids());
		if(templist!=null&&templist.size()>0){
			for(int i=0;i<templist.size();i++){
				Gwc gwc=templist.get(i);
				if(!gwc.getDate().equals(date)){
					gwcDao.delete(gwc);
				}
			}
		}
		this.numberlist=numberlist;
		getTmepObj();
	}


	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = inflater.inflate(R.layout.item_cook_detail, parent, false);
			holder = new ViewHolder();
			holder.number_tv=(TextView)convertView.findViewById(R.id.number_tv);
			holder.add_iv=(ImageView)convertView.findViewById(R.id.add_iv);
			holder.delete_iv=(ImageView)convertView.findViewById(R.id.delete_iv);
			holder.comments_tv=(TextView)convertView.findViewById(R.id.comments_tv);
			holder.price_tv=(TextView)convertView.findViewById(R.id.price_tv);
			holder.level_tv=(TextView)convertView.findViewById(R.id.level_tv);
			convertView.setTag(holder);
		}
//		numberlist=gwcDao.queryRaw("where cbids=? and mid=? and mydate=?", new String[]{list.get(position).getCbids(),MyConstant.user.getUserids(),date});
		if(numberlist!=null&&numberlist.size()>0){
			if(numberlist.get(0).getNumber()>0){
				if(list.get(position).getCbids().equals(numberlist.get(0).getCbids())){
					if(BuildConfig.LEO_DEBUG)L.e(numberlist.size()+"numberlist.size()========"+numberlist.get(0).getNumber()+"position====="+position);
					holder.number_tv.setText(numberlist.get(0).getNumber()+"");
				}
			}else{
				holder.number_tv.setText("0");
			}
		}else{
			holder.number_tv.setText("0");
		}
		holder.level_tv.setText(list.get(position).getName());
		holder.comments_tv.setText(list.get(position).getCommentnum() + "评论");
		holder.price_tv.setText(list.get(position).getPrice()+"");

		holder.add_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentNumber = Tools.toInteger(holder.number_tv.getText().toString());
				double curprice=Tools.toDouble(bottom_price_tv.getText().toString());
				bottom_price_tv.setText(curprice+list.get(position).getPrice()+"");
				currentNumber++;
				holder.number_tv.setText(currentNumber+"");
				int[] start_location = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
				holder.number_tv.getLocationInWindow(start_location);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
				buyImg=new CircleImageView(context);
//				buyImg.setImageBitmap(getAddDrawBitMap(position));// 设置buyImg的图片
				buyImg.setBackgroundResource(R.drawable.deep_dot2);
				setAnim(buyImg, start_location);// 开始执行动画
				List<Gwc> templist=gwcDao.queryRaw("where cbids=? and mid=? and mydate=?", new String[]{list.get(position).getCbids(),MyConstant.user.getUserids(),date});
				if(templist!=null&&templist.size()>0){
					Gwc gwc=new Gwc(templist.get(0).getId(), list.get(position).getCbids(), MyConstant.user.getUserids(),date,list.get(position).getStatus(), chefList.getAddressinfo(), "111", chefList.getNickname(), list.get(position).getName(), chefList.getAvatar(), list.get(position).getPrice(), 10, currentNumber,chefList.getChefids());
					gwcDao.insertOrReplace(gwc);
				}else{
					Gwc gwc=new Gwc(null, list.get(position).getCbids(), MyConstant.user.getUserids(),date,list.get(position).getStatus(), chefList.getAddressinfo(), "111", chefList.getNickname(), list.get(position).getName(), chefList.getAvatar(), list.get(position).getPrice(), 10, currentNumber,chefList.getChefids());
					gwcDao.insertOrReplace(gwc);
				}
			}
		});
		holder.comments_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				context.startActivity(new Intent(context,CommentsDetailActivity.class));
			}
		});
		holder.delete_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentNumber = Tools.toInteger(holder.number_tv.getText().toString());
				if (currentNumber > 1) {
					currentNumber--;
					double curprice=Tools.toDouble(bottom_price_tv.getText().toString());
					bottom_price_tv.setText(curprice-list.get(position).getPrice()+"");
					holder.number_tv.setText(currentNumber+"");
					Log.e("cid", list.get(position).getCbids()+"");
					List<Gwc> templist=gwcDao.queryRaw("where cbids=? and mid=? and mydate=?", new String[]{list.get(position).getCbids(),MyConstant.user.getUserids(),date});
					if(templist!=null&&templist.size()>0){
						Gwc gwc=new Gwc(templist.get(0).getId(), list.get(position).getCbids(), MyConstant.user.getUserids(),date,list.get(position).getStatus(), chefList.getAddressinfo(), "111", chefList.getNickname(), list.get(position).getName(), chefList.getAvatar(), list.get(position).getPrice(), 10, currentNumber,chefList.getChefids());
						gwcDao.insertOrReplace(gwc);
					}else{
						Gwc gwc=new Gwc(null, list.get(position).getCbids(), MyConstant.user.getUserids(),date,list.get(position).getStatus(), chefList.getAddressinfo(), "111", chefList.getNickname(), list.get(position).getName(), chefList.getAvatar(), list.get(position).getPrice(), 10, currentNumber,chefList.getChefids());
						gwcDao.insertOrReplace(gwc);
					}
				}else if(currentNumber==1){
					currentNumber--;
					holder.number_tv.setText(currentNumber+"");
					double curprice=Tools.toDouble(bottom_price_tv.getText().toString());
					bottom_price_tv.setText(curprice-list.get(position).getPrice()+"");
					List<Gwc> templist=gwcDao.queryRaw("where cbids = ? and mid = ? and mydate = ?", new String[]{list.get(position).getCbids(),MyConstant.user.getUserids(),date});
					for(int i=0;i<templist.size();i++){
						gwcDao.delete(templist.get(i));
					}
				}else{
					
				}
			}
		});
		holder.comments_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,CommentsDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("cbids",list.get(position).getCbids());
				bundle.putString("nickname",chefList.getNickname());
				bundle.putString("naviland", "非常" +chefList.getHomeprov().replace("省", "").replace("市", "")+"/"+chefList.getHomecity().replace("新区", "").replace("市", "").replace("区", ""));
				bundle.putString("avatar", chefList.getAvatar());
				bundle.putString("status", list.get(position).getStatus());
				bundle.putString("name", list.get(position).getName());
				bundle.putString("comments", list.get(position).getCommentnum()+"");
				bundle.putString("price", list.get(position).getPrice()+"");
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
	public Bitmap getAddDrawBitMap(int position) {
		Tools tools = new Tools();
	    View drawableViewPar = LayoutInflater.from(context).inflate(R.layout.item_cook_detail, null);
	    TextView text = (TextView) drawableViewPar.findViewById(R.id.number_tv);
	    text.setText("  ");
	    return tools.convertViewToBitmap(text);
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
	public void getTmepObj(){
		int price=0;
		for(int i=0;i<numberlist.size();i++){
			price+=numberlist.get(i).getPrice()*numberlist.get(i).getNumber();
			bottom_price_tv.setText(price+"");
		}
		if(numberlist.size()==0){
			bottom_price_tv.setText(0.0+"");
		}
	}
	
	public List<Gwc> getNumberlist() {
		return numberlist;
	}


	public void setNumberlist(List<Gwc> numberlist) {
		this.numberlist = numberlist;
	}
	

	public List<CookBookLevel> getList() {
		return list;
	}


	public void setList(List<CookBookLevel> list) {
		this.list = list;
	}


	class ViewHolder {
		private TextView number_tv;
		private ImageView add_iv;
		private ImageView delete_iv;
		private TextView level_tv;
		private TextView comments_tv;
		private TextView price_tv;

	}

}
