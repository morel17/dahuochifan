package com.dahuochifan.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.cookbook.CBCookBook;
import com.dahuochifan.model.cookbookself.ChefCBAll;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.cookbook.CBData;
import com.dahuochifan.ui.adapter.MyCookBookAdapterNew;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;
import com.zcw.togglebutton.ToggleButton;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by Morel on 2016/1/14.
 * Chef's CookBook
 */
public class MyCookBookActivity extends BaseActivity {
    private MyAsyncHttpClient client;
    private RequestParams param;
    private Context context;
    private Gson gson;

    @Bind(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    @Bind(R.id.top_center_tv)
    TextView top_center_tv;
    @Bind(R.id.cookbook_name)
    TextView cookbook_name;
    @Bind(R.id.com_num_tv)
    TextView com_num_tv;
    @Bind(R.id.mytogglebtn)
    ToggleButton mytogglebtn;
    @Bind(R.id.status_tv)
    TextView status_tv;
    @Bind(R.id.title_tv)
    TextView title_tv;
    @Bind(R.id.cookbook_cyc)
    RecyclerView cookbook_cyc;
    private List<CBCookBook> list;

    private MyCookBookAdapterNew adapter;
    private LayoutInflater inflater;
    private ChefCBAll chef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initViews();
        getMainData();
    }

    public void onEventMainThread(FirstEvent event) {
        if (event.getMsg().equals("cbedit")) {
            if (BuildConfig.LEO_DEBUG)
                L.e("dy");
            getMainData();
        }
    }

    private void getMainData() {
        if (TextUtils.isEmpty(MyConstant.user.getChefids()))
            MyConstant.user.setChefids(SharedPreferenceUtil.initSharedPerence().getChefIds(baseSpf));
        param = ParamData.getInstance().getChefTopObj(MyCookBookActivity.this, 1, SharedPreferenceUtil.initSharedPerence().getGDLongitude(baseSpf),
                SharedPreferenceUtil.initSharedPerence().getGDLatitude(baseSpf), MyConstant.user.getChefids());
        client.post(MyConstant.URL_GETCUISINE, param, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                CBData data = new CBData();
                if (data.dealData(responseString, MyCookBookActivity.this, gson) == 1) {
                    if (data.getCbAll() != null && data.getCbAll().getMap() != null && data.getCbAll().getMap().getCookbooks() != null) {
                        list = data.getCbAll().getMap().getCookbooks();
                    }
                    adapter.setCbList(list);
                    adapter.notifyDataSetChanged();
                    cookbook_cyc.setVisibility(View.VISIBLE);
                    top_center_tv.setVisibility(View.GONE);
                } else {
                    cookbook_cyc.setVisibility(View.GONE);
                    top_center_tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }
        });
    }

    private void initData() {
        client = new MyAsyncHttpClient();
        param = new RequestParams();
        context = this;
        gson = new Gson();
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
    }

    private void initViews() {
        inflater = LayoutInflater.from(this);
        adapter = new MyCookBookAdapterNew(inflater, this);
        LinearLayoutManager managerL = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cookbook_cyc.setLayoutManager(managerL);
        cookbook_cyc.setItemAnimator(new DefaultItemAnimator());
        cookbook_cyc.setAdapter(adapter);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_my_caipu_new;
    }

    @Override
    protected String initToolbarTitle() {
        return "私房菜";
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
