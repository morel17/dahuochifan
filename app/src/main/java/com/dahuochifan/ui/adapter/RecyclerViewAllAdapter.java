package com.dahuochifan.ui.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.interfaces.TopCuisineItemListener;
import com.dahuochifan.model.cheflist.ChefActs;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.activity.ChefDetailActivity;
import com.dahuochifan.ui.activity.ConfirmOrderActivity;
import com.dahuochifan.ui.activity.DhActivitiesDetailActivity;
import com.dahuochifan.ui.activity.LoginActivity;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.LunboLoader;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NetworkImageHolderView;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.Tools;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

//import com.bigkoo.convenientbanner.CBViewHolderCreator;
//import com.bigkoo.convenientbanner.OnItemClickListener;

public class RecyclerViewAllAdapter extends RecyclerView.Adapter<RecyclerViewAllAdapter.ViewHolder> {
    private AppCompatActivity context;
    private LayoutInflater inflater;
    private List<ChefList> list;
    private boolean cando;
    private boolean cancollect;
    private String locationStr;
    private MyAsyncHttpClient client;
    private RequestParams params;
    //    private static final int TYPE_INFO = 0x000;
//    private static final int TYPE_IV = 0x0001;
    private boolean canClick;

    private int activityType, chefType;
    private List<ChefActs> acts;


    public RecyclerViewAllAdapter(AppCompatActivity context, List<ChefList> list, String locationStr) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.locationStr = locationStr;
        activityType = 0;
        chefType = 1;
        cando = true;
        cancollect = true;
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        canClick = true;
        acts = new ArrayList<>();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView chef_pic;
        private CircleImageView chef_head;
        private RelativeLayout chef_like_rl;
        private RelativeLayout chef_collect_rl;
        private RelativeLayout chef_eat_rl;
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
        private RelativeLayout order_rl;
        private RecyclerView activity_cyc;
        private MainChefListActAdapter act_adapter;
        private LinearLayoutManager manager;

        private ConvenientBanner convenientBanner;

