package com.signity.bonbon.ui.offer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;

/**
 * Created by rajesh on 9/12/15.
 */
public class OfferViewActivity  extends FragmentActivity implements View.OnClickListener {


    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_view_activity);
        String offerName = getIntent().getStringExtra("offerName");
        ((TextView) findViewById(R.id.textTitle)).setText(offerName);
        Fragment fragment = OfferViewFragment.newInstance(this);
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
                AnimUtil.slideFromLeftAnim(OfferViewActivity.this);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(OfferViewActivity.this);
    }
}
