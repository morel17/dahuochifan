package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.cookbook.CBCookBook;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.zcw.togglebutton.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MyCookBookAdapterNew extends RecyclerView.Adapter<MyCookBookAdapterNew.ViewHolder> {
    private LayoutInflater inflate;
    private List<CBCookBook> cbList;
    private boolean cantoogle;
    private Context context;
    protected MyAsyncHttpClient client;
    private RequestParams params;

    public MyCookBookAdapterNew(LayoutInflater inflate, Context context) {
        this.context = context;
        this.inflate = inflate;
        cbList = new ArrayList<>();
        client = new MyAsyncHttpClient();
        params = new RequestParams();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(inflate.inflate(R.layout.item_my_cookbooknew, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CBCookBook cbObj = cbList.get(position);
        holder.item_cb_name.setText(cbObj.getName());
        holder.tip_cb.setText(cbObj.isopen() ? "关闭" : "已关闭");
        holder.item_price.setText("￥" + cbObj.getPrices());
        holder.item_relay.setText(cbObj.getMaxnum() + "");
        setToggle(holder.item_toggle, cbObj.isopen());
        holder.item_toggle.setOnToggleChanged(new ToggleButton.OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                if (cantoogle) {
                    postToggle(on, cbObj, holder.item_toggle);
                }
            }
        });
    }

    /**
     * @param on          打开关闭状态
     * @param object      菜谱对象
     * @param mytogglebtn button
     *                    关闭菜单
     */
    private void postToggle(final boolean on, CBCookBook object, final ToggleButton mytogglebtn) {
        params = ParamData.getInstance().toggleObj(object.getCbids(), on);
        client.post(MyConstant.URL_COOKBOOK_OPEN, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                cantoogle = false;
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                try {
                    JSONObject jobj = new JSONObject(arg2);
                    String tag = jobj.getString("tag");
                    int resultcode = jobj.getInt("resultcode");
                    if (resultcode == 1) {
                        setToggle(mytogglebtn, on);
                    } else {
                        setToggle(mytogglebtn, !on);
                    }
                    MainTools.ShowToast(context, tag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                MainTools.ShowToast(context, R.string.interneterror);
                setToggle(mytogglebtn, !on);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                cantoogle = true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return cbList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_cb_name, tip_cb, item_price, item_relay;
        ToggleButton item_toggle;

        public ViewHolder(View itemView) {
            super(itemView);
            item_cb_name = (TextView) itemView.findViewById(R.id.item_cb_name);
            tip_cb = (TextView) itemView.findViewById(R.id.tip_cb);
            item_price = (TextView) itemView.findViewById(R.id.item_price);
            item_relay = (TextView) itemView.findViewById(R.id.item_relay);
            item_toggle = (ToggleButton) itemView.findViewById(R.id.item_toggle);
        }
    }

    private void setToggle(final ToggleButton mytogglebtn, boolean flag) {
        if (flag) {
            mytogglebtn.setToggleOn();
        } else {
            mytogglebtn.setToggleOff();
        }
    }

    public void setCbList(List<CBCookBook> cbList) {
        this.cbList = cbList;
    }
}
