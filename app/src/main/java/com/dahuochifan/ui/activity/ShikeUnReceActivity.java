package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.dahuochifan.R;
import com.dahuochifan.ui.fragment.MyShikeFragmentUnRece;

public class ShikeUnReceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        MyShikeFragmentUnRece settingFragment = new MyShikeFragmentUnRece();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, settingFragment).commit();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_shike_unrece;
    }

    @Override
    protected String initToolbarTitle() {
        return "取消/已拒订单";
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
