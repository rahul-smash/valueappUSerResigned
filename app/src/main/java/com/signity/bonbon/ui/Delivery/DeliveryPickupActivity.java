package com.signity.bonbon.ui.Delivery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.ui.fragment.DeliveryPickupFragment;

/**
 * Created by root on 14/10/15.
 */
public class DeliveryPickupActivity extends FragmentActivity {
    Button backButton;
    TextView textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.delivery_activity);

        backButton = (Button) findViewById(com.signity.bonbon.R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.textTitle)).setText("Pick up locations");

        String from = getIntent().getStringExtra(AppConstant.FROM);
        Fragment fragment = DeliveryPickupFragment.newInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.FROM, from);
        fragment.setArguments(bundle);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .add(com.signity.bonbon.R.id.container, fragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(DeliveryPickupActivity.this);
    }
}