        public ViewHolder(View itemView, int itemType) {
            super(itemView);
            if (itemType == chefType) {
                chef_pic = (ImageView) itemView.findViewById(R.id.chef_iv);
                chef_head = (CircleImageView) itemView.findViewById(R.id.chef_head_iv);
                chef_like_rl = (RelativeLayout) itemView.findViewById(R.id.like_rl);
                chef_collect_rl = (RelativeLayout) itemView.findViewById(R.id.collect_rl);
                chef_nickname_tv = (TextView) itemView.findViewById(R.id.nick_name_tv);
                chef_eat_rl = (RelativeLayout) itemView.findViewById(R.id.eat_rl);
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
                order_rl = (RelativeLayout) itemView.findViewById(R.id.order_rl);
                activity_cyc = (RecyclerView) itemView.findViewById(R.id.activity_cyc);
                manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                act_adapter = new MainChefListActAdapter(inflater, context);
                activity_cyc.setLayoutManager(manager);
                activity_cyc.setItemAnimator(new DefaultItemAnimator());
                activity_cyc.setAdapter(act_adapter);
            } else {
                convenientBanner = (ConvenientBanner) itemView.findViewById(R.id.convenientBanner);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        ChefList chef = list.get(position);
        if (chef.getModel() == 1) {
            return activityType;
        } else {
            return chefType;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final ChefList object = list.get(position);
        if (getItemViewType(position) == chefType) {
            if (object.getBgs() != null && object.getBgs().size() > 0) {
                if (viewHolder.chef_pic.getTag() == null || !viewHolder.chef_pic.getTag().equals(object.getBgs().get(0).getUrl())) {
                    LunboLoader.loadImage(object.getBgs().get(0).getUrl() + "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_70_80) + "/h/"
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
                if (splitStr.length == 0) {
                    viewHolder.province_tv.setText("");
                    viewHolder.province2_tv.setText("");
                    viewHolder.province3_tv.setText("");
                    viewHolder.province4_tv.setText("");

                } else if (splitStr.length == 1) {
                    viewHolder.province_tv.setText(splitStr[0]);
                    viewHolder.province2_tv.setText("");
                    viewHolder.province3_tv.setText("");
                    viewHolder.province4_tv.setText("");
                } else if (splitStr.length == 2) {
                    viewHolder.province_tv.setText(splitStr[0]);
                    viewHolder.province2_tv.setText(splitStr[1]);
                    viewHolder.province3_tv.setText("");
                    viewHolder.province4_tv.setText("");
                } else if (splitStr.length == 3) {
                    viewHolder.province_tv.setText(splitStr[0]);
                    viewHolder.province2_tv.setText(splitStr[1]);
                    viewHolder.province3_tv.setText(splitStr[2]);
                    viewHolder.province4_tv.setText("");
                } else {
                    viewHolder.province_tv.setText(splitStr[0]);
                    viewHolder.province2_tv.setText(splitStr[1]);
                    viewHolder.province3_tv.setText(splitStr[2]);
                    viewHolder.province4_tv.setText("");
                }
            }
            if (object.getActs() != null) {
                acts = object.getActs();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_80_80), acts.size()
                        * context.getResources().getDimensionPixelOffset(R.dimen.dh_24));
                viewHolder.activity_cyc.setLayoutParams(params);
                viewHolder.act_adapter.setActs(acts);
                viewHolder.act_adapter.notifyDataSetChanged();
            }
            if (object.isopen()) {
                if (object.getLimitdistance() != 0 && (object.getDistance() <= object.getLimitdistance())) {
                    String lastTime = null;
                    if (!TextUtils.isEmpty(object.getDinnertimes())) {
                        String[] timesStr = object.getDinnertimes().split(",");
                        if (!TextUtils.isEmpty(timesStr[timesStr.length - 1])) {
                            lastTime = MainTools.getLastTime(timesStr[timesStr.length - 1], object.getAdvanceminute());
                        }
                    }
                    if (!TextUtils.isEmpty(lastTime)) {
                        if (MainTools.timeCompare("11:00") && MainTools.timeCompare(lastTime)) {
                            viewHolder.order_tv.setText("明日预定");
                        } else {
                            viewHolder.order_tv.setText("今日下单");
                        }
                    } else {
                        if (MainTools.timeCompare("11:00") && MainTools.timeCompare("17:00")) {
                            viewHolder.order_tv.setText("明日预定");
                        } else {
                            viewHolder.order_tv.setText("今日下单");
                        }
                    }
                    viewHolder.order_tv.setBackgroundResource(R.drawable.fastorder_press_selector);
                } else {
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
                    if (cando) {
                        doLike(object, isLike, viewHolder.chef_like_rl, viewHolder.chef_likenum_tv);
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
            viewHolder.chef_eat_rl.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {

                }
            });
            viewHolder.cardView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (!MyConstant.user.isLogin()) {
                        context.startActivity(new Intent(context, LoginActivity.class));
                    } else {
                        Intent intent = new Intent(context, ChefDetailActivity.class);
                        intent.putExtra("myids", object.getChefids());
                        intent.putExtra("tag", locationStr);
                        context.startActivity(intent);
                    }
                }
            });
            viewHolder.act_adapter.setOnItemClickListener(new TopCuisineItemListener() {
                @Override
                public void onItemClick(View view, int postionx) {
                    viewHolder.cardView.performClick();
                }
            });
            viewHolder.distance_tv.setText(Tools.getDistanc((object.getDistance())));
            viewHolder.order_rl.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (MyConstant.user.getRole() == 2) {
                        MainTools.ShowToast(context, "您好，暂时不支持主厨下订单");
                        return;
                    }
                    if (object.isopen()) {
                        if (object.getLimitdistance() != 0 && (object.getDistance() <= object.getLimitdistance())) {
                            if (canClick) {
//                                getCookBookInfo(object, viewHolder.order_tv.getText().toString());
                                Intent intent = new Intent(context, ConfirmOrderActivity.class);
                                Bundle bundle = new Bundle();
                                if (viewHolder.order_tv.getText().toString().equals("今日下单")) {
                                    bundle.putInt("whenindex", 0);
                                } else {
                                    bundle.putInt("whenindex", 1);
                                }
                                if (object.getLimitdistance() != 0 && (object.getDistance() <= object.getLimitdistance())) {
                                    bundle.putString("chefids", object.getChefids());
                                    intent.putExtras(bundle);
                                    context.startActivity(intent);
                                } else {
                                    String tipStr = "您超出点餐范围" + Tools.getDistanc(object.getDistance() - object.getLimitdistance()) + "不能点餐";
                                    MainTools.ShowToast(context, tipStr);
                                }
                            } else {
                                MainTools.ShowToast(context, "网速较慢,请稍等...");
                            }
                        }
                    }
                }
            });
        } else {
            ArrayList<String> networkImages = new ArrayList<>();
            if (object.getHeaders() != null && object.getHeaders().size() > 0) {
                for (int i = 0; i < object.getHeaders().size(); i++) {
                    networkImages.add(object.getHeaders().get(i).getHead() + "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_70_80) + "/h/"
                            + context.getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY);
                }
            }
//            网络加载例子
            viewHolder.convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                @Override
                public NetworkImageHolderView createHolder() {
                    return new NetworkImageHolderView();
                }
            }, networkImages)
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int i) {
                            if (object.getHeaders() != null && object.getHeaders().size() > 0) {
                                if (!TextUtils.isEmpty(object.getHeaders().get(i).getDetail())) {
                                    Intent intent = new Intent(context, DhActivitiesDetailActivity.class);
                                    intent.putExtra("remark",object.getHeaders().get(i).getRemark());
                                    intent.putExtra("detail", object.getHeaders().get(i).getDetail());
                                    intent.putExtra("url", object.getHeaders().get(i).getHead());
                                    context.startActivity(intent);
                                }
                            }
                        }
                    });
                    //设置翻页的效果，不需要翻页效果可用不设
