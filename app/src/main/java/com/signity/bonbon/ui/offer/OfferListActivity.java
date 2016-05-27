package com.signity.bonbon.ui.offer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.ga.GAConstant;
import com.signity.bonbon.ga.GATrackers;

/**
 * Created by rajesh on 9/12/15.
 */
public class OfferListActivity extends FragmentActivity implements View.OnClickListener {


    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_list_activity);
        Fragment fragment = OfferFragment.newInstance(this);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(this);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                AnimUtil.slideFromLeftAnim(OfferListActivity.this);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(OfferListActivity.this);
    }
}
