package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dahuochifan.R;
import com.dahuochifan.ui.views.dialog.MorelAlertDialog;

import java.lang.ref.WeakReference;

public class TestActivity extends AppCompatActivity {
    private Button changeWarn, changeSuccess, changeError;
    private MorelAlertDialog dialog;
    private static final int CHANGEWARN = 2;
    private static final int CHANGESUCCESS = 1;
    private static final int CHANGEERROR = 0;
    private static final int CHANGEDISMISS = -1;

    static class MyHandler extends Handler {
        WeakReference<TestActivity> wrReferences;
        WeakReference<MorelAlertDialog> swReferences;

        MyHandler(TestActivity wrReferences, MorelAlertDialog dialog) {
            this.wrReferences = new WeakReference<>(wrReferences);
            this.swReferences = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGEERROR:
                    swReferences.get().changeAlertType(MorelAlertDialog.ERROR_TYPE);
                    this.sendEmptyMessageDelayed(CHANGEDISMISS, 2000);
                    break;
                case CHANGESUCCESS:
                    swReferences.get().changeAlertType(MorelAlertDialog.SUCCESS_TYPE);
                    this.sendEmptyMessageDelayed(CHANGEDISMISS, 2000);
                    break;
                case CHANGEWARN:
                    swReferences.get().changeAlertType(MorelAlertDialog.WARNING_TYPE);
                    this.sendEmptyMessageDelayed(CHANGEDISMISS, 2000);
                    break;
                case CHANGEDISMISS:
                    swReferences.get().dismissWithAnimation();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        changeWarn = (Button) findViewById(R.id.changeWarn);
        changeSuccess = (Button) findViewById(R.id.changeSuccess);
        changeError = (Button) findViewById(R.id.changeError);
        changeWarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new MorelAlertDialog(TestActivity.this);
                dialog.setTitleText("Loading...").getProgressHelper().setBarColor(ContextCompat.getColor(TestActivity.this, R.color.white));
                dialog.getProgressHelper().setBarWidth(3);
                MyHandler handler = new MyHandler(TestActivity.this, dialog);
                dialog.changeAlertType(MorelAlertDialog.PROGRESS_TYPE);
                dialog.show();
                handler.sendEmptyMessageDelayed(CHANGEWARN, 3000);
            }
        });
        changeSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new MorelAlertDialog(TestActivity.this);
                dialog.setTitleText("Loading...").getProgressHelper().setBarColor(ContextCompat.getColor(TestActivity.this, R.color.white));
                dialog.getProgressHelper().setBarWidth(1);
                MyHandler handler = new MyHandler(TestActivity.this, dialog);
                dialog.changeAlertType(MorelAlertDialog.PROGRESS_TYPE);
                dialog.show();
                handler.sendEmptyMessageDelayed(CHANGESUCCESS, 3000);
            }
        });
        changeError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new MorelAlertDialog(TestActivity.this);
                dialog.setTitleText("Loading...").getProgressHelper().setBarColor(ContextCompat.getColor(TestActivity.this, R.color.white));
                dialog.getProgressHelper().setBarWidth(1);
                MyHandler handler = new MyHandler(TestActivity.this, dialog);
                dialog.changeAlertType(MorelAlertDialog.PROGRESS_TYPE);
                dialog.show();
                handler.sendEmptyMessageDelayed(CHANGEERROR, 3000);
            }
        });
    }
}
