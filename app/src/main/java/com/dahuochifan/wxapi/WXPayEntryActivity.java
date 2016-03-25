package com.dahuochifan.wxapi;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.ui.views.dialog.MorelTipsDialog;
import com.nostra13.universalimageloader.utils.L;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.sourceforge.simcpux.Constants;

import de.greenrobot.event.EventBus;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;
	private AlertDialog dialog;
	private MorelTipsDialog mdialog;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1 :
					EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
					break;
				default :
					break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);
		Log.e("dy", "dy1");
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		Log.e("code#######", "dy1");
		Log.e("code#######", req.toString() + "*****" + req.getType());
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			final int code = resp.errCode;
			String msg = "";
			switch (code) {
				case 0 :
					msg = "支付成功！";
					L.e(msg);
					break;
				case -1 :
					msg = "支付失败！";
					L.e(msg);
					break;
				case -2 :
					msg = "您取消了支付！";
					L.e(msg);
					break;

				default :
					msg = "支付失败！!";
					L.e(msg);
					break;
			}
			EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_PAY, code));
			Log.e("code#######x", resp.toString() + "*****" + resp.getType());
			dialog=new AlertDialog.Builder(this).setCancelable(false).setTitle(getResources().getString(R.string.app_tip)).setMessage(msg)
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							WXPayEntryActivity.this.finish();
//							EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_PAY, code));
							handler.sendEmptyMessageDelayed(1, 3000);
						}
					}).create();

			dialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		if(dialog!=null){
			dialog.dismiss();
		}
		super.onDestroy();
	}
}