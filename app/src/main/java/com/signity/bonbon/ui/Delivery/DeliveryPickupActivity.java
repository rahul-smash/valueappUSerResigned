package com.signity.bonbon.ui.Delivery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.model.PickupAdressModel;
import com.signity.bonbon.ui.fragment.DeliveryPickupFragment;
import com.signity.bonbon.ui.fragment.PickupDetailFragment;
import com.signity.bonbon.ui.home.MainActivity;

/**
 * Created by root on 14/10/15.
 */
public class DeliveryPickupActivity extends FragmentActivity {
    Button backButton;
    TextView textTitle;
    ImageButton homeBtn;
    String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.delivery_activity);

        backButton = (Button) findViewById(com.signity.bonbon.R.id.backButton);
        homeBtn = (ImageButton) findViewById(com.signity.bonbon.R.id.homeBtn);
        homeBtn.setVisibility(View.GONE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.textTitle)).setText("Place Order");

        from = getIntent().getStringExtra(AppConstant.FROM);

        if(from.equalsIgnoreCase("shop_cart")){
            Fragment fragment = DeliveryPickupFragment.newInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.FROM, from);
            fragment.setArguments(bundle);
            if (savedInstanceState == null)
                getSupportFragmentManager().beginTransaction()
                        .add(com.signity.bonbon.R.id.container, fragment).commit();
        }
        else {
            Fragment fragment = PickupDetailFragment.newInstance(this);
            PickupAdressModel pickupAddressModel=DataAdapter.getInstance().getPickupAdressModel();
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.FROM, from);
            bundle.putSerializable("object", pickupAddressModel);
            fragment.setArguments(bundle);
            if (savedInstanceState == null)
                getSupportFragmentManager().beginTransaction()
                        .add(com.signity.bonbon.R.id.container, fragment).commit();
        }


    }

    public void disableBackButton() {
        backButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(from.equalsIgnoreCase("shop_cart")){
            AnimUtil.slideFromLeftAnim(DeliveryPickupActivity.this);
        }
        else {
            DataAdapter.getInstance().setPickupAdressModel(null);
            Intent intent_home = new Intent(DeliveryPickupActivity.this, MainActivity.class);
            intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_home);
            this.finish();
        }
    }
}
