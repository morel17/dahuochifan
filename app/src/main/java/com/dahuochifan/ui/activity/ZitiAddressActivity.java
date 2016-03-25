package com.dahuochifan.ui.activity;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.ui.views.ripple.RippleView;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class ZitiAddressActivity extends BaseActivity {
    @Bind(R.id.textInput)
    TextInputLayout inputLayout;
    @Bind(R.id.address_submit)
    RippleView nickname_submit;
    private EditText editText;
    private RequestParams params;
    private SweetAlertDialog pDialog;
    private Editor editor;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE4:
                    if (pDialog != null) {
                        pDialog.dismiss();
                    }
                    break;
                case MyConstant.MYHANDLER_CODE6:
                    if (pDialog != null) {
                        pDialog.dismiss();
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initEdit();
        btnListener();
    }

    private void init() {
        editor = baseSpf.edit();
    }

    private void initEdit() {
        editText = inputLayout.getEditText();
        editText.setText(MyConstant.user.getAddressinfo());
        editText.setHint("请输入自提地址");
        inputLayout.setHint("自提地址:");
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void btnListener() {
        nickname_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String editContent = editText.getText().toString();
                if (TextUtils.isEmpty(editContent)) {
                    MainTools.ShowToast(ZitiAddressActivity.this, "昵称不能为空");
                } else {
                    postAddress(editContent);
                }
            }
        });
    }

    private void postAddress(final String content) {
        baseClient.post(MyConstant.URL_UPDATEPROV, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pDialog = new SweetAlertDialog(ZitiAddressActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在提交修改");
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
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
                        SharedPreferenceUtil.initSharedPerence().putAddressInfo(editor, content);
                        MyConstant.user.setAddressinfo(content);
                        editor.commit();
                        handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE6, 1500);
                        EventBus.getDefault().post(new FirstEvent("zitiaddress"));
                    } else {
                        pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                pDialog.setTitleText("网络不给力").setConfirmText("确定").changeAlertType(SweetAlertDialog.ERROR_TYPE);
                handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_zitiaddress;
    }

    @Override
    protected String initToolbarTitle() {
        return "自提地址";
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
