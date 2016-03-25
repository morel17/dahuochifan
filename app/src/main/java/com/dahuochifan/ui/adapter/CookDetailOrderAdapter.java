package com.dahuochifan.ui.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.dao.Gwc;
import com.dahuochifan.dao.GwcDao;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.utils.Tools;

import java.util.Calendar;
import java.util.List;

public class CookDetailOrderAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private Activity context;
	private GwcDao gwcDao;
	private Calendar ca;
	private List<Gwc> list;   
	private ImageView gwc_view;
	private ChefList chefList;
	private String date;
	private TextView bottom_price_tv;
	
	public CookDetailOrderAdapter(Activity context,GwcDao gwcDao,Calendar ca, List<Gwc> templist_all, ImageView gwc_view,ChefList chefList, String date, TextView bottom_price_tv) {
		super();
		this.context=context;
		this.gwcDao=gwcDao;
		this.ca=ca;
		inflater=LayoutInflater.from(context);
		this.list=templist_all;
		this.gwc_view=gwc_view;
		this.chefList=chefList;
		this.date=date;
		this.bottom_price_tv=bottom_price_tv;
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
		if(convertView!=null){
			holder=(ViewHolder)convertView.getTag();
		}else{
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_cookdetail_order, parent,false);
			holder.statustv=(TextView)convertView.findViewById(R.id.statustv);
			holder.pricetv=(TextView)convertView.findViewById(R.id.pricetv);
			holder.delete_iv=(ImageView)convertView.findViewById(R.id.delete_iv);
			holder.number_tv=(TextView)convertView.findViewById(R.id.number_tv);
			holder.add_iv=(ImageView)convertView.findViewById(R.id.add_iv);
			holder.leveltv=(TextView)convertView.findViewById(R.id.leveltv);
			convertView.setTag(holder);
		}
		if(list.get(position).getStatus().equals("1")){
			holder.statustv.setText("商务私厨");
		}else{
			holder.statustv.setText("邻家私厨");
		}
		holder.pricetv.setText(list.get(position).getPrice()+"");
		holder.number_tv.setText(list.get(position).getNumber()+"");
		holder.leveltv.setText(list.get(position).getName()+"");
		holder.add_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentNumber = Tools.toInteger(holder.number_tv.getText().toString());
				currentNumber++;
				double curprice= Tools.toDouble(bottom_price_tv.getText().toString());
				bottom_price_tv.setText(curprice+list.get(position).getPrice()+"");
				holder.number_tv.setText(currentNumber+"");
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
		return convertView;
	}
	public void getTmepObj(){
		List<Gwc> templist=gwcDao.queryRaw("where chefid=? and mid=? and mydate=?", new String[]{chefList.getChefids(),MyConstant.user.getUserids(),date});
		int price=0;
		for(int i=0;i<templist.size();i++){
			price+=templist.get(i).getPrice()*templist.get(i).getNumber();
			bottom_price_tv.setText(price+"");
		}
	}
	class ViewHolder{
		TextView statustv;
		TextView pricetv;
		ImageView delete_iv;
		TextView number_tv;
		ImageView add_iv;
		TextView leveltv;
	}

}
