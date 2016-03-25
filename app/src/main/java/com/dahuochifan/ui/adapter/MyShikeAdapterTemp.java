package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.model.custom.ShikeInfo;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.activity.ShikeDetailActivity;
import com.dahuochifan.ui.activity.ShikeUnReceActivity;
import com.dahuochifan.ui.activity.TimeLineActivity;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.utils.Arith;
import com.dahuochifan.utils.CustomerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class MyShikeAdapterTemp extends RootAdapter<ShikeInfo> {
    private Context context;
    private RequestParams params;
    private Gson gson;
    private SweetAlertDialog pd;
    private RelativeLayout control_rl;
    private int mainType, otherType;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE6:
                    pd.dismiss();
                    break;
                case MyConstant.MYHANDLER_CODE4:
                    pd.dismiss();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public MyShikeAdapterTemp(Context context) {
        super(context);
        this.context = context;
        params = new RequestParams();
        gson = new Gson();
        mainType = 0;
        otherType = 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ShikeInfo detail = getItem(position);
        if (!TextUtils.isEmpty(detail.getCbids())) {
            return mainType;
        } else {
            return otherType;
        }
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == mainType) {
            View v = inflater.inflate(R.layout.item_my_shike_new, parent, false);
            ViewHolderMain viewHolder = new ViewHolderMain(v);
            v.setTag(viewHolder);
            return v;
        } else {
            View v = inflater.inflate(R.layout.item_myshike_other, parent, false);
            ViewHolderOther viewHolder = new ViewHolderOther(v);
            v.setTag(viewHolder);
            return v;
        }

    }

    @Override
    protected void bindView(View view, final int position, final ShikeInfo object) {
        if (getItemViewType(position) == mainType) {
            if (BuildConfig.LEO_DEBUG) L.e("**************" + object.getNumber());
            final ViewHolderMain viewHolder = (ViewHolderMain) view.getTag();
            CustomerHead.loadImage(object.getAvatar(), viewHolder.chef_avatar_iv);
            viewHolder.chef_nickname_tv.setText(object.getNickname());
            viewHolder.person_tv.setText(object.getEatennum() + "份");
            viewHolder.tag_tv.setText(object.getTag());
            if (!TextUtils.isEmpty(object.getLunchtime())) {
                viewHolder.day_number_tv.setText(MainTools.changeMD(object.getEatenwhen()) + object.getLunchtime());
            } else {
                viewHolder.day_number_tv.setText(MainTools.changeMD(object.getEatenwhen()) + object.getDinnertime());
            }

            viewHolder.address_tv.setText(object.getDinername() + " (" + object.getDineraddr() + ")");
            if (object.getTrack() != null) {
                viewHolder.timeline_tv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.timeline_tv.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(object.getLunchtime())) {
                viewHolder.night_tv.setText("午");
            }
            if (!TextUtils.isEmpty(object.getDinnertime())) {
                viewHolder.night_tv.setText("晚");
            }
            double allResult = Arith.add(object.getTotal(), object.getDiscount());
            viewHolder.all_price_tv.setText("￥" + MainTools.getDoubleValue(allResult, 1));
            switch (object.getOlstatus()) {
                case "2":
                    if (object.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                        viewHolder.pay_tv.setText("已付款");
                        viewHolder.status_tv.setText("待接单");
                    } else if (object.getPaystatus().equals(MyConstant.PAY_NO)) {
                        viewHolder.pay_tv.setText("未付款");
                        viewHolder.status_tv.setText("异常");
                    } else if (object.getPaystatus().equals(MyConstant.REFUND_APPLY)) {
                        viewHolder.pay_tv.setText("退款中");
                    } else if (object.getPaystatus().equals(MyConstant.REFUND_SUCCESS)) {
                        viewHolder.pay_tv.setText("已退款");
                    }
                    viewHolder.control_rl.setVisibility(View.VISIBLE);
//				viewHolder.status_tv.setTextColor(context.getResources().getColor(R.color.maincolor));
                    break;
                case "3":
                    if (object.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                        viewHolder.pay_tv.setText("已付款");
                    } else if (object.getPaystatus().equals(MyConstant.PAY_NO)) {
                        viewHolder.pay_tv.setText("未付款");
                    } else if (object.getPaystatus().equals(MyConstant.REFUND_APPLY)) {
                        viewHolder.pay_tv.setText("退款中");
                    } else if (object.getPaystatus().equals(MyConstant.REFUND_SUCCESS)) {
                        viewHolder.pay_tv.setText("已退款");
                    }
                    viewHolder.status_tv.setText("等待确认用餐");
                    viewHolder.control_rl.setVisibility(View.GONE);
                    break;
                case "5":
                    if (object.getPaystatus().equals(MyConstant.REFUND_SUCCESS)) {
                        viewHolder.pay_tv.setText("已退款");
                    } else if (object.getPaystatus().equals(MyConstant.REFUND_APPLY)) {
                        viewHolder.pay_tv.setText("退款中");
                    } else if (object.getPaystatus().equals(MyConstant.PAY_NO)) {
                        viewHolder.pay_tv.setText("未付款");
                    } else if (object.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                        viewHolder.pay_tv.setText("已付款");
                    }
                    viewHolder.status_tv.setText("已取消");
                    viewHolder.control_rl.setVisibility(View.GONE);
                    break;
                case "4":
                    if (object.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                        viewHolder.pay_tv.setText("已付款");
                    } else if (object.getPaystatus().equals(MyConstant.PAY_NO)) {
                        viewHolder.pay_tv.setText("未付款");
                    } else if (object.getPaystatus().equals(MyConstant.REFUND_APPLY)) {
                        viewHolder.pay_tv.setText("退款中");
                    } else if (object.getPaystatus().equals(MyConstant.REFUND_SUCCESS)) {
                        viewHolder.pay_tv.setText("已退款");
                    }
                    viewHolder.status_tv.setText("已拒单");
                    viewHolder.control_rl.setVisibility(View.GONE);
                    break;
                default:

                    break;
            }
            viewHolder.top_rl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShikeDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putSerializable("obj", object);
                    bundle.putInt("index", 0);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
            viewHolder.left_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否接受该订单").setCancelText("取消")
                            .setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            if (MainTools.isNetworkAvailable(context)) {
                                if (object.getOlstatus().equals("2")) {
                                    postStatus(object.getOlids(), "3", position, object);
                                }
                            } else {
                                MainTools.ShowToast(context, R.string.interneterror);
                            }
                        }
                    }).show();
                }
            });
            viewHolder.right_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否拒接该订单").setCancelText("取消")
                            .setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            if (MainTools.isNetworkAvailable(context)) {
                                postStatus(object.getOlids(), "4", position, object);
                            } else {
                                MainTools.ShowToast(context, R.string.interneterror);
                            }
                        }
                    }).show();
                }
            });
            viewHolder.timeline_tv.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (object.getTrack().getStatus().equals("0")) {
                        Intent intent = new Intent(context, TimeLineActivity.class);
                        if (!TextUtils.isEmpty(object.getTrack().getUrl())) {
                            intent.putExtra("trackurl", object.getTrack().getUrl());
                            context.startActivity(intent);
                        }
                    }
                }
            });
        } else {
            final ViewHolderOther viewHolder = (ViewHolderOther) view.getTag();
            viewHolder.other_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ShikeUnReceActivity.class));
                }
            });
        }
    }

    private void postStatus(final String olids, final String status, final int position, final ShikeInfo object) {
        params = ParamData.getInstance().postStatusObj(olids, status, object.getOlversion());
        client.post(MyConstant.URL_FOLLOWORDER, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在提交");
                pd.setCancelable(false);
                pd.getProgressHelper().setBarColor(context.getResources().getColor(R.color.blue_btn_bg_color));
                pd.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    int request = jobj.getInt("resultcode");
                    if (request == 1) {
                        pd.setTitleText(jobj.getString("tag")).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        Message msg = Message.obtain();
                        msg.arg1 = position;
                        msg.what = MyConstant.MYHANDLER_CODE6;
                        handler.sendMessageDelayed(msg, 1500);
                    } else {
                        pd.setContentText(jobj.getString("tag")).setTitleText("错误提示").setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    }
                    EventBus.getDefault().post(new FirstEvent("MyShike"));
                    EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_ONE));
                    EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_TWO));
                    if(BuildConfig.LEO_DEBUG)L.e("hehehe0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.setTitleText("网络不给力").changeAlertType(SweetAlertDialog.ERROR_TYPE);
                handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
            }
        });

    }

    public class ViewHolderMain {
        private CircleImageView chef_avatar_iv;
        private TextView chef_nickname_tv;
        private TextView tag_tv;
        private TextView person_tv;
        private TextView day_number_tv;
        private TextView night_tv;
        private TextView all_price_tv;
        private TextView left_tv;
        private TextView status_tv;
        private TextView right_tv;
        private RelativeLayout top_rl;
        private RelativeLayout control_rl;
        private TextView pay_tv;
        private TextView address_tv;
        private TextView timeline_tv;

        public ViewHolderMain(View v) {
            chef_avatar_iv = (CircleImageView) v.findViewById(R.id.chef_avatar_iv);
            chef_nickname_tv = (TextView) v.findViewById(R.id.chef_nickname_tv);
            tag_tv = (TextView) v.findViewById(R.id.tag_tv);
            person_tv = (TextView) v.findViewById(R.id.person_tv);
            day_number_tv = (TextView) v.findViewById(R.id.day_number_tv);
            night_tv = (TextView) v.findViewById(R.id.night_tv);
            all_price_tv = (TextView) v.findViewById(R.id.all_price_tv);
            left_tv = (TextView) v.findViewById(R.id.left_tv);
            right_tv = (TextView) v.findViewById(R.id.right_tv);
            top_rl = (RelativeLayout) v.findViewById(R.id.top_rl);
            status_tv = (TextView) v.findViewById(R.id.status_tv);
            control_rl = (RelativeLayout) v.findViewById(R.id.control_rl);
            pay_tv = (TextView) v.findViewById(R.id.pay_tv);
            address_tv = (TextView) v.findViewById(R.id.address_tv);
            timeline_tv = (TextView) v.findViewById(R.id.timeline_tv);
        }
    }

    public class ViewHolderOther {
        private TextView other_tv;

        public ViewHolderOther(View v) {
            other_tv = (TextView) v.findViewById(R.id.other_tv);
        }
    }

}
