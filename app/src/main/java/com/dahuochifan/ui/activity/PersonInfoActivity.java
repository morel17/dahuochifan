package com.dahuochifan.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.crop.MyBackgroundActivity;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.liandong.activity.AgeWheelActivity;
import com.dahuochifan.model.userinfo.PersonInfoDetail;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.PersonInfoData;
import com.dahuochifan.ui.views.ActionSheet;
import com.dahuochifan.ui.views.ActionSheet.ActionSheetListener;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.ui.views.MyNickNameDialog;
import com.dahuochifan.ui.views.dialog.MorelDialog;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.CustomerHead;
import com.dahuochifan.utils.GetFileSizeUtil;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class PersonInfoActivity extends BaseActivity implements ActionSheetListener {
    private CircleImageView nickname_iv;
    private TextView nickname_tv;
    private RelativeLayout nickname_rl, age_rl, ziti_rl, contact_rl, head_rl, tag_rl;
    private TextView username_tv;
    private boolean canage;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private SharedPreferences spf;
    private Editor editor;
    private Context context;
    private MyNickNameDialog dialog;
    private SweetAlertDialog pDialog;
    private TextView age_tv;
    private MorelDialog mDialog;
    private TextView ziti_tv;
    private TextView phone_tv;
    private Gson gson;

    // 初始化图片保存路径
    private String photo_dir;
    /* 拍照的照片存储位置 */
    private File PHOTO_DIR = null;
    // 照相机拍照得到的图片
    private String mFileName;
    private String takephotetype;
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    /* 用来标识请求裁剪图片后的activity */
    private static final int CAMERA_CROP_DATA = 3022;
    private String fileName;
    private File file;

    private File mCurrentPhotoFile;
    private RelativeLayout story_rl;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE4:
                    if (pDialog != null) {
                        if (BuildConfig.LEO_DEBUG)
                            pDialog.dismiss();
                    }
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
        EventBus.getDefault().register(this);
        initViews();
        listener();
        getUserInfo();
    }

    /**
     * 获取用户的个人信息
     */
    private void getUserInfo() {
        params = ParamData.getInstance().getPersonObj(MyConstant.user.getRole());
        client.setTimeout(2000);
        client.post(MyConstant.URL_GETPERSONINFO, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                initViewStatus();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                L.e("responseString===" + responseString);
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    if (jobj.getInt("resultcode") == 1) {
                        PersonInfoData persondata = new PersonInfoData();
                        persondata.dealData2(responseString, PersonInfoActivity.this, gson);
                        PersonInfoDetail info = persondata.getPersonInfoDetail();
                        MyConstant.user.setAvatar(info.getAvatar());
                        MyConstant.user.setNickname(info.getNickname());
                        MyConstant.user.setUsername(info.getUsername());
                        MyConstant.user.setMobile(info.getMobile());
                        MyConstant.user.setAddressinfo(info.getAddressinfo());
                        MyConstant.user.setAge(info.getAge());
                        initViewStatus();
                    } else {
                        if (!TextUtils.isEmpty(jobj.getString("tag"))) {
                            showTipDialog(PersonInfoActivity.this, jobj.getString("tag"), jobj.getInt("resultcode"));
                        } else {
                            showTipDialog(PersonInfoActivity.this, "重新登录", jobj.getInt("resultcode"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置布局属性
     */
    private void initViewStatus() {
        if (MyConstant.user.getRole() == 1) {
            CustomerHead.loadImage(MyConstant.user.getAvatar(), nickname_iv);
        } else if (MyConstant.user.getRole() == 2) {
            CookerHead.loadImage(MyConstant.user.getAvatar(), nickname_iv);
            story_rl.setVisibility(View.VISIBLE);
        } else {
            CustomerHead.loadImage(MyConstant.user.getAvatar(), nickname_iv);
        }
        nickname_tv.setText(MyConstant.user.getNickname());
        if (MyConstant.user.getUsername().length() > 11) {
            username_tv.setText("微信用户");
        } else {
            username_tv.setText(MyConstant.user.getUsername());
        }
        phone_tv.setText(MyConstant.user.getMobile());
        ziti_tv.setText(MyConstant.user.getAddressinfo());
        age_tv.setText(MyConstant.user.getAge());
        if (MyConstant.user.getRole() != 2) {
            contact_rl.setVisibility(View.GONE);
            ziti_rl.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        context = this;
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();
        canage = true;
        spf = getSharedPreferences(MyConstant.APP_SPF_NAME, MODE_PRIVATE);
        editor = spf.edit();
        nickname_iv = (CircleImageView) findViewById(R.id.nickname_iv);
        nickname_tv = (TextView) findViewById(R.id.nickname_tv);
        username_tv = (TextView) findViewById(R.id.username_tv);
        nickname_rl = (RelativeLayout) findViewById(R.id.nickname_rl);
        ziti_rl = (RelativeLayout) findViewById(R.id.ziti_rl);
        age_rl = (RelativeLayout) findViewById(R.id.age_rl);
        age_tv = (TextView) findViewById(R.id.age_tv);
        contact_rl = (RelativeLayout) findViewById(R.id.contact_rl);
        ziti_tv = (TextView) findViewById(R.id.ziti_tv);
        phone_tv = (TextView) findViewById(R.id.phone_tv);
        head_rl = (RelativeLayout) findViewById(R.id.head_rl);
        story_rl = (RelativeLayout) findViewById(R.id.story_rl);
        tag_rl = (RelativeLayout) findViewById(R.id.tag_rl);

        photo_dir = GetFileSizeUtil.getMainSD(context) + "/dahuochifan/chifanimg";
    }

    public void showActionSheet() {
        if (!TextUtils.isEmpty(MyConstant.user.getAvatar())) {
            ActionSheet.createBuilder(this, getSupportFragmentManager()).setCancelButtonTitle("取消").setOtherButtonTitles("拍照", "从相册选择", "查看头像")
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        } else {
            ActionSheet.createBuilder(this, getSupportFragmentManager()).setCancelButtonTitle("取消").setOtherButtonTitles("拍照", "从相册选择")
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }
    }

    private void listener() {

        story_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonInfoActivity.this, StoryActivity.class));
            }
        });
        head_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(photo_dir)) {
                    MainTools.ShowToast(context, "存储卡不存在");
                    return;
                } else {
                    PHOTO_DIR = new File(photo_dir);
                    if (!PHOTO_DIR.exists()) {
                        PHOTO_DIR.mkdirs();
                    }
                }
                setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet();
            }
        });
        nickname_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonInfoActivity.this, NicknameActivity.class));
            }
        });
        age_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (canage) {
                    canage = false;
                    Intent intent = new Intent(PersonInfoActivity.this, AgeWheelActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, 0);
                }

            }
        });
        contact_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContactActivity.class);
                startActivityForResult(intent, MyConstant.REQUESTCODEQ_MOBI);
            }
        });
        tag_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LeftMainGridActivity2.class));
            }
        });
        ziti_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonInfoActivity.this, ZitiAddressActivity.class));
            }
        });
    }

    private void updateFunction(final String content, final MorelDialog mDialog2, final TextView phone_tv2, final String type) {
        client.post(MyConstant.URL_UPDATEPROV, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在提交修改");
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
                        if (type.equals("phone")) {
                            SharedPreferenceUtil.initSharedPerence().putMobile(editor, content);
                            MyConstant.user.setMobile(content);
                            editor.commit();
                        } else {
                            SharedPreferenceUtil.initSharedPerence().putAddressInfo(editor, content);
                            MyConstant.user.setAddressinfo(content);
                            editor.commit();
                        }

                        phone_tv2.setText(content);
                        handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
                    } else {
                        MainTools.ShowToast(PersonInfoActivity.this, tag);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case MyConstant.REQUESTCODEQ_MOBI:
                if (MyConstant.user.getRole() == 2) {
                    if (!TextUtils.isEmpty(data.getExtras().getString("mobile"))) {
                        phone_tv.setText(data.getExtras().getString("mobile"));
                    }
                }
                break;
            case PHOTO_PICKED_WITH_DATA:
                if (data != null) {
                    // 选择
                    String currentFilePath = data.getStringExtra("PATH");
                    String currentType = data.getStringExtra("type");
                    if (!TextUtils.isEmpty(currentFilePath)) {
                        Intent intent1 = new Intent(context, ClipPictureActivity.class);
                        intent1.putExtra("PATH", currentFilePath);
                        intent1.putExtra("type", currentType);
                        startActivityForResult(intent1, CAMERA_CROP_DATA);
                    } else {
                        // showShortToast("未在存储卡中找到这个文件");
                        MainTools.ShowToast(context, "未在存储卡中找到这个文件");
                    }
                }
                break;
            case CAMERA_WITH_DATA:
                if (BuildConfig.LEO_DEBUG)
                    L.e("mCurrentPhotoFile===" + mCurrentPhotoFile.getAbsolutePath() + "***" + mCurrentPhotoFile.getPath());
                // 拍照
                String currentFilePath2 = mCurrentPhotoFile.getPath();
                Intent intent2 = new Intent(context, ClipPictureActivity.class);
                intent2.putExtra("PATH", currentFilePath2);
                intent2.putExtra("type", takephotetype);
                startActivityForResult(intent2, CAMERA_CROP_DATA);

                break;
            case CAMERA_CROP_DATA:
                if (data != null) {
                    // 裁剪
                    String path = data.getStringExtra("PATH");
                    final String currenttype = data.getStringExtra("type");
                    fileName = data.getStringExtra("NAME");
                    file = new File(path);
                    // LoaderHead.loadImage("file://" + path, head_img);
                    // nickname_iv.setBackgroundResource(resid);
                    CustomerHead.loadImage("file://" + path, nickname_iv);
                    if (BuildConfig.LEO_DEBUG)
                        L.e("file" + file.length());
                    uploadFile(MyConstant.URL_AVATAR, file);
                }
                break;
            default:
                break;
        }

    }

    public void onEventMainThread(FirstEvent event) {
        if (event != null && !TextUtils.isEmpty(event.getMsg())) {
            if (event.getMsg().equals("nickname")) {
                nickname_tv.setText(MyConstant.user.getNickname());
            } else if (event.getMsg().equals("zitiaddress")) {
                ziti_tv.setText(MyConstant.user.getAddressinfo());
            }
        }
        if (event != null) {
            if (event.getType() == MyConstant.USER_AGE) {
                if (!TextUtils.isEmpty(event.getMsg())) {
                    String age = event.getMsg();
                    SharedPreferenceUtil.initSharedPerence().putAge(editor, age);
                    editor.commit();
                    MyConstant.user.setAge(age);
                    age_tv.setText(age);
                }
                canage = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nickname_tv.setText(MyConstant.user.getNickname());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (pDialog != null) {
            pDialog.dismiss();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                doPickPhotoAction();
                break;
            case 1:
                try {
                    Intent intent = new Intent(context, MyBackgroundActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "A");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                } catch (ActivityNotFoundException e) {
                    MainTools.ShowToast(context, "没有找到照片");
                }
                break;
            case 2:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(PersonInfoActivity.this, SimpleViewActivity.class);
                        int[] startLocation = new int[2];
                        nickname_iv.getLocationOnScreen(startLocation);
                        startLocation[0] += nickname_iv.getMeasuredWidth() / 2;
                        intent.putExtra("imgpath", MyConstant.user.getAvatar());
                        intent.putExtra("location", startLocation);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }, 200);
                break;
            default:
                break;
        }
    }

    /**
     * @param file 要上传的文件路径
     * @param url  服务端接收URL
     * @throws Exception
     */
    public void uploadFile(String url, final File file) {
        if (file.exists() && file.length() > 0) {
            RequestParams params = new RequestParams();
            params = ParamData.getInstance().avatarObj();
            try {
                if (BuildConfig.LEO_DEBUG)
                    L.e("file" + file.getAbsolutePath() + "**" + file.length());
                params.put("avatar", file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // 上传文件
            client.post(url, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在上传头像");
                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
                    pDialog.setConfirmText("").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.show();
                }

                @Override
                public void onSuccess(int arg0, Header[] arg1, String arg2) {
                    if (BuildConfig.LEO_DEBUG)
                        L.e("arg2========" + arg2);
                    try {
                        JSONObject jobj = new JSONObject(arg2);
                        String tag = jobj.getString("tag");
                        int resultcode = jobj.getInt("resultcode");
                        String obj = jobj.getString("obj");
                        if (resultcode == 1) {
                            MyConstant.user.setAvatar(obj);

                            SharedPreferenceUtil.initSharedPerence().putAvatar(editor, obj);
                            if (BuildConfig.LEO_DEBUG)
                                L.e("obj" + obj);
                            editor.commit();
                            EventBus.getDefault().post(new FirstEvent("avatar"));
                            pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
                        } else {
                            pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
                            MainTools.ShowToast(PersonInfoActivity.this, "上传失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                    MainTools.ShowToast(context, "网络异常");
                    pDialog.setTitleText("网络不给力").setConfirmText("确定").changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (file.exists()) {
                        file.delete();
                    }
                    if (mCurrentPhotoFile != null && mCurrentPhotoFile.exists()) {
                        mCurrentPhotoFile.delete();
                    }
                    handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
                }
            });
        } else {
        }
    }

    /**
     * 描述：从照相机获取
     *
     * @param
     */
    private void doPickPhotoAction() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            doTakePhoto();
        } else {
            MainTools.ShowToast(context, "没有可用的存储卡");
        }
    }

    /**
     * 拍照获取图片
     *
     * @param
     */
    protected void doTakePhoto() {
        try {
            mFileName = System.currentTimeMillis() + ".jpg";
            mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (Exception e) {
            MainTools.ShowToast(context, "未找到系统相机程序");
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_personinfo;
    }

    @Override
    protected String initToolbarTitle() {
        return "个人信息";
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
