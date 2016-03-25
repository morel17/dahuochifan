package com.dahuochifan.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.dao.ChefListDao;
import com.dahuochifan.dao.ChefListStr;
import com.dahuochifan.dao.DaoMaster;
import com.dahuochifan.dao.DaoSession;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.interfaces.TopCuisineItemListener;
import com.dahuochifan.model.TagObj;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.CookData;
import com.dahuochifan.requestdata.TagData;
import com.dahuochifan.ui.adapter.MainTabAdapter;
import com.dahuochifan.ui.adapter.RecyclerViewAllAdapter;
import com.dahuochifan.ui.adapter.TopCuisineAdapter;
import com.dahuochifan.ui.views.dialog.MorelAlertDialog;
import com.dahuochifan.utils.LunboLoader;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.L;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by morel on 2015/11/23.
 * mainfragment
 */
public class MainFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<RecyclerView> {
    private RecyclerView cyc_view;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Gson gson;
    private List<TagObj> taglist;
    private TopCuisineAdapter cuisineAdapter;
    private MainTabAdapter tabAdapter;
    private SharedPreferences spf;
    private int lastPosition;
    private TextView top_all_tv;
    private ImageView top_more_iv;
    private ImageView back_bg;
    private PullToRefreshRecyclerView mRecyclerView;
    private FrameLayout cyc_frame;
    private ChefListDao chefDao;
    private List<ChefList> list;
    private RecyclerViewAllAdapter adapter;
    private RecyclerView gv_recycler;
    private int page = 1;
    private String longitudeStr, latitudeStr;
    private String curTag;
    PropertyValuesHolder pvhA1, pvhY1, pvhA2, pvhY2;
//    private SweetAlertDialog pDialog;

    /**
     * 预加载界面
     */
    private PercentRelativeLayout preview_rl;
    private TextView refresh_tv;

    static class MyHandler extends Handler {
        WeakReference<PullToRefreshRecyclerView> puReferences;
        WeakReference<MainFragment> wrReferences;

