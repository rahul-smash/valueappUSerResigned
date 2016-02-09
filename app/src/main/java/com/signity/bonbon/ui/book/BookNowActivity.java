package com.signity.bonbon.ui.book;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.ui.fragment.BookNowFragment;

/**
 * Created by root on 14/10/15.
 */
public class BookNowActivity extends FragmentActivity {
    Button backButton;
    TextView textTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_now_activity);

        backButton = (Button) findViewById(com.signity.bonbon.R.id.backButton);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        String from = getIntent().getStringExtra(AppConstant.FROM);
        Fragment fragment = new BookNowFragment();
        Bundle bundle = new Bundle();
        if (from != null && !from.isEmpty()) {
            bundle.putString(AppConstant.FROM, from);
        }
        fragment.setArguments(bundle);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .add(com.signity.bonbon.R.id.container, fragment).commit();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(BookNowActivity.this);
    }
}
