package com.signity.bonbon.ui.Delivery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.ui.fragment.DeliveryAddressFragment;
import com.signity.bonbon.ui.home.MainActivity;

/**
 * Created by root on 14/10/15.
 */
public class DeliveryActivity extends FragmentActivity {
    Button backButton;
    TextView textTitle;
    ImageButton homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.delivery_activity);

        backButton = (Button) findViewById(com.signity.bonbon.R.id.backButton);
        homeBtn=(ImageButton)findViewById(R.id.homeBtn);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String from = getIntent().getStringExtra(AppConstant.FROM);
        Fragment fragment = DeliveryAddressFragment.newInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.FROM, from);
        fragment.setArguments(bundle);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(DeliveryActivity.this);
    }
}