        MyHandler(PullToRefreshRecyclerView wrReferences, MainFragment wrReference) {
            this.puReferences = new WeakReference<>(wrReferences);
            this.wrReferences = new WeakReference<>(wrReference);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE3:
                    PullToRefreshRecyclerView cycView = puReferences.get();
                    cycView.onRefreshComplete();
                    break;
                case MyConstant.MYHANDLER_ERROR:
                    wrReferences.get().changeAlertDialog(MorelAlertDialog.ERROR_TYPE, "网络不给力");
                    this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_WAIT, 1500);
                    break;
                case MyConstant.MYHANDLER_WARN:
                    wrReferences.get().changeAlertDialog(MorelAlertDialog.WARNING_TYPE, "请求出错");
                    this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_WAIT, 1500);
                    break;
                case MyConstant.MYHANDLER_SUCCESS:
                    wrReferences.get().changeAlertDialog(MorelAlertDialog.SUCCESS_TYPE, "加载成功");
                    this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_WAIT, 1500);
                    break;
                case MyConstant.MYHANDLER_WAIT:
                    wrReferences.get().dismissAlertDialog();
                    break;
                default:
                    break;
            }
        }
    }

    public void onEventMainThread(FirstEvent event) {
        if (event != null && !TextUtils.isEmpty(event.getMsg())) {
            switch (event.getMsg()) {
                case "TAGS":
                    refreshMainView();
                    break;
                case "DhLocation":
                    refreshMainView();
                    break;
            }
        }
    }

    /**
     * 完全刷新当前界面
     */
    private void refreshMainView() {
        if (lastPosition != -1) {
            top_all_tv.setSelected(true);
            curTag = "";
            taglist.get(lastPosition).setIsselect(false);
            cuisineAdapter.notifyDataSetChanged();
            lastPosition = -1;
        }
        getTopData();
        if (cyc_frame != null && top_more_iv != null && pvhA2 != null && pvhY2 != null) {
            ObjectAnimator oa2 = ObjectAnimator.ofPropertyValuesHolder(cyc_frame, pvhA2, pvhY2).setDuration(888);
            oa2.start();
            oa2.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    top_more_iv.setSelected(false);
                    cyc_frame.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        getMainData(true, curTag, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        EventBus.getDefault().register(this);
        cyc_view = (RecyclerView) view.findViewById(R.id.cyc_view);
        back_bg = (ImageView) view.findViewById(R.id.back_bg);
        top_all_tv = (TextView) view.findViewById(R.id.top_all_tv);
        top_more_iv = (ImageView) view.findViewById(R.id.top_more_iv);
        mRecyclerView = (PullToRefreshRecyclerView) view.findViewById(R.id.recycler);
        gv_recycler = (RecyclerView) view.findViewById(R.id.gv_recycler);
        cyc_frame = (FrameLayout) view.findViewById(R.id.cyc_frame);
        cyc_frame.setVisibility(View.INVISIBLE);
        cyc_frame.setPivotY(0);
        preview_rl = (PercentRelativeLayout) view.findViewById(R.id.preview_rl);
//        detail_tv = (TextView) view.findViewById(R.id.detail_tv);
        refresh_tv = (TextView) view.findViewById(R.id.refresh_tv);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initRecyclerView();
        btnListener();
        getTopData();
    }

    /**
     * 下拉recyclerview的配置
     */
    private void initRecyclerView() {
        mRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        mRecyclerView.setOnRefreshListener(this);
        // 设置下拉刷新文本
        mRecyclerView.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        mRecyclerView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        mRecyclerView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置上拉刷新文本
        mRecyclerView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        mRecyclerView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        mRecyclerView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
        getDataFromDB();
    }

    /**
     * 从数据库直接获取数据
     */
    private void getDataFromDB() {
        List<ChefListStr> listtemp = chefDao.queryRaw("where text=?", "");
        if (listtemp != null && listtemp.size() > 0) {
            String content;
            content = chefDao.queryRaw("where text=?", "").get(0).getComment();
            if (!TextUtils.isEmpty(content)) {
                CookData cookdata = new CookData();
                if (cookdata.dealData(content, mActivity, gson) == 1) {
                    if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
                        L.e("dbSize=====" + list.size());
                        list = cookdata.getAll().getPage().getList();
                        list.set(0, list.get(0));
                        adapter.setList(list);
                    } else {
                        L.e("dbSize2=====" + list.size());
                        adapter.setList(list);
                    }
                }
            }
        } else {
            adapter.setList(list);
        }
        getMainData(true, "", false);
    }

    /**
     * @param isRefresh 是否是下拉操作
     * @param tag       菜系标签
     * @param isRecyc   是否是通过下拉来刷新当前界面的
     */
    private void getMainData(boolean isRefresh, String tag, boolean isRecyc) {
        if (isRefresh) {
            page = 1;
        }
        int pageSize = 15;
        int picNum = 3;
        longitudeStr = SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf);
        latitudeStr = SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf);
        if (tag.equals("")) {
            params = ParamData.getInstance().getChefListObj(mActivity, page, pageSize, picNum, longitudeStr, latitudeStr,
                    tag, true + "");
        } else {
            params = ParamData.getInstance().getChefListObj(mActivity, page, pageSize, picNum, longitudeStr, latitudeStr,
                    tag, false + "");
        }
        post_refresh(params, isRefresh, tag, isRecyc);
    }

    /**
     * @param param2    requsetParam
     * @param isRefresh 用于判断是首界面刷新还是上拉加载
     * @param tag       口味标签
     * @param isRecyc   是否是通过上拉下拉来调用这个方法的 仅仅是用于控制pb的出现和消失
     */
    private void post_refresh(RequestParams param2, final boolean isRefresh, final String tag, final boolean isRecyc) {
        final MyHandler handler = new MyHandler(mRecyclerView, this);
        client.setTimeout(5000);
        client.post(MyConstant.URL_MAINCOOKLIST, param2, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!isRecyc) {
                    showAlertDialog(MorelAlertDialog.PROGRESS_TYPE, "正在加载中", false);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                CookData cookdata = new CookData();
                if (cookdata.dealData(responseString, mActivity, gson) == 1) {
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_SUCCESS);
                    if (!isRefresh) {
                        if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
                            list.addAll(cookdata.getAll().getPage().getList());
                        } else {
                            MainTools.ShowToast(mActivity, "已经到底啦");
                        }
                    } else {//刷新的情况下
                        if (tag.equals("") && !TextUtils.isEmpty(responseString)) {
                            List<ChefListStr> temp_list = chefDao.queryRaw("where text=?", "");
                            ChefListStr gwc;
                            if (temp_list != null && temp_list.size() > 0) {
                                gwc = new ChefListStr(temp_list.get(0).getId(), "", responseString);
                            } else {
                                gwc = new ChefListStr(null, "", responseString);
                            }
                            chefDao.insertOrReplace(gwc);
                        }
                        if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
                            list = cookdata.getAll().getPage().getList();
                            mRecyclerView.setVisibility(View.VISIBLE);
                            preview_rl.setVisibility(View.GONE);
                        } else {
                            list.clear();
                            if (!isRecyc) {
                                preview_rl.setVisibility(View.VISIBLE);
                                back_bg.setVisibility(View.VISIBLE);
                                refresh_tv.setVisibility(View.GONE);
//                                detail_tv.setVisibility(View.VISIBLE);
//                                detail_tv.setText("没有更多厨师了");
                            }
                            back_bg.setBackgroundResource(R.drawable.chef_back);
                        }
                    }
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    page++;
                } else {
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_WARN);
//                    MainTools.ShowToast(mActivity, cookdata.getAll().getTag());
                    if (cookdata.getAll() != null && !TextUtils.isEmpty(cookdata.getAll().getTag())) {
                        showTipDialog(cookdata.getAll().getTag(), Integer.parseInt(cookdata.getAll().getResultcode()));
                    } else {
                        showTipDialog("未知错误", -1);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (!isRecyc) {
                    preview_rl.setVisibility(View.VISIBLE);
                    back_bg.setVisibility(View.VISIBLE);
                    refresh_tv.setVisibility(View.VISIBLE);
//                    detail_tv.setVisibility(View.VISIBLE);
//                    detail_tv.setText("网络不给力");
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_ERROR);
                } else {
                    MainTools.ShowToast(mActivity, R.string.interneterror);
                }
                back_bg.setBackgroundResource(R.mipmap.internet_failed);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRecyclerView.onRefreshComplete();
            }
        });
    }

    /**
     * 点击事件的监听
     */
    private void btnListener() {
        refresh_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taglist != null && taglist.size() > 0) {
                    if (!TextUtils.isEmpty(curTag)) {
                        getMainData(true, curTag, false);
                    } else {
                        refreshMainView();
                    }
                } else {
                    refreshMainView();
                }
            }
        });
        mRecyclerView.getRefreshableView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动，既是否向右滑动或向下滑动
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    new PauseOnScrollListener(LunboLoader.getInstance().getImageLoader(), false, false);
                } else {
                    new PauseOnScrollListener(LunboLoader.getInstance().getImageLoader(), true, true);
                }
            }
        });
        tabAdapter.setOnItemClickListener(new TopCuisineItemListener() {
            @Override
            public void onItemClick(View view, int postion) {
                if (taglist != null && taglist.size() > 0 && cuisineAdapter != null) {
                    if (!taglist.get(postion).getName().equals(curTag)) {
                        //处理刷新
                        if (lastPosition != -1) {
                            taglist.get(lastPosition).setIsselect(false);
                        } else {
                            top_all_tv.setSelected(false);
                        }
                        taglist.get(postion).setIsselect(true);
                        curTag = taglist.get(postion).getName();
                        getMainData(true, curTag, false);
                        cuisineAdapter.notifyDataSetChanged();
                        lastPosition = postion;
                        if (cyc_frame.isShown()) {
                            topShow();
                        }
                        cyc_view.smoothScrollToPosition(postion);
                    }
                }
            }
        });
        cuisineAdapter.setOnItemClickListener(new TopCuisineItemListener() {
            @Override
            public void onItemClick(View view, int postion) {
                if (taglist != null && taglist.size() > 0 && cuisineAdapter != null) {
                    if (lastPosition != postion) {
                        //处理刷新
                        if (lastPosition != -1) {
                            taglist.get(lastPosition).setIsselect(false);
                        } else {
                            top_all_tv.setSelected(false);
                        }
                        taglist.get(postion).setIsselect(true);
                        curTag = taglist.get(postion).getName();
                        getMainData(true, curTag, false);
                        cuisineAdapter.notifyDataSetChanged();
                        lastPosition = postion;
                        if (cyc_frame.isShown()) {
                            topShow();
                        }
                    }
                }
            }
        });
        top_all_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (taglist != null && cuisineAdapter != null) {
                    if (lastPosition != -1) {
                        top_all_tv.setSelected(true);
                        curTag = "";
                        taglist.get(lastPosition).setIsselect(false);
                        cuisineAdapter.notifyDataSetChanged();
                        lastPosition = -1;
                        getMainData(true, curTag, false);
                    }
                    if (cyc_frame.isShown()) {
                        topShow();
                    }
                }
            }
        });
        top_more_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taglist != null && taglist.size() > 0) {
                    topShow();
                }
            }
        });
    }

    private void topShow() {
        if (!cyc_frame.isShown()) {
            top_more_iv.setSelected(true);
            cyc_frame.setVisibility(View.VISIBLE);
            ObjectAnimator.ofPropertyValuesHolder(cyc_frame, pvhA1, pvhY1).setDuration(666).start();
        } else {
            ObjectAnimator oa2 = ObjectAnimator.ofPropertyValuesHolder(cyc_frame, pvhA2, pvhY2).setDuration(666);
            oa2.start();
            oa2.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    top_more_iv.setSelected(false);
                    cyc_frame.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    /**
     * 初始化相关属性
     */
    private void initData() {
        client = new MyAsyncHttpClient();
        client.setMaxRetriesAndTimeout(1, 5000);
        client.setTimeout(5000);
        gson = new Gson();
        taglist = new ArrayList<>();
        cuisineAdapter = new TopCuisineAdapter(mActivity, taglist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        cyc_view.setLayoutManager(linearLayoutManager);
        cyc_view.setAdapter(cuisineAdapter);
        spf = SharedPreferenceUtil.initSharedPerence().init(mActivity, MyConstant.APP_SPF_NAME);
        DaoMaster daoMaster = new DaoMaster(MyConstant.db);
        DaoSession daoSession = daoMaster.newSession();
        chefDao = daoSession.getChefListDao();
        list = new ArrayList<>();
        adapter = new RecyclerViewAllAdapter(mActivity, list, "全部");
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        longitudeStr = SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf);
        latitudeStr = SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf);
        lastPosition = -1;//上一个position
        curTag = "";
        top_all_tv.setSelected(true);
        tabAdapter = new MainTabAdapter(taglist, mActivity);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 4);
        // layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        // 设置布局管理器
        gv_recycler.setLayoutManager(layoutManager);
        gv_recycler.setItemAnimator(new DefaultItemAnimator());
        gv_recycler.setAdapter(tabAdapter);
        pvhA1 = PropertyValuesHolder.ofFloat("alpha", 0.2f, 1f);
        pvhY1 = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
        pvhA2 = PropertyValuesHolder.ofFloat("alpha", 1f, 0.2f);
        pvhY2 = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f);
    }

    /**
     * 获取顶部菜系，以recyclerview展现
     */
    private void getTopData() {
        params = ParamData.getInstance().postTagObj_new();
        client.post(MyConstant.URL_CBTAG_LIST, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                TagData data = new TagData();
                if (data.dealData(responseString, mActivity, gson) == 1) {
                    taglist.clear();
                    if (data.getAll().getList() != null && data.getAll().getList().size() > 0) {
                        taglist = data.getAll().getList();
                        //设置布局管理器
                        //设置适配器
                        cuisineAdapter.setmDatas(taglist);
                        cuisineAdapter.notifyDataSetChanged();
                        tabAdapter.setList(taglist);
                        tabAdapter.notifyDataSetChanged();
                        int num = taglist.size() % 4;
                        int line = taglist.size() / 4;
                        if (num > 0) {
                            line++;
                        }
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mActivity.getResources().getDimensionPixelOffset(R.dimen.width_80_80), line
                                * mActivity.getResources().getDimensionPixelOffset(R.dimen.width_8_80));
                        gv_recycler.setLayoutParams(params);
                    }
                } else {
                    if (data.getAll() != null && !TextUtils.isEmpty(data.getAll().getTag())) {
                        showTipDialog(data.getAll().getTag(), data.getAll().getResultcode());
                    }
                }
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (MainTools.isNetworkAvailable(mActivity)) {
            getMainData(true, curTag, true);
        } else {
            doInNetUnuseful();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (MainTools.isNetworkAvailable(mActivity)) {
            if (page > 1) {
                getMainData(false, curTag, true);
            } else {
                doCloseRefresh();
            }
        } else {
            doInNetUnuseful();
        }
    }

    public void doInNetUnuseful() {
        MainTools.ShowToast(mActivity, R.string.check_internet);
        doCloseRefresh();
    }

    public void doCloseRefresh() {
        final MyHandler handlerx = new MyHandler(mRecyclerView, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handlerx.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE3, 1000);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
