package com.dahuochifan.ui.adapter;

import java.util.List;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.ui.activity.CommentsDetailActivity;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.model.CookBookLevel;
import com.dahuochifan.utils.Arith;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.ui.views.popwindow.TagPopWindow;
import com.dahuochifan.ui.views.popwindow.TagPopWindow.MorelPopListener;

import static java.lang.Integer.MAX_VALUE;

public class CookDetailAdapterTemp extends RootAdapter<CookBookLevel> {
	private TextView bottom_price_tv;
	private AppCompatActivity context;
	private CircleImageView buyImg;// 这是在界面上跑的小图片
	private ViewGroup anim_mask_layout;// 动画层
	private ChefList chefList;
	private List<CookBookLevel> list;
	private TagPopWindow popWindow;
	private int discount;
	private double limitPrice;
	private int eatennum;
	private TextView yh_tv;
	public CookDetailAdapterTemp(AppCompatActivity context, TextView bottom_price_tv, ChefList chefList, List<CookBookLevel> list, TextView yh_tv) {
		super(context);
		this.context = context;
		this.bottom_price_tv = bottom_price_tv;
		this.chefList = chefList;
		this.list = list;
		this.yh_tv = yh_tv;
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.item_cook_detail2, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.add_rl = (RelativeLayout) v.findViewById(R.id.add_rl);
		viewHolder.add_rl2 = (RelativeLayout) v.findViewById(R.id.add_rl2);
		viewHolder.delete_rl = (RelativeLayout) v.findViewById(R.id.delete_rl);
		viewHolder.delete_rl2 = (RelativeLayout) v.findViewById(R.id.delete_rl2);
		viewHolder.comments_tv = (TextView) v.findViewById(R.id.comments_tv);
		viewHolder.price_tv = (TextView) v.findViewById(R.id.price_tv);
		viewHolder.level_tv = (TextView) v.findViewById(R.id.level_tv);
		viewHolder.person_tv = (TextView) v.findViewById(R.id.person_tv);
		viewHolder.ratingbar = (RatingBar) v.findViewById(R.id.myratingbar);
		viewHolder.tag_iv = (ImageView) v.findViewById(R.id.tag_iv);
		viewHolder.tag_tv = (TextView) v.findViewById(R.id.tag_tv);
		viewHolder.top_gray = (TextView) v.findViewById(R.id.top_gray);
		viewHolder.province_tv = (TextView) v.findViewById(R.id.province_tv);
		viewHolder.tag_rl = (RelativeLayout) v.findViewById(R.id.tag_rl);
		viewHolder.today_relay_tv = (TextView) v.findViewById(R.id.today_relay_tv);
		viewHolder.tomorrow_relay_tv = (TextView) v.findViewById(R.id.tomorrow_relay_tv);
		v.setTag(viewHolder);
		return v;
	}

	@Override
	protected void bindView(View view, int position, final CookBookLevel object) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		viewHolder.today_relay_tv.setText("今日剩" + object.getToday_num() + "份");
		viewHolder.tomorrow_relay_tv.setText("明日剩" + object.getTomorrow_num() + "份");
		viewHolder.person_tv.setText(object.getMynumber() + "");
		viewHolder.price_tv.setText((int) object.getMyprice() + "");
		viewHolder.level_tv.setText(object.getName());
		viewHolder.comments_tv.setText(object.getCommentnum() + "评论");
		if (object.getCommentnum() != 0) {
			viewHolder.ratingbar.setRating(object.getTotalscore() / object.getCommentnum() * 0.5f);
		} else {
			viewHolder.ratingbar.setRating(0f);
		}
		String tags_str = object.getTags();
		final String[] tag = tags_str.replace("非常", "").split(",");
		if (!object.isIsopen()) {
			viewHolder.top_gray.setVisibility(View.VISIBLE);
		} else {
			viewHolder.top_gray.setVisibility(View.GONE);
		}
		if (tag.length > 1) {
			viewHolder.tag_iv.setVisibility(View.VISIBLE);
		} else {
			viewHolder.tag_iv.setVisibility(View.GONE);
		}
		if (tag.length >= 1) {
			// viewHolder.tag_tv.setText(tag[0]);
			viewHolder.province_tv.setText(tag[0]);
		}
		viewHolder.tag_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tag.length > 1) {
					popWindow = new TagPopWindow(context, tag, new MorelPopListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							viewHolder.province_tv.setText(tag[position].replace("非常", ""));
							object.setMytag(tag[position]);
							popWindow.dismiss();
						}
					});
					popWindow.showPopupWindow(viewHolder.tag_tv);
				}
			}
		});
		viewHolder.add_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				double curPrice = object.getMyprice();
				// curPrice += object.getStep();
				curPrice = Arith.add(curPrice, object.getStep());
				object.setMyprice(curPrice);
				viewHolder.price_tv.setText((int) curPrice + "");
				// bottom_price_tv.setText(curPrice * object.getMynumber() + "");
