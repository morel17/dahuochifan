package com.dahuochifan.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.bean.DhComments;
import com.dahuochifan.core.model.order.OrderInfo;
import com.dahuochifan.crop.MyBackgroundCommentActivity;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.adapter.CommentsGvAdapter;
import com.dahuochifan.ui.views.ActionSheet;
import com.dahuochifan.utils.GetFileSizeUtil;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class CommentsNewActivity extends BaseActivity implements ActionSheet.ActionSheetListener {
    private RatingBar myratingbar;
    private EditText comments_et;
    private TextView number_tv;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private GridView pic_gv;
    private List<DhComments> commentsList;
    private CommentsGvAdapter adapter;
    @Bind({R.id.delete_rl_one, R.id.delete_rl_two, R.id.delete_rl_three})
    List<PercentRelativeLayout> delete_rl;
    @Bind(R.id.delete_rl_all)
    PercentRelativeLayout delete_rl_all;
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

    private boolean canCom;
    private OrderInfo obj;

    private SweetAlertDialog pDialog;

    private Handler mHander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    pDialog.dismissWithAnimation();
                    break;
                case 1:
                    pDialog.dismissWithAnimation();
                    finish();
                    break;
                case 2:
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
        initViews();
        btnListener();
    }

    private void initViews() {
        ButterKnife.bind(this);
        obj = (OrderInfo) getIntent().getSerializableExtra("obj");
        myratingbar = (RatingBar) findViewById(R.id.myratingbar);
        comments_et = (EditText) findViewById(R.id.comments_et);
        number_tv = (TextView) findViewById(R.id.number_tv);
        pic_gv = (GridView) findViewById(R.id.pic_gv);
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        commentsList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            DhComments commentObj = new DhComments();
            commentObj.setImgPath("");
            commentObj.setImgPathBig("");
            commentsList.add(commentObj);
        }
        adapter = new CommentsGvAdapter(this);
        adapter.setmObjects(commentsList);
        pic_gv.setAdapter(adapter);

        photo_dir = GetFileSizeUtil.getMainSD(this) + "/dahuochifan/chifanimg";
        canCom = true;

    }

    private void btnListener() {
        comments_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    number_tv.setVisibility(View.VISIBLE);
                    number_tv.setText(s.length() + "/120");
                } else {
                    number_tv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        pic_gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                DhComments commentsObj = commentsList.get(position);
//                if (TextUtils.isEmpty(commentsObj.getImgPath())) {
//                    //当前无图，执行添加
//                    addPic();
//                } else {
//                    commentsList.remove(position);
//                    DhComments dhTemp = new DhComments();
//                    dhTemp.setImgPathBig("");
//                    dhTemp.setImgPath("");
//                    commentsList.add(dhTemp);
//                    adapter.notifyDataSetChanged();
//                }
//                return true;
//            }
//        });
        delete_rl.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DhComments commentsObj = commentsList.get(0);
                if (TextUtils.isEmpty(commentsObj.getImgPath())) {
//                    //当前无图，执行添加
//                    addPic();
                } else {
                    commentsList.remove(0);
                    DhComments dhTemp = new DhComments();
                    dhTemp.setImgPathBig("");
                    dhTemp.setImgPath("");
                    commentsList.add(dhTemp);
                    adapter.notifyDataSetChanged();
                    for(int i=0;i<commentsList.size();i++){
                        if(!TextUtils.isEmpty(commentsList.get(i).getImgPath())){
                            delete_rl.get(i).setVisibility(View.VISIBLE);
                        }else{
                            delete_rl.get(i).setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        delete_rl.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DhComments commentsObj = commentsList.get(1);
                if (TextUtils.isEmpty(commentsObj.getImgPath())) {
                    //当前无图，执行添加
                    addPic();
                } else {
                    commentsList.remove(1);
                    DhComments dhTemp = new DhComments();
                    dhTemp.setImgPathBig("");
                    dhTemp.setImgPath("");
                    commentsList.add(dhTemp);
                    adapter.notifyDataSetChanged();
                    for(int i=0;i<commentsList.size();i++){
                        if(!TextUtils.isEmpty(commentsList.get(i).getImgPath())){
                            delete_rl.get(i).setVisibility(View.VISIBLE);
                        }else{
                            delete_rl.get(i).setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        delete_rl.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DhComments commentsObj = commentsList.get(2);
                if (TextUtils.isEmpty(commentsObj.getImgPath())) {
                    //当前无图，执行添加
                    addPic();
                } else {
                    commentsList.remove(2);
                    DhComments dhTemp = new DhComments();
                    dhTemp.setImgPathBig("");
                    dhTemp.setImgPath("");
                    commentsList.add(dhTemp);
                    adapter.notifyDataSetChanged();
                    for(int i=0;i<commentsList.size();i++){
                        if(!TextUtils.isEmpty(commentsList.get(i).getImgPath())){
                            delete_rl.get(i).setVisibility(View.VISIBLE);
                        }else{
                            delete_rl.get(i).setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        pic_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DhComments commentsObj = commentsList.get(position);
                if (TextUtils.isEmpty(commentsObj.getImgPath())) {
                    //当前无图，执行添加
                    addPic();
                } else {
                    //当前有图，执行预览
                    Intent intent = new Intent(CommentsNewActivity.this, PhotoViewVPCommentActivity.class);
                    ArrayList<String> listStr = new ArrayList<String>();
                    for (int i = 0; i < commentsList.size(); i++) {
                        if (!TextUtils.isEmpty(commentsList.get(i).getImgPathBig())) {
                            listStr.add(commentsList.get(i).getImgPathBig());
                        }
                    }
                    intent.putExtra("index",position);
                    intent.putStringArrayListExtra("imglist", listStr);
                    startActivity(intent);
                }
            }
        });
    }

    private void postComment(String commentStr, float startNum) {
        params = ParamData.getInstance().postCommentObj(obj.getCbids(), commentStr, startNum * 2 + "", obj.getOlids());
        for (int i = 0; i < commentsList.size(); i++) {
            if (!TextUtils.isEmpty(commentsList.get(i).getImgPath())) {
                File file = new File(commentsList.get(i).getImgPath());
                try {
                    params.put("pic" + (i + 1), file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        client.post(MyConstant.URL_POSTCOMMENT, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                canCom = false;
                pDialog = new SweetAlertDialog(CommentsNewActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在提交评论");
                pDialog.setCancelable(false);
                pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    int resultcode = jobj.getInt("resultcode");
                    String tag = jobj.getString("tag");
                    if (resultcode == 1) {
                        if (OrderDetailActivity.instance != null) {
                            OrderDetailActivity.instance.finish();
                        }
                        pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        EventBus.getDefault().post(new FirstEvent("MyOrder"));
                        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_TWO));
                        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_THREE));
                        mHander.sendEmptyMessageDelayed(1, 1500);
                    } else {
                        if (!TextUtils.isEmpty(tag)) {
                            showTipDialog(CommentsNewActivity.this, tag, resultcode);
                        } else {
                            showTipDialog(CommentsNewActivity.this, "重新登录", resultcode);
                        }
                        pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pDialog.setTitleText("网络不给力").setConfirmText("确定").changeAlertType(SweetAlertDialog.ERROR_TYPE);
                mHander.sendEmptyMessageDelayed(0, 1500);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                canCom = true;
            }
        });
    }

    private void addPic() {
        if (TextUtils.isEmpty(photo_dir)) {
            MainTools.ShowToast(CommentsNewActivity.this, "存储卡不存在");
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

    public void showActionSheet() {
        ActionSheet.createBuilder(this, getSupportFragmentManager()).setCancelButtonTitle("取消").setOtherButtonTitles("拍照", "从相册选择")
                .setCancelableOnTouchOutside(true).setListener(this).show();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_comments_new;
    }

    @Override
    protected String initToolbarTitle() {
        return "评论";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return super.onOptionsItemSelected(item);
            case R.id.confirm:
                if (canCom) {
                    String commentStr = comments_et.getText().toString();
                    float startNum = myratingbar.getRating();
                    if (TextUtils.isEmpty(commentStr)) {
                        MainTools.ShowToast(CommentsNewActivity.this, "评论内容不能为空");
                    } else {
                        postComment(commentStr, startNum);
                    }
                } else {
                    MainTools.ShowToast(CommentsNewActivity.this, "请勿重复评论");
                }
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    Intent intent = new Intent(this, MyBackgroundCommentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "A");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                } catch (ActivityNotFoundException e) {
                    MainTools.ShowToast(CommentsNewActivity.this, "没有找到照片");
                }
                break;
            default:
                break;
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
            MainTools.ShowToast(this, "没有可用的存储卡");
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
            MainTools.ShowToast(this, "未找到系统相机程序");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case CAMERA_WITH_DATA:
                if (BuildConfig.LEO_DEBUG)
                    L.e("mCurrentPhotoFile===" + mCurrentPhotoFile.getAbsolutePath() + "***" + mCurrentPhotoFile.getPath());
                // 拍照
                String currentFilePath2 = mCurrentPhotoFile.getPath();
//                Intent intent2 = new Intent(this, ClipPictureCommentActivity.class);
//                intent2.putExtra("PATH", currentFilePath2);
//                intent2.putExtra("type", takephotetype);
//                startActivityForResult(intent2, CAMERA_CROP_DATA);
                for (int i = 0; i < commentsList.size(); i++) {
                    if (TextUtils.isEmpty(commentsList.get(i).getImgPath())) {
                        commentsList.get(i).setImgPath(MainTools.getSmallBitmap(currentFilePath2, this));
                        commentsList.get(i).setImgPathBig(currentFilePath2);
                        adapter.notifyDataSetChanged();
                        delete_rl.get(i).setVisibility(View.VISIBLE);
                        break;
                    } else {
                        delete_rl.get(i).setVisibility(View.VISIBLE);
                    }
                }
                break;
            case PHOTO_PICKED_WITH_DATA:
                if (data != null) {
                    ArrayList<String> photoList = new ArrayList<>();
                    photoList = data.getStringArrayListExtra("PATH");
                    if (photoList != null && photoList.size() > 0) {
                        for (int i = 0; i < photoList.size(); i++) {
                            if (!TextUtils.isEmpty(photoList.get(i))) {
                                for (int j = 0; j < commentsList.size(); j++) {
                                    if (TextUtils.isEmpty(commentsList.get(j).getImgPath())) {
                                        commentsList.get(j).setImgPath(MainTools.getSmallBitmap(photoList.get(i), this));
                                        commentsList.get(j).setImgPathBig(photoList.get(i));
                                        delete_rl.get(j).setVisibility(View.VISIBLE);
                                        break;
                                    } else {
                                        delete_rl.get(j).setVisibility(View.VISIBLE);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        MainTools.ShowToast(this, "图片加载失败");
                    }
                } else {
                    MainTools.ShowToast(this, "未在存储卡中找到这个文件");
                }
                break;
            default:
                break;
        }
    }

    private BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