//                    .setPageTransformer(ConvenientBanner.Transformer.DepthPageTransformer);
            if (object.getHeaders() != null && object.getHeaders().size() > 1) {
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                viewHolder.convenientBanner.setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused});
                viewHolder.convenientBanner.startTurning(16888);
                viewHolder.convenientBanner.setManualPageable(true);
            } else {
                viewHolder.convenientBanner.setManualPageable(false);
            }
            try {
                Class cls = Class.forName("com.ToxicBakery.viewpager.transforms." + DepthPageTransformer.class.getSimpleName());
                ABaseTransformer transforemer = (ABaseTransformer) cls.newInstance();
                viewHolder.convenientBanner.getViewPager().setPageTransformer(true, transforemer);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        if (type == chefType) {
            View itemLayout = inflater.inflate(R.layout.adapter_cheflist, parent, false);
            return new ViewHolder(itemLayout, chefType);
        } else {
            View itemLayout = inflater.inflate(R.layout.adapter_cheflist_extra, parent, false);
            return new ViewHolder(itemLayout, activityType);
        }
    }

    public void setList(List<ChefList> list) {
        this.list = list;
    }

    private void doLike(final ChefList object, final boolean isLike, final RelativeLayout like_ll, final TextView like_tv) {
        params = ParamData.getInstance().getLikeObj(object.getChefids(), object.isIslike() ? "0" : "1");

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
                            object.setIslike(false);
                            like_ll.setSelected(false);
                            num--;
                        } else {
                            object.setIslike(true);
                            like_ll.setSelected(true);
                            num++;
                        }
                        String numStr = num + "";
                        object.setLikenum(numStr);
                        like_tv.setText(numStr);
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
                            object.setIscollect(false);
                            collect_ll.setSelected(false);
                            num--;
                        } else {
                            object.setIscollect(true);
                            collect_ll.setSelected(true);
                            num++;
                        }
                        String numStr = num + "";
                        object.setCollectnum(numStr);
                        collect_tv.setText(numStr);
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
}
