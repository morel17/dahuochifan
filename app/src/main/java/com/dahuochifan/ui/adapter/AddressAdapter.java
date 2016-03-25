package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.model.address.AddressInfo;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AddressAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater inflater;
	private List<AddressInfo> list=new ArrayList<AddressInfo>();
	private Gson gson;
	private MyAsyncHttpClient client;
	private RequestParams params;
	
	public AddressAdapter(Context context, List<AddressInfo> list, Gson gson, MyAsyncHttpClient client, RequestParams params) {
		super();
		this.context=context;
		this.list=list;
		inflater=LayoutInflater.from(context);
		this.gson=gson;
		this.client=client;
		this.params=params;
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
			convertView=inflater.inflate(R.layout.item_address, parent,false);
			holder.shike_name_tv=(TextView)convertView.findViewById(R.id.shike_name_tv);
			holder.mobile_tv=(TextView)convertView.findViewById(R.id.mobile_tv);
			holder.addressinfo_tv=(TextView)convertView.findViewById(R.id.addressinfo_tv);
			holder.moren_iv=(ImageView)convertView.findViewById(R.id.moren_iv);
			holder.moren_rl=(RelativeLayout)convertView.findViewById(R.id.moren_rl);
			holder.status_tv=(TextView)convertView.findViewById(R.id.status_tv);
			convertView.setTag(holder);
		}
		holder.shike_name_tv.setText(list.get(position).getName());
		holder.mobile_tv.setText(list.get(position).getMobile());
		if(TextUtils.isEmpty(list.get(position).getLoc_simple())){
			if(TextUtils.isEmpty(list.get(position).getLoc_detail())){
				holder.addressinfo_tv.setText(list.get(position).getAddrdetail());
			}else{
				holder.addressinfo_tv.setText(list.get(position).getLoc_detail()+list.get(position).getAddrdetail());
			}
		}else{
			if(!TextUtils.isEmpty(list.get(position).getAddrdetail())){
				holder.addressinfo_tv.setText(list.get(position).getLoc_simple()+list.get(position).getAddrdetail());
			}else{
				holder.addressinfo_tv.setText(list.get(position).getLoc_simple());
			}
		}


		if(list.get(position).isdefault()){
			holder.status_tv.setVisibility(View.VISIBLE);
			holder.status_tv.setSelected(true);
		}else{
			holder.status_tv.setVisibility(View.GONE);
			holder.status_tv.setSelected(false);
		}
		return convertView;
	}
	
	public List<AddressInfo> getList() {
		return list;
	}

	public void setList(List<AddressInfo> list) {
		this.list = list;
	}

	private void postDefaultAddr(final int posi) {
		params=ParamData.getInstance().getDefaultAddrObj(list.get(posi).getDaids());
		client.post(MyConstant.URL_POSTDEFAULTADD, params,new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				try {
					JSONObject jobj=new JSONObject(responseString);
					int resultcode=jobj.getInt("resultcode");
					if(resultcode==1){
						list.get(posi).setIsdefault(true);
						for(int i=0;i<list.size();i++){
							if(i!=posi){
								if(BuildConfig.LEO_DEBUG)L.e("position"+posi);
								list.get(i).setIsdefault(false);
							}
						}
						MainTools.ShowToast(context,jobj.getString("tag"));
						notifyDataSetChanged();
						if(BuildConfig.LEO_DEBUG)L.e("**"+list.get(posi).isdefault());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				MainTools.ShowToast(context, R.string.interneterror);
			}
		});
	}

	class ViewHolder{
		private TextView shike_name_tv;
		private TextView mobile_tv;
		private TextView addressinfo_tv;
		private TextView status_tv;
		private ImageView moren_iv;
		private RelativeLayout moren_rl;
	}
	
}
