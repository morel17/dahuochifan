package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.CookBookLevel;
import com.dahuochifan.param.CbEditParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.Tools;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class CaipuEditActivity extends BaseActivity {
    private RelativeLayout tag_rl;
    private EditText start_price_et, step_price_et, min_price_et, max_person_et;
    private TextView baocun;
    private TextView[] tagsTv = new TextView[5];
    private CookBookLevel object;
    private List<String> list = new ArrayList<>();
    private String newtag;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        initData();
        initViews();
        btn_listener();
    }

    public void onEventMainThread(FirstEvent event) {
        if (event.getMsg().equals("cooktag")) {
            list.clear();
            list = event.getList();
            tagsTv[0].setVisibility(View.GONE);
            tagsTv[1].setVisibility(View.GONE);
            tagsTv[2].setVisibility(View.GONE);
            tagsTv[3].setVisibility(View.GONE);
            tagsTv[4].setVisibility(View.GONE);
            for (int i = 0; i < list.size(); i++) {
                tagsTv[i].setText(list.get(i).replace("非常", ""));
                tagsTv[i].setVisibility(View.VISIBLE);
            }

            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                buffer.append(list.get(i));
                if (i < list.size() - 1) {
                    buffer.append(",");
                }
            }
            object.setTags(buffer.toString());
        }
    }

    private void initData() {
        object = (CookBookLevel) getIntent().getExtras().getSerializable("obj");
        if (object != null) {
            String[] objstr = object.getTags().split(",");
            if (objstr.length > 0)
                list.addAll(Arrays.asList(objstr));
        }
    }

    private void btn_listener() {
        baocun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() <= 0) {
                    MainTools.ShowToast(CaipuEditActivity.this, "至少选择一个标签");
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        newtag = list.get(i);
                    } else {
                        newtag += "," + list.get(i);
                    }
                }
                String start_price_str = start_price_et.getText().toString();
                String step_price_str = step_price_et.getText().toString();
                String min_price_str = min_price_et.getText().toString();
                String max_person_str = max_person_et.getText().toString();
//				CbEditParamData data = new CbEditParamData();
                if (TextUtils.isEmpty(start_price_str) || TextUtils.isEmpty(step_price_str) || TextUtils.isEmpty(min_price_str)
                        || TextUtils.isEmpty(max_person_str)) {
                    MainTools.ShowToast(CaipuEditActivity.this, "内容不能为空");
                    return;
                }
                int max_person = Tools.toInteger(max_person_str);
                if (max_person > 15) {
                    MainTools.ShowToast(CaipuEditActivity.this, "最多用餐人数不能超过15人");
                    return;
                }
                int start_price = Tools.toInteger(start_price_str);
                int min_price = Tools.toInteger(min_price_str);
                if (start_price == 0) {
                    MainTools.ShowToast(CaipuEditActivity.this, "起步价不能为0");
                    return;
                }
                if (min_price == 0) {
                    MainTools.ShowToast(CaipuEditActivity.this, "最低消费不能为0");
                    return;
                }
                if (max_person == 0) {
                    MainTools.ShowToast(CaipuEditActivity.this, "最多用餐人数不能为0");
                    return;
                }
//				String json = gson.toJson(data.cbeditObj(MyConstant.user.getUserids(), MyConstant.user.getToken(),  object.getCbids(),
//						start_price_str, newtag, max_person_str, min_price_str, step_price_str));
//				params.put("param", json);
//				params.put("sign", SignUtils.sign(json, MyConstant.PUCLIC_KEY));
                params = CbEditParamData.getInstance().cbeditObj(MyConstant.user.getUserids(), MyConstant.user.getToken(), object.getCbids(),
                        start_price_str, newtag, max_person_str, min_price_str, step_price_str, CaipuEditActivity.this);
                client.post(MyConstant.URL_COOKBOOK_EDIT, params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        pDialog = new SweetAlertDialog(CaipuEditActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在提交修改");
                        pDialog.setCancelable(false);
                        pDialog.getProgressHelper().setBarColor(ContextCompat.getColor(CaipuEditActivity.this, R.color.blue_btn_bg_color));
                        pDialog.setConfirmText("").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.show();
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        try {
                            JSONObject jobj = new JSONObject(arg2);
                            int request = jobj.getInt("resultcode");
                            String tag = jobj.getString("tag");
                            if (request == 1) {
                                pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                EventBus.getDefault().post(new FirstEvent("cbedit"));
                                finish();
                            } else {
                                pDialog.dismissWithAnimation();
                                if (!TextUtils.isEmpty(tag)) {
                                    showTipDialog(CaipuEditActivity.this, tag, request);
                                } else {
                                    showTipDialog(CaipuEditActivity.this, "重新登录", request);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                        pDialog.setTitleText("网络不给力").setConfirmText("确定").changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                });
            }
        });
        tag_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CaipuEditActivity.this, CookTagActivity.class);
                intent.putExtra("obj", object);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        tag_rl = (RelativeLayout) findViewById(R.id.tag_rl);
        start_price_et = (EditText) findViewById(R.id.start_price_et);
        step_price_et = (EditText) findViewById(R.id.step_price_et);
        min_price_et = (EditText) findViewById(R.id.min_price_et);
        max_person_et = (EditText) findViewById(R.id.max_person_et);
        baocun = (TextView) findViewById(R.id.baocun);
        tagsTv[0] = (TextView) findViewById(R.id.tag_one_tv);
        tagsTv[1] = (TextView) findViewById(R.id.tag_two_tv);
        tagsTv[2] = (TextView) findViewById(R.id.tag_three_tv);
        tagsTv[3] = (TextView) findViewById(R.id.tag_four_tv);
        tagsTv[4] = (TextView) findViewById(R.id.tag_five_tv);
        String[] tags = object.getTags().split(",");

        for (int i = 0; i < tags.length; i++) {
            tagsTv[i].setText(tags[i].replace("非常", ""));
            tagsTv[i].setVisibility(View.VISIBLE);
        }
        String startP = MainTools.getDoubleValue(object.getPrice(), 1) + "";
        String stepP = MainTools.getDoubleValue(object.getStep(), 1) + "";
        String miniP = MainTools.getDoubleValue(object.getMinspending(), 1) + "";
        String maxP = MainTools.getDoubleValue(object.getMaxnum(), 1) + "";
        start_price_et.setText(startP);
        step_price_et.setText(stepP);
        min_price_et.setText(miniP);
        max_person_et.setText(maxP);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_caipuedit;
    }

    @Override
    protected String initToolbarTitle() {
        return "编辑菜谱";
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
