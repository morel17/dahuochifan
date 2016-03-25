package com.dahuochifan.ui.adapter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.ui.activity.AddressManagerActivity;
import com.dahuochifan.ui.activity.DhDiscountActivity;
import com.dahuochifan.ui.activity.GonggaoActivity;
import com.dahuochifan.ui.activity.MeCollectActivity;
import com.dahuochifan.ui.activity.MyCookBookActivity;
import com.dahuochifan.ui.activity.PayActivity;
import com.dahuochifan.ui.activity.PersonInfoActivity;
import com.dahuochifan.ui.activity.SettingActivity;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by morel on 2015/8/19.
 * 主界面侧栏Adapter
 */
public class MainCycAdapter extends RecyclerView.Adapter<MainCycAdapter.ViewHolder>{
    private List<String> mData;
    private LayoutInflater inflate;
    private AppCompatActivity context;
//    public static ActionSheet actionSheet;

    public MainCycAdapter(List<String> mData, AppCompatActivity context) {
        this.mData = mData;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
        ShareSDK.initSDK(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(inflate.inflate(R.layout.adapter_item_menu, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String title = mData.get(position);
        holder.menu_content_tv.setText(title);
        if (mData.get(1).toString().equals("用餐地址")) {
            switch (position) {
                case 0:
                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_info);
                    break;
                case 1:
                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_dizhi);
                    break;
                case 2:
                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_coll);
                    break;
//                case 3:
//                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_apply);
//                    break;
//                case 4:
//                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_share);
//                    break;
                case 3:
                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_discount);
                    break;
                case 4:
                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_sett);
                    break;
            }
        } else {
            switch (position) {
                case 0:
                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_info);
                    break;
                case 1:
                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_pay);
                    break;
                case 2:
                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_cuisine);
                    break;
                case 3:
                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_noti);
                    break;
//                case 4:
//                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_share);
//                    break;
                case 4:
                    holder.menu_contnet_iv.setBackgroundResource(R.drawable.dh_sett);
                    break;
            }
        }
        holder.menu_content_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.get(1).toString().equals("用餐地址")) {
                    switch (position) {
                        case 0:
                            Intent intent_personinfo = new Intent(context, PersonInfoActivity.class);
                            context.startActivity(intent_personinfo);
                            break;
                        case 1:
                            Intent intent_add = new Intent(context, AddressManagerActivity.class);
                            context.startActivity(intent_add);
                            break;
                        case 2:
                            Intent intent_tab2 = new Intent(context, MeCollectActivity.class);
                            context.startActivity(intent_tab2);
                            break;
//                        case 3:
//                            Intent intent_apply = new Intent(context, ApplyAllActivity.class);
//                            context.startActivity(intent_apply);
//                            break;
//                        case 4:
//                            context.setTheme(R.style.ActionSheetStyleIOS7);
//                            showActionSheet();
//                            break;
                        case 3:
                            Intent intent_discount = new Intent(context, DhDiscountActivity.class);
                            context.startActivity(intent_discount);
                            break;
                        case 4:
                            Intent intent_setting = new Intent(context, SettingActivity.class);
                            context.startActivity(intent_setting);
                            break;
                    }
                } else {
                    switch (position) {
                        case 0:
                            Intent intent_personinfo = new Intent(context, PersonInfoActivity.class);
                            context.startActivity(intent_personinfo);
                            break;
                        case 1:
                            Intent intent_pay = new Intent(context, PayActivity.class);
                            context.startActivity(intent_pay);
                            break;
                        case 2:
                            Intent intent_caipu = new Intent(context, MyCookBookActivity.class);
                            context.startActivity(intent_caipu);
                            break;
                        case 3:
                            Intent intent_gonggao = new Intent(context, GonggaoActivity.class);
                            context.startActivity(intent_gonggao);
                            break;
//                        case 4:
//                            context.setTheme(R.style.ActionSheetStyleIOS7);
//                            showActionSheet();

//                            break;
                        case 4:
                            Intent intent_setting = new Intent(context, SettingActivity.class);
                            context.startActivity(intent_setting);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

//    @Override
//    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
//
//    }

//    @Override
//    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
//        {
//            switch (index) {
//                case 0:
//                    Platform plat = ShareSDK.getPlatform(context, Wechat.NAME);
//                    plat.setPlatformActionListener(this);
//                    Wechat.ShareParams sp = new Wechat.ShareParams();
//                    // 任何分享类型都需要title和text
//                    // the two params of title and text are required in every share type
//                    sp.title = "搭伙吃饭分享";
//                    sp.text = "搭伙吃饭APP下载，正宗家庭小炒，纯正老家味";
//                    sp.shareType = Platform.SHARE_WEBPAGE;
//                    sp.url = "http://www.dahuochifan.com/download/app";
//                    sp.imageData = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//                    plat.share(sp);
//                    break;
//                case 1:
//                    shareMoments();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

//    private void shareMoments() {
//        Platform plat2 = ShareSDK.getPlatform(context, WechatMoments.NAME);
//        plat2.setPlatformActionListener(this);
//        WechatMoments.ShareParams sp2 = new WechatMoments.ShareParams();
//        sp2.shareType = Platform.SHARE_TEXT;
//        sp2.title = "搭伙吃饭分享title";
//        sp2.text = "搭伙吃饭APP下载，正宗家庭小炒，纯正老家味text";
//        sp2.shareType = Platform.SHARE_WEBPAGE;
//        sp2.url = "http://www.dahuochifan.com/download/app";
////                    sp2.imageUrl = "http://www.dahuochifan.com/web/img/barcode.png";
//        sp2.imageData = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//        plat2.share(sp2);
//    }

//    @Override
//    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//
//    }
//
//    @Override
//    public void onError(Platform platform, int i, Throwable throwable) {
//
//    }
//
//    @Override
//    public void onCancel(Platform platform, int i) {
//
//    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView menu_content_tv;
        private ImageView menu_contnet_iv;
        private PercentRelativeLayout menu_content_rl;

        public ViewHolder(View itemView) {
            super(itemView);
            menu_content_tv = (TextView) itemView.findViewById(R.id.menu_content_tv);
            menu_contnet_iv = (ImageView) itemView.findViewById(R.id.menu_content_iv);
            menu_content_rl = (PercentRelativeLayout) itemView.findViewById(R.id.menu_content_rl);
        }
    }

//    public void showActionSheet() {
//        actionSheet = ActionSheet.createBuilder(context, context.getSupportFragmentManager()).setCancelButtonTitle("取消").setOtherButtonTitles("分享给好友", "分享到朋友圈")
//                .setCancelableOnTouchOutside(true).setListener(this).show();
//    }

    public void setmData(List<String> mData) {
        this.mData = mData;
    }
}