//				bottom_price_tv.setText(Arith.mul(curPrice, object.getMynumber()) + "");
				showDiscount(curPrice,object,bottom_price_tv,Arith.mul(curPrice, object.getMynumber()));
			}
		});
		viewHolder.delete_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				double price = object.getPrice();
				double curPrice = object.getMyprice();
				if (curPrice > price) {
					// curPrice -= object.getStep();
					curPrice = Arith.sub(curPrice, object.getStep());
					object.setMyprice(curPrice);
					viewHolder.price_tv.setText((int) curPrice + "");
//					bottom_price_tv.setText(Arith.mul(curPrice, object.getMynumber()) + "");
				}
				showDiscount(curPrice,object,bottom_price_tv,Arith.mul(curPrice, object.getMynumber()));

			}
		});
		viewHolder.add_rl2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentNumber = object.getMynumber();
				if (currentNumber < object.getMaxnum()) {
					currentNumber++;
					object.setMynumber(currentNumber);
					// double curprice = object.getMynumber() * object.getMyprice();
					double curprice = Arith.mul(object.getMynumber(), object.getMyprice());
//					bottom_price_tv.setText(curprice + "");
					viewHolder.person_tv.setText(currentNumber + "");
					initAnimation(viewHolder.person_tv);
					showDiscount(object.getMyprice(),object,bottom_price_tv,curprice);
				} else {
					MainTools.ShowToast(context, "最多供" + currentNumber + "人订餐");
				}

			}
		});
		viewHolder.delete_rl2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentNumber = object.getMynumber();
				if (currentNumber >= 1) {
					currentNumber--;
					object.setMynumber(currentNumber);
					double curprice = Arith.mul(object.getMynumber(), object.getMyprice());
//					bottom_price_tv.setText(curprice + "");
					viewHolder.person_tv.setText(currentNumber + "");
					showDiscount(object.getMyprice(),object,bottom_price_tv,curprice);
				} else {
				}
			}
		});
		viewHolder.comments_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, CommentsDetailActivity.class);
				intent.putExtra("nickname", chefList.getNickname());
				if (!TextUtils.isEmpty(chefList.getHomecity())) {
					intent.putExtra("naviland", "非常" + chefList.getHomeprov().replace("省", "").replace("市", "") + "/"
							+ chefList.getHomecity().replace("新区", "").replace("市", "").replace("区", ""));
				} else {
					intent.putExtra("naviland", "非常" + chefList.getHomeprov().replace("省", "").replace("市", ""));
				}
				intent.putExtra("avatar", chefList.getAvatar());
//				intent.putExtra("obj", (Serializable) object);
				intent.putExtra("score",object.getTotalscore());
				intent.putExtra("commentnum",object.getCommentnum());
				intent.putExtra("cbids",object.getCbids());
				context.startActivity(intent);
			}
		});
	}
	private void showDiscount(double curPrice, CookBookLevel object, TextView bottom_price_tv2, double curprice) {
		double mydiscount;
		if (curPrice == limitPrice) {
			if (object.getMynumber() >= eatennum) {
				yh_tv.setText(eatennum * discount + "");
				mydiscount=eatennum * discount;
			} else {
				yh_tv.setText(object.getMynumber() * discount + "");
				mydiscount=object.getMynumber() * discount;
			}
		} else {
			yh_tv.setText(0 + "");
			mydiscount=0;
		}
		bottom_price_tv2.setText((curprice-mydiscount)+"");
	}
	public class ViewHolder {
		private RelativeLayout add_rl;
		private RelativeLayout add_rl2;
		private RelativeLayout delete_rl;
		private RelativeLayout delete_rl2;
		private TextView level_tv;
		private TextView comments_tv;
		private TextView price_tv;
		private TextView person_tv;
		private RatingBar ratingbar;
		private TextView tag_tv;
		private ImageView tag_iv;
		private TextView top_gray;
		private TextView province_tv;
		private RelativeLayout tag_rl;
		private TextView today_relay_tv;
		private TextView tomorrow_relay_tv;
	}

	public int getDiscount() {
		return discount;
	}

	public void setEatennum(int eatennum) {
		this.eatennum = eatennum;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public void setLimitPrice(double limitPrice) {
		this.limitPrice = limitPrice;
	}

	private void initAnimation(TextView number_tv) {
		int[] start_location = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
		number_tv.getLocationInWindow(start_location);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
		buyImg = new CircleImageView(context);
		buyImg.setBackgroundResource(R.drawable.deep_dot2);
		setAnim(buyImg, start_location);// 开始执行动画
	}

	private void setAnim(final View v, int[] start_location) {
		anim_mask_layout = null;
		anim_mask_layout = createAnimLayout();
		anim_mask_layout.addView(v);// 把动画小球添加到动画层
		final View view = addViewToAnimLayout(anim_mask_layout, v, start_location);
		int[] end_location = new int[2];// 这是用来存储动画结束位置的X、Y坐标
		bottom_price_tv.getLocationInWindow(end_location);// shopCart是那个购物车

		// 计算位移
		// int endX = 0 - start_location[0] + 40;// 动画位移的X坐标
		int endX = end_location[0] - start_location[0];
		int endY = end_location[1] - start_location[1];// 动画位移的y坐标
		TranslateAnimation translateAnimationX = new TranslateAnimation(0, endX, 0, 0);
		translateAnimationX.setInterpolator(new LinearInterpolator());
		translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);

		TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0, 0, endY);
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
				// buyNum++;// 让购买数量加1
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
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		animLayout.setLayoutParams(lp);
		animLayout.setId(MAX_VALUE);
		animLayout.setBackgroundResource(android.R.color.transparent);
		rootView.addView(animLayout);
		return animLayout;
	}
	private View addViewToAnimLayout(final ViewGroup vg, final View view, int[] location) {
		int x = location[0];
		int y = location[1];
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = x;
		lp.topMargin = y;
		view.setLayoutParams(lp);
		return view;
	}

}
