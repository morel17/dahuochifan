package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.dahuochifan.R;
import com.dahuochifan.utils.SystemBarTintManager;

public class ApplyAllActivity extends AppCompatActivity {
    private RelativeLayout back_rl;
    private View net_iv;
    private View phone_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_apply2);
        initViews();
        btn_Listener();
    }

    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(ContextCompat.getColor(ApplyAllActivity.this, R.color.black));
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintColor(ContextCompat.getColor(ApplyAllActivity.this, R.color.black));
        }
    }

    private void initViews() {
        back_rl = (RelativeLayout) findViewById(R.id.back_rl);
        net_iv = findViewById(R.id.net_iv);
        phone_iv = findViewById(R.id.phone_iv);
    }

    private void btn_Listener() {
        back_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        net_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplyAllActivity.this, ApplyActivity.class);
                startActivity(intent);
            }
        });
        phone_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + "4000960509");
                intent.setData(data);
                startActivity(intent);
            }
        });
    }
}
