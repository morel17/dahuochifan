package com.dahuochifan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.cookbookself.ChefCBAll;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.cookbookself.ChefCBData;
import com.dahuochifan.ui.adapter.MyCookBookAdapter;
import com.dahuochifan.ui.views.MyScrollview;
import com.dahuochifan.ui.views.MyWrapListView;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NetworkImageHolderView;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;


public class MyCaipuActivity extends BaseActivity {
    private MyAsyncHttpClient client;
    private RequestParams param;
    private Context context;
    private Gson gson;
    private MyCookBookAdapter adapter1;
    private MyWrapListView listview;
    private MyScrollview scrollview;
    private TextView tip_tv;
    private TextView top_center_tv;
    private LayoutInflater inflater;
    private RelativeLayout add_pic_rl;
    private ChefCBAll chef;
    private ConvenientBanner convenientBanner;
    private ArrayList<String> picList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
        init();
        btn_listener();
        getMainData();
    }

    public void onEventMainThread(FirstEvent event) {
        if (event.getMsg().equals("cbedit")) {
            if (BuildConfig.LEO_DEBUG)
                L.e("dy");
            getMainData();
        }
    }

    private void init() {
        context = this;
        client = new MyAsyncHttpClient();
        param = new RequestParams();
        // param.prepare(this, param);
        gson = new Gson();

        scrollview = (MyScrollview) findViewById(R.id.mydetail_scrollview);
        listview = (MyWrapListView) findViewById(R.id.business_listview);
        tip_tv = (TextView) findViewById(R.id.title_tv);
        top_center_tv = (TextView) findViewById(R.id.top_center_tv);
        add_pic_rl = (RelativeLayout) findViewById(R.id.add_pic_rl);
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        inflater = LayoutInflater.from(context);
        adapter1 = new MyCookBookAdapter(context);
        listview.setAdapter(adapter1);
    }


    private void getMainData() {
        if (TextUtils.isEmpty(MyConstant.user.getChefids()))
            MyConstant.user.setChefids(SharedPreferenceUtil.initSharedPerence().getChefIds(baseSpf));
        param = ParamData.getInstance().getChefTopObj(MyCaipuActivity.this, 1, SharedPreferenceUtil.initSharedPerence().getGDLongitude(baseSpf),
                SharedPreferenceUtil.initSharedPerence().getGDLatitude(baseSpf), MyConstant.user.getChefids());
        client.post(MyConstant.URL_GETCUISINE, param, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ChefCBData cookdata = new ChefCBData();
                if (cookdata.dealData(responseString, MyCaipuActivity.this, gson) == 1) {
                    if (cookdata.getChefCbAll() != null) {
                        chef = cookdata.getChefCbAll();
                        if (chef.getMap() != null && chef.getMap().getCookbook() != null && chef.getMap().getCookbook().size() > 0) {
                            initCBViewPager(chef);
                            top_center_tv.setVisibility(View.GONE);
                        } else {
                            top_center_tv.setVisibility(View.VISIBLE);
                        }
                    }
                    adapter1.clear();
                    List<ChefCBAll> listTemp = new ArrayList<>();
                    listTemp.add(chef);
                    adapter1.setmObjects(listTemp);
                    adapter1.notifyDataSetChanged();
                } else {
                    top_center_tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(context, R.string.interneterror);
                listview.setVisibility(View.GONE);
            }
        });
    }

    /**
     * @param chefx chefinfo
     *              初始化图片轮播
     */
    private void initCBViewPager(ChefCBAll chefx) {
        if (chefx.getMap().getPic() != null && chefx.getMap().getPic().size() > 0) {
            picList.clear();
            for (int i = 0; i < chefx.getMap().getPic().size(); i++) {
                if (chefx.getMap().getPic().get(i).getStatus().equals("1")) {
                    picList.add(chefx.getMap().getPic().get(i).getUrl());
                }
            }
            convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                @Override
                public NetworkImageHolderView createHolder() {
                    return new NetworkImageHolderView();
                }
            }, picList)
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int i) {
                            Intent intent = new Intent(MyCaipuActivity.this, PhotoViewVPActivity.class);
                            intent.putExtra("imgList", picList);
                            intent.putExtra("item", i);
                            startActivity(intent);
                        }
                    })
                    //设置翻页的效果，不需要翻页效果可用不设
//                    .setPageTransformer(ConvenientBanner.Transformer.DepthPageTransformer)
                    //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                    .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused});
//                    .startTurning(16888);
            try {
                Class cls = Class.forName("com.ToxicBakery.viewpager.transforms." + DepthPageTransformer.class.getSimpleName());
                ABaseTransformer transforemer = (ABaseTransformer) cls.newInstance();
                convenientBanner.getViewPager().setPageTransformer(true, transforemer);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void btn_listener() {
        add_pic_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_my_caipu;
    }

    @Override
    protected String initToolbarTitle() {
        return "私房菜";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
