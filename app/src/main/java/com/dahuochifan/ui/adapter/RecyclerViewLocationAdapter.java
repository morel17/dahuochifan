package com.dahuochifan.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.model.address.AddressInfo;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.activity.AddressAddNewActivity;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.Tools;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by Morel on 2015/10/26.
 */
public class RecyclerViewLocationAdapter extends RecyclerView.Adapter<RecyclerViewLocationAdapter.ViewHolder> {
    private Activity context;
    private LayoutInflater inflater;
    private static final int TYPE_HEAD = 0x000;
    private static final int TYPE_CONTENT = 0x0001;
    private List<AddressInfo> list = new ArrayList<AddressInfo>();
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;

    public RecyclerViewLocationAdapter(Activity context, LayoutInflater inflater, List<AddressInfo> list) {
        this.inflater = inflater;
        this.context = context;
        this.list = list;
        spf = SharedPreferenceUtil.initSharedPerence().init(context, MyConstant.APP_SPF_NAME);
        editor = spf.edit();
    }

    @Override
    public RecyclerViewLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (getItemViewType(viewType) == TYPE_CONTENT) {
            View itemLayout = inflater.inflate(R.layout.adapter_location_list, null);
            return new ViewHolder(itemLayout, TYPE_CONTENT);
        } else {
            View itemLayout = inflater.inflate(R.layout.adapter_location_head, null);
            return new ViewHolder(itemLayout, TYPE_HEAD);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewLocationAdapter.ViewHolder holder, int position) {
        if (position > 0) {
            final AddressInfo object = list.get(position - 1);
            if (object != null) {
                holder.name_tv.setText(object.getName());
                if (TextUtils.isEmpty(object.getLoc_simple())) {
                    if (!TextUtils.isEmpty(object.getAddrdetail())) {
                        holder.address_tv.setText(Tools.toString(object.getLoc_detail()) + object.getAddrdetail());
                    } else {
                        holder.address_tv.setText(Tools.toString(object.getLoc_detail()));
                    }
                } else {
                    if (!TextUtils.isEmpty(object.getAddrdetail())) {
                        holder.address_tv.setText(object.getLoc_simple() + object.getAddrdetail());
                    } else {
                        holder.address_tv.setText(object.getLoc_simple());
                    }
                }
                holder.phone_tv.setText(object.getMobile());
            }
            holder.address_rl.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    SharedPreferenceUtil.initSharedPerence().putGDProvince(editor, "上海");
                    if (TextUtils.isEmpty(object.getLoc_simple())) {
                        if (!TextUtils.isEmpty(object.getLoc_detail())) {
                            SharedPreferenceUtil.initSharedPerence().putPoiName(editor, object.getLoc_detail());
                        } else {
                            MainTools.ShowToast(context, "改地址需要完善");
                            return;
                        }
                    } else {
                        SharedPreferenceUtil.initSharedPerence().putPoiName(editor, object.getLoc_simple());
                    }
                    SharedPreferenceUtil.initSharedPerence().putGDLatitude(editor, object.getLatitude() + "");
                    SharedPreferenceUtil.initSharedPerence().putGDLongitude(editor, object.getLongitude() + "");
                    editor.commit();
                    EventBus.getDefault().post(new FirstEvent("DhLocation", SharedPreferenceUtil.initSharedPerence().getPoiName(spf)));
                    postAddrDefault(object.getDaids());
                    context.finish();
                }
            });
        } else {
            holder.add_rl.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (list.size() >= 20) {
                        MainTools.ShowToast(context, "您的地址数量已经超过上限");
                    } else {
                        context.startActivity(new Intent(context, AddressAddNewActivity.class));
                    }
                }
            });
        }
    }

    private void postAddrDefault(String daids) {
        MyAsyncHttpClient client=new MyAsyncHttpClient();
        RequestParams params = ParamData.getInstance().getDefaultAddrObj(daids);
        client.post(MyConstant.URL_POSTDEFAULTADD, params,new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int type) {
        if (type == 0) {
            return TYPE_CONTENT;
        } else {
            return TYPE_HEAD;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv, phone_tv, address_tv;
        RelativeLayout address_rl;
        RelativeLayout add_rl;

        public ViewHolder(View itemView, int itemType) {
            super(itemView);
            if (itemType == TYPE_CONTENT) {
                name_tv = (TextView) itemView.findViewById(R.id.name_tv);
                phone_tv = (TextView) itemView.findViewById(R.id.phone_tv);
                address_tv = (TextView) itemView.findViewById(R.id.address_tv);
                address_rl = (RelativeLayout) itemView.findViewById(R.id.address_rl);
            } else {
                add_rl = (RelativeLayout) itemView.findViewById(R.id.add_rl);
            }
        }
    }

    public void setList(List<AddressInfo> list) {
        this.list = list;
    }
}
