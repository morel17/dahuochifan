package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.ui.activity.ChefDetailActivity;
import com.dahuochifan.ui.activity.ConfirmOrderActivity;
import com.dahuochifan.ui.activity.LeftMainGridActivity2;
import com.dahuochifan.ui.activity.LoginActivity;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.order.OrderListV11;
import com.dahuochifan.core.order.OrderParamV11;
import com.dahuochifan.model.CookBookLevel;
import com.dahuochifan.model.CookBookPartAll;
import com.dahuochifan.model.TagObj;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.CookBookListData;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.LunboLoader;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.Tools;
import com.dahuochifan.ui.views.CircleImageView;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RecyclerViewTabAdapter extends RecyclerView.Adapter<RecyclerViewTabAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<ChefList> list;
    private boolean cando;
    private boolean cancollect;
    private String locationStr;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private static final int TYPE_HEAD = 0x000;
    private static final int TYPE_CONTENT = 0x0001;
    private List<TagObj> adList = new ArrayList<TagObj>();
    private Gson gson;
    private CookBookLevel cookbook;

    private MainTabAdapter adapter;
    private boolean canClick;

    public RecyclerViewTabAdapter(Context context, List<ChefList> list, String locationStr) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.locationStr = locationStr;
        cando = true;
        cancollect = true;
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        adapter = new MainTabAdapter(adList, context);
        gson = new Gson();
        canClick=true;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView chef_pic;
        private CircleImageView chef_head;
        private RelativeLayout chef_like_rl;
        private RelativeLayout chef_collect_rl;
        private TextView chef_nickname_tv;
        private TextView province_tv;
        private TextView chef_eatnum_tv;
        private TextView chef_collectnum_tv;
        private TextView chef_likenum_tv;
        private CardView cardView;
        private TextView distance_tv;
        private TextView province2_tv;
        private TextView province3_tv;
        private TextView province4_tv;
        private TextView order_tv;
        private RelativeLayout bottom_rl;
        private RecyclerView recycler;
        private ImageView back_bg;
        private RelativeLayout order_rl;
        private RelativeLayout chef_eat_rl;
        private RelativeLayout discount_rl,discount_rl2;
        private ImageView discount_others,discount_others2;
        private TextView discount_remark,discount_remark2;

        public ViewHolder(View itemView, int itemType) {
            super(itemView);
            if (itemType == TYPE_CONTENT) {
                chef_pic = (ImageView) itemView.findViewById(R.id.chef_iv);
                chef_head = (CircleImageView) itemView.findViewById(R.id.chef_head_iv);
                chef_like_rl = (RelativeLayout) itemView.findViewById(R.id.like_rl);
                chef_collect_rl = (RelativeLayout) itemView.findViewById(R.id.collect_rl);
                chef_nickname_tv = (TextView) itemView.findViewById(R.id.nick_name_tv);
                province_tv = (TextView) itemView.findViewById(R.id.province_tv);
                province2_tv = (TextView) itemView.findViewById(R.id.province2_tv);
                province3_tv = (TextView) itemView.findViewById(R.id.province3_tv);
                province4_tv = (TextView) itemView.findViewById(R.id.province4_tv);
                chef_eatnum_tv = (TextView) itemView.findViewById(R.id.eat_num_tv);
                chef_collectnum_tv = (TextView) itemView.findViewById(R.id.collect_num_tv);
                chef_likenum_tv = (TextView) itemView.findViewById(R.id.like_num_tv);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                distance_tv = (TextView) itemView.findViewById(R.id.distence_tv);
                order_tv = (TextView) itemView.findViewById(R.id.order_tv);
                bottom_rl = (RelativeLayout) itemView.findViewById(R.id.bottom_rl);
                order_rl = (RelativeLayout) itemView.findViewById(R.id.order_rl);
                chef_eat_rl=(RelativeLayout)itemView.findViewById(R.id.eat_rl);
                discount_rl=(RelativeLayout) itemView.findViewById(R.id.discount_rl);
                discount_rl2=(RelativeLayout) itemView.findViewById(R.id.discount_rl2);
                discount_others=(ImageView)itemView.findViewById(R.id.discount_others);
                discount_remark=(TextView)itemView.findViewById(R.id.discount_remark);
                discount_others2=(ImageView)itemView.findViewById(R.id.discount_others2);
                discount_remark2=(TextView)itemView.findViewById(R.id.discount_remark2);
            } else {
                recycler = (RecyclerView) itemView.findViewById(R.id.recycler);
                back_bg = (ImageView) itemView.findViewById(R.id.back_bg);
            }
        }
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

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEAD) {
            final ChefList object = list.get(position - 1);
            if (object.getBgs() != null && object.getBgs().size() > 0) {
                if (viewHolder.chef_pic.getTag() == null || !viewHolder.chef_pic.getTag().equals(object.getBgs().get(0).getUrl())) {
                    LunboLoader.loadImage(object.getBgs().get(0).getUrl()+ "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_70_80) + "/h/"
                            + context.getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY, viewHolder.chef_pic);
                    viewHolder.chef_pic.setTag(object.getBgs().get(0).getUrl());
                }
            }
            CookerHead.loadImage(object.getAvatar(), viewHolder.chef_head);
            viewHolder.chef_eatnum_tv.setText(object.getEatennum());
            viewHolder.chef_collectnum_tv.setText(object.getCollectnum());
            viewHolder.chef_likenum_tv.setText(object.getLikenum());
            if (!TextUtils.isEmpty(object.getTags())) {
                String[] splitStr = object.getTags().split(",");
                if(splitStr.length==0){
                    viewHolder.province_tv.setText("");
                    viewHolder.province2_tv.setText("");
                    viewHolder.province3_tv.setText("");
                    viewHolder.province4_tv.setText("");

                }else if(splitStr.length==1){
                    viewHolder.province_tv.setText(splitStr[0]);
                    viewHolder.province2_tv.setText("");
                    viewHolder.province3_tv.setText("");
                    viewHolder.province4_tv.setText("");
                }else if(splitStr.length==2){
                    viewHolder.province_tv.setText(splitStr[0]);
                    viewHolder.province2_tv.setText(splitStr[1]);
                    viewHolder.province3_tv.setText("");
                    viewHolder.province4_tv.setText("");
                }else if(splitStr.length==3){
                    viewHolder.province_tv.setText(splitStr[0]);
                    viewHolder.province2_tv.setText(splitStr[1]);
                    viewHolder.province3_tv.setText(splitStr[2]);
                    viewHolder.province4_tv.setText("");
                }else{
                    viewHolder.province_tv.setText(splitStr[0]);
                    viewHolder.province2_tv.setText(splitStr[1]);
                    viewHolder.province3_tv.setText(splitStr[2]);
                    viewHolder.province4_tv.setText("");
                }
            }
            if(object.getActs()!=null){
                if(object.getActs().size()==0){
                    viewHolder.discount_rl.setVisibility(View.GONE);
                    viewHolder.discount_rl2.setVisibility(View.GONE);
                }else if(object.getActs().size()==1){
                    viewHolder.discount_rl.setVisibility(View.VISIBLE);
                    viewHolder.discount_rl2.setVisibility(View.GONE);
                    LunboLoader.loadImage(object.getActs().get(0).getIconurl(), viewHolder.discount_others);
                    viewHolder.discount_remark.setText(object.getActs().get(0).getRemark());
                }else if(object.getActs().size()>=2){
                    viewHolder.discount_rl.setVisibility(View.VISIBLE);
                    viewHolder.discount_rl2.setVisibility(View.VISIBLE);
                    LunboLoader.loadImage(object.getActs().get(0).getIconurl(), viewHolder.discount_others);
                    viewHolder.discount_remark.setText(object.getActs().get(0).getRemark());
                    LunboLoader.loadImage(object.getActs().get(1).getIconurl(), viewHolder.discount_others2);
                    viewHolder.discount_remark2.setText(object.getActs().get(1).getRemark());
                }
            }
            if (object.isopen()) {
                if(object.getLimitdistance()!=0&&(object.getDistance()<= object.getLimitdistance())){
                    String lastTime = null;
                    if(!TextUtils.isEmpty(object.getDinnertimes())){
                        String[] timesStr=object.getDinnertimes().split(",");
                        if(!TextUtils.isEmpty(timesStr[timesStr.length-1])){
                            lastTime=MainTools.getLastTime(timesStr[timesStr.length-1],object.getAdvanceminute());
                        }
                    }
                    if(!TextUtils.isEmpty(lastTime)){
                        if (MainTools.timeCompare("11:00") && MainTools.timeCompare(lastTime)) {
                            viewHolder.order_tv.setText("明日预定");
                        } else {
                            viewHolder.order_tv.setText("今日下单");
                        }
                    }else{
                        if (MainTools.timeCompare("11:00") && MainTools.timeCompare("17:00")) {
                            viewHolder.order_tv.setText("明日预定");
                        } else {
                            viewHolder.order_tv.setText("今日下单");
                        }
                    }
                    viewHolder.order_tv.setBackgroundResource(R.drawable.fastorder_press_selector);
                }else{
                    viewHolder.order_tv.setText("超出点餐范围");
                    viewHolder.order_tv.setBackgroundResource(R.mipmap.fastorder_unable);
                }
            } else {
                viewHolder.order_tv.setText("主厨休息中");
                viewHolder.order_tv.setBackgroundResource(R.mipmap.fastorder_unable);
            }
            viewHolder.chef_nickname_tv.setText(object.getNickname());

            viewHolder.chef_like_rl.setSelected(object.isIslike());
            viewHolder.chef_collect_rl.setSelected(object.isIscollect());

            viewHolder.chef_like_rl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isLike = object.isIslike();
                    String number = object.getLikenum();
                    if (cando) {
                        doLike(object, isLike, number, viewHolder.chef_like_rl, viewHolder.chef_likenum_tv);
                    }
                }
            });
            viewHolder.chef_collect_rl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isCollect = object.isIscollect();
                    if (cancollect) {
                        doCollect(object, viewHolder.chef_collect_rl, isCollect, viewHolder.chef_collectnum_tv);
                    }
                }
            });
            viewHolder.cardView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (!MyConstant.user.isLogin()) {
                        context.startActivity(new Intent(context, LoginActivity.class));
                    } else {
                        Intent intent = new Intent(context, ChefDetailActivity.class);
                        intent.putExtra("mylist", (Serializable) object);
                        intent.putExtra("myids", object.getChefids());
                        intent.putExtra("tag", locationStr);
                        context.startActivity(intent);
                    }
                }
            });
            viewHolder.distance_tv.setText(Tools.getDistanc((object.getDistance())));
            viewHolder.chef_eat_rl.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {

                }
            });
            viewHolder.order_rl.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (MyConstant.user.getRole() == 2) {
                        MainTools.ShowToast(context, "您好，暂时不支持主厨下订单");
                        return;
                    }
                    if (object.isopen()) {
                        if(object.getLimitdistance()!=0&&(object.getDistance()<= object.getLimitdistance())) {
                            if (canClick) {
//                                getCookBookInfo(object, viewHolder.order_tv.getText().toString());
                            } else {
                                MainTools.ShowToast(context, "网速较慢,请稍等...");
                            }
                        }
                    }
                }
            });
        } else {
            GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
            // layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
            // 设置布局管理器
            viewHolder.recycler.setLayoutManager(layoutManager);
            viewHolder.recycler.setItemAnimator(new DefaultItemAnimator());
            viewHolder.recycler.setAdapter(adapter);
            int num = adList.size() % 4;
            int line = adList.size() / 4;
            if (num > 0) {
                line++;
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_80_80), line
                    * context.getResources().getDimensionPixelOffset(R.dimen.width_8_80));
            viewHolder.recycler.setLayoutParams(params);
            if (list.size() > 0) {
                viewHolder.back_bg.setVisibility(View.GONE);
            } else {
                viewHolder.back_bg.setVisibility(View.VISIBLE);
            }
            viewHolder.back_bg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, LeftMainGridActivity2.class));
                }
            });
        }

    }

    private void getCookBookInfo(final ChefList object, final String timeStrs) {
        params = ParamData.getInstance().getCookBookListObjPart(object.getChefids(), "");
        client.post(MyConstant.URL_GETCUISINE, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                canClick=false;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                canClick=true;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(context, R.string.interneterror);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (BuildConfig.LEO_DEBUG) L.e(responseString);
                CookBookListData data = new CookBookListData();
                CookBookPartAll partData = data.getCookbookpart(gson, responseString);
                if (partData != null && partData.getResultcode() == 1) {
                    cookbook = partData.getList().get(0);
                    if (cookbook != null) {
                        List<OrderListV11> orderlist = new ArrayList<OrderListV11>();
                        OrderListV11 order = new OrderListV11();
                        order.setCbids(cookbook.getCbids());
                        order.setEatennum(1);
                        order.setPrice(cookbook.getPrice() + "");
                        order.setRemark("");
                        order.setTag(cookbook.getTags().split(",")[0]);
                        OrderParamV11 op = new OrderParamV11();
                        orderlist.add(order);
                        op.setList(orderlist);
                        op.setChefids(object.getChefids());
                        op.setMids(MyConstant.user.getUserids());
                        op.setToken(MyConstant.user.getToken());
                        op.setPlatform(MyConstant.device);
                        Intent intent = new Intent(context, ConfirmOrderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("OrderParam", op);
                        bundle.putSerializable("chef", object);
                        bundle.putSerializable("cookbook", cookbook);
                        if(timeStrs.equals("今日下单")){
                            bundle.putInt("whenindex", 0);
                        }else{
                            bundle.putInt("whenindex", 1);
                        }
                        if (object.getActivity() != null) {
                            bundle.putSerializable("activities", object.getActivity());
                        } else {
                            bundle.putSerializable("activities", null);
                        }
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                } else {
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        L.e("viewType==="+viewType);
        if (getItemViewType(viewType) == TYPE_CONTENT) {
            View itemLayout = inflater.inflate(R.layout.adapter_cheflist, null);
            return new ViewHolder(itemLayout, TYPE_CONTENT);
        } else {
            View itemLayout = inflater.inflate(R.layout.adapter_cheflist_tab, null);
            return new ViewHolder(itemLayout, TYPE_HEAD);
        }
    }

    public void setList(List<ChefList> list) {
        this.list = list;
    }

    private void doLike(final ChefList object, final boolean isLike, String number, final RelativeLayout like_ll, final TextView like_tv) {
        Gson gson = new Gson();
        String json;
        if (!object.isIslike()) {
            params = ParamData.getInstance().getLikeObj(object.getChefids(), "1");
        } else {
            params = ParamData.getInstance().getLikeObj(object.getChefids(), "0");
        }

        client.post(MyConstant.URL_MAINTOPLIKE, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                cando = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    if (jobj.getString("resultcode").equals("1")) {
                        int num = Tools.toInteger(object.getLikenum());
                        if (isLike) {
                            object.setIslike(!isLike);
                            like_ll.setSelected(!isLike);
                            num--;
                        } else {
                            object.setIslike(!isLike);
                            like_ll.setSelected(!isLike);
                            num++;
                        }
                        object.setLikenum(num + "");
                        like_tv.setText(num + "");
                    } else {
                        MainTools.ShowToast(context, jobj.getString("tag"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(context, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                cando = true;
            }
        });
    }

    private void doCollect(final ChefList object, final RelativeLayout collect_ll, final boolean isCollect, final TextView collect_tv) {
        Gson gson = new Gson();
        String json = null;
        if (!isCollect) {
            params = ParamData.getInstance().getLikeObj(object.getChefids(), "1");
        } else {
            params = ParamData.getInstance().getLikeObj(object.getChefids(), "0");
        }

        client.post(MyConstant.URL_MAINTOPCOLLECT, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                cancollect = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    if (jobj.getString("resultcode").equals("1")) {
                        int num = Tools.toInteger(object.getCollectnum());
                        if (isCollect) {
                            object.setIscollect(!isCollect);
                            collect_ll.setSelected(!isCollect);
                            num--;
                        } else {
                            object.setIscollect(!isCollect);
                            collect_ll.setSelected(!isCollect);
                            num++;
                        }
                        object.setCollectnum(num + "");
                        collect_tv.setText(num + "");
                    } else {
                        MainTools.ShowToast(context, jobj.getString("tag"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(context, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                cancollect = true;
            }
        });

    }

    public void setAdList(List<TagObj> adList) {
        this.adList = adList;
        adapter.setList(adList);
        adapter.notifyItemRangeInserted(0, adList.size() - 1);
    }

}
