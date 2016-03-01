package com.signity.bonbon.ui.order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.signity.bonbon.Utilities.AnimUtil;

/**
 * Created by root on 21/10/15.
 */
public class OrderListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.order_list_activity);

        Fragment fragment = OrderHistory.newInstance(this);
        Bundle bundle = getIntent().getExtras();
        fragment.setArguments(bundle);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .add(com.signity.bonbon.R.id.container, fragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(OrderListActivity.this);
    }
}
